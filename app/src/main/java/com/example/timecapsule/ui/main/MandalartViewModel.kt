package com.example.timecapsule.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.timecapsule.data.local.AppDatabase
import com.example.timecapsule.data.model.GoalCapsule
import com.example.timecapsule.data.model.MandalartSubGoal
import com.example.timecapsule.data.repository.GoalCapsuleRepository
import com.example.timecapsule.data.repository.MandalartSubGoalRepository
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class MandalartViewModel(application: Application) : AndroidViewModel(application) {

    private val goalRepository: GoalCapsuleRepository
    private val subGoalRepository: MandalartSubGoalRepository

    private val _goals = MutableList(8) { i -> MutableLiveData<String>().apply { value = "목표 ${i + 1}" } }
    val goals: List<LiveData<String>> = _goals

    private val _finalGoal = MutableLiveData<String>()
    val finalGoal: LiveData<String> = _finalGoal

    private val _year = MutableLiveData<String>()
    val year: LiveData<String> = _year

    private val _saveMandalartImage = MutableLiveData<Boolean>()
    val saveMandalartImage: LiveData<Boolean> = _saveMandalartImage

    private var currentGoalId: String? = null

    init {
        val db = AppDatabase.getDatabase(application)
        goalRepository = GoalCapsuleRepository(db.goalCapsuleDao())
        subGoalRepository = MandalartSubGoalRepository(db.mandalartSubGoalDao())
        viewModelScope.launch {
            loadMandalartForYear(LocalDate.now().year)
        }
    }

    fun loadMandalartForYear(year: Int) {
        viewModelScope.launch {
            val goal = goalRepository.getMandalartGoals().find { it.year == year }
            if (goal != null) {
                currentGoalId = goal.id
                _finalGoal.postValue(goal.goalName)
                _year.postValue(goal.year.toString())

                _goals.forEachIndexed { i, liveData -> liveData.postValue("목표 ${i + 1}") }

                val subGoals = subGoalRepository.getSubGoals(goal.id)
                subGoals.forEach { subGoal ->
                    if (subGoal.index in 0..7) {
                        _goals[subGoal.index].postValue(subGoal.content)
                    }
                }
            } else {
                resetMandalartState(year)
            }
        }
    }

    private fun resetMandalartState(yearToReset: Int) {
        currentGoalId = null
        _finalGoal.postValue("")
        _year.postValue(yearToReset.toString())
        _goals.forEachIndexed { i, liveData ->
            liveData.postValue("목표 ${i + 1}")
        }
    }

    fun updateAndSaveFinalGoal(text: String) {
        if (_finalGoal.value == text) return
        _finalGoal.value = text
        saveMandalart()
    }

    fun updateYear(year: String) {
        val yearInt = year.toIntOrNull() ?: return
        if (_year.value != year) {
            _year.value = year
            loadMandalartForYear(yearInt)
        }
    }

    fun updateSubGoal(index: Int, text: String) {
        if (index in 0..7) {
            _goals[index].value = text
            viewModelScope.launch {
                currentGoalId?.let {
                    subGoalRepository.saveSubGoals(listOf(MandalartSubGoal(parentGoalId = it, index = index, content = text)))
                }
            }
        }
    }

    fun deleteCurrentMandalart() {
        val idToDelete = currentGoalId ?: return
        val yearToRefresh = _year.value?.toIntOrNull() ?: return

        viewModelScope.launch {
            subGoalRepository.deleteSubGoalsByParentId(idToDelete)
            goalRepository.deleteGoalCapsuleById(idToDelete)
            loadMandalartForYear(yearToRefresh) // Refresh the view
        }
    }

    fun saveMandalart() {
        val goalName = _finalGoal.value
        val goalYear = _year.value?.toIntOrNull()

        if (goalName.isNullOrBlank() || goalYear == null) {
            return
        }

        viewModelScope.launch {
            val existingGoal = goalRepository.getMandalartGoals().find { it.year == goalYear }

            if (existingGoal != null) {
                // Update existing goal
                val updatedGoal = existingGoal.copy(goalName = goalName)
                goalRepository.insertGoalCapsule(updatedGoal)
            } else {
                // Create new goal
                val newGoal = GoalCapsule(
                    goalName = goalName,
                    targetDate = LocalDate.of(goalYear, 12, 31),
                    startDate = LocalDate.now(),
                    isMandalartGoal = true,
                    year = goalYear,
                    color = "#FFF5F5",
                    description = "만다라트에서 생성된 목표",
                    question1 = "",
                    question2 = "",
                    question3 = "",
                    startMessage = ""
                )
                currentGoalId = newGoal.id // Set state immediately
                goalRepository.insertGoalCapsule(newGoal)

                // Save default sub-goals for the new mandalart
                val subGoalsToSave = _goals.mapIndexed { index, liveData ->
                    MandalartSubGoal(parentGoalId = newGoal.id, index = index, content = liveData.value ?: "")
                }
                subGoalRepository.saveSubGoals(subGoalsToSave)
            }
        }
    }

    fun onSaveImageClicked() {
        _saveMandalartImage.value = true
    }

    fun onSaveImageHandled() {
        _saveMandalartImage.value = false
    }

    fun getSubGoalContent(index: Int): String? {
        return if (index in 0..7) _goals[index].value else null
    }

    fun getCurrentGoalId(): String? {
        return currentGoalId
    }
}
