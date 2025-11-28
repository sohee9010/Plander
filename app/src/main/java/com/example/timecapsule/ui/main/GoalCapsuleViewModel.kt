package com.example.timecapsule.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.timecapsule.data.local.AppDatabase
import com.example.timecapsule.data.model.ChecklistItem
import com.example.timecapsule.data.model.DailyRecord
import com.example.timecapsule.data.model.GoalAnswer
import com.example.timecapsule.data.model.GoalCapsule
import com.example.timecapsule.data.model.GoalStatus
import com.example.timecapsule.data.repository.ChecklistItemRepository
import com.example.timecapsule.data.repository.DailyRecordRepository
import com.example.timecapsule.data.repository.GoalAnswerRepository
import com.example.timecapsule.data.repository.GoalCapsuleRepository
import com.example.timecapsule.util.SingleLiveEvent
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit

class GoalCapsuleViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val goalRepository = GoalCapsuleRepository(database.goalCapsuleDao())
    private val checklistRepository = ChecklistItemRepository(database.checklistItemDao())
    private val answerRepository = GoalAnswerRepository(database.goalAnswerDao())
    private val dailyRecordRepository = DailyRecordRepository(database.dailyRecordDao())

    val activeGoalCapsules: LiveData<List<GoalCapsule>> = goalRepository.getActiveGoalCapsules()
    val gaveUpGoalCapsules: LiveData<List<GoalCapsule>> = goalRepository.getGaveUpGoals()
    val archivedGoalCapsules: LiveData<List<GoalCapsule>> = goalRepository.getArchivedGoalCapsules()

    val congratulationEvent = SingleLiveEvent<GoalCapsule>()

    private val _visibleDateRange = MutableLiveData<Pair<LocalDate, LocalDate>>()
    val monthlyRecords: LiveData<List<DailyRecord>> = _visibleDateRange.switchMap { (start, end) ->
        dailyRecordRepository.getRecordsBetweenDates(start, end)
    }
    private val _selectedDate = MutableLiveData<LocalDate>()

    private val recordForSelectedDate: LiveData<List<DailyRecord>> = _selectedDate.switchMap { date ->
        dailyRecordRepository.getRecordsForDate(date)
    }

    val checklistDataForUi = MediatorLiveData<Pair<List<ChecklistItem>, Set<String>>>().apply {
        value = Pair(emptyList(), emptySet())
        val update = { ->
            viewModelScope.launch {
                val date = _selectedDate.value
                val goals = activeGoalCapsules.value
                val records = recordForSelectedDate.value
                if (date != null && goals != null) {
                    val goalsForDate = goals.filter { !date.isBefore(it.startDate) && !date.isAfter(it.targetDate) }
                    val checklistItems = if (goalsForDate.isNotEmpty()) {
                        checklistRepository.getChecklistsByGoalIds(goalsForDate.map { it.id })
                    } else {
                        emptyList()
                    }
                    val completedIds = records?.flatMap { it.completedChecklistIds }?.toSet() ?: emptySet()
                    postValue(Pair(checklistItems, completedIds))
                }
            }
        }
        addSource(activeGoalCapsules) { update() }
        addSource(_selectedDate) { update() }
        addSource(recordForSelectedDate) { update() }
    }

    private val _selectedGoalId = MutableLiveData<String>()
    val selectedGoal: LiveData<GoalCapsule?> = _selectedGoalId.switchMap { id ->
        goalRepository.getGoalCapsuleByIdLiveData(id)
    }

    private val recordsForSelectedGoal: LiveData<List<DailyRecord>> = _selectedGoalId.switchMap { id ->
        dailyRecordRepository.getRecordsForGoal(id)
    }

    val practiceRate: LiveData<Float> = MediatorLiveData<Float>().apply {
        value = 0f
        val update = { ->
            val goal = selectedGoal.value
            val records = recordsForSelectedGoal.value

            if (goal != null && records != null) {
                val elapsedDays = ChronoUnit.DAYS.between(goal.startDate, LocalDate.now()).toInt().coerceAtLeast(1)
                val practiceDays = records.count { it.completedChecklistIds.isNotEmpty() }
                val rate = (practiceDays.toFloat() / elapsedDays.toFloat()) * 100
                postValue(rate.coerceAtMost(100f))
            }
        }
        addSource(selectedGoal) { update() }
        addSource(recordsForSelectedGoal) { update() }
    }

    init {
        autoCompleteGoals()
        checkForDDayGoals()
    }

    private fun checkForDDayGoals() = viewModelScope.launch {
        val today = LocalDate.now()
        val dDayGoals = goalRepository.getDDayGoals(today)
        dDayGoals.forEach { goal ->
            if (!goal.congratulated) {
                congratulationEvent.postValue(goal)
            }
        }
    }

    fun markAsCongratulated(goal: GoalCapsule) = viewModelScope.launch {
        goalRepository.updateGoalCapsule(goal.copy(congratulated = true))
    }

    fun setGoalId(goalId: String) {
        _selectedGoalId.value = goalId
    }

    fun updateGoalStatus(id: String, status: GoalStatus) = viewModelScope.launch {
        goalRepository.updateStatus(id, status)
    }

    fun setVisibleDateRange(startDate: LocalDate, endDate: LocalDate) {
        _visibleDateRange.value = Pair(startDate, endDate)
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun updateChecklistState(checklistItemId: String, date: LocalDate, isCompleted: Boolean) = viewModelScope.launch {
        val item = checklistRepository.getChecklistItemById(checklistItemId) ?: return@launch
        val goalId = item.goalCapsuleId

        val record = dailyRecordRepository.getRecordForDateAndGoal(date, goalId)
            ?: DailyRecord(date = date, goalCapsuleId = goalId)

        val currentIds = record.completedChecklistIds.toMutableSet()
        if (isCompleted) {
            currentIds.add(checklistItemId)
        } else {
            currentIds.remove(checklistItemId)
        }

        dailyRecordRepository.upsertRecord(record.copy(completedChecklistIds = currentIds.toList()))
    }

    private fun autoCompleteGoals() = viewModelScope.launch {
        goalRepository.autoCompleteOverdueGoals()
    }

    fun insertGoalCapsuleWithChecklistAndAnswers(
        goalCapsule: GoalCapsule,
        checklistContents: List<String>,
        answers: List<GoalAnswer>
    ) = viewModelScope.launch {
        goalRepository.insertGoalCapsule(goalCapsule)
        val checklistItems = checklistContents.mapIndexed { index, content ->
            ChecklistItem(goalCapsuleId = goalCapsule.id, content = content, orderIndex = index)
        }
        checklistRepository.insertChecklistItems(checklistItems)
        answerRepository.insertAnswers(answers)
    }

    fun deleteGoal(goalId: String) = viewModelScope.launch {
        val goal = goalRepository.getGoalCapsuleById(goalId)
        goal?.let {
            goalRepository.deleteGoalCapsule(it)
            checklistRepository.deleteChecklistByGoalId(goalId)
        }
    }
}