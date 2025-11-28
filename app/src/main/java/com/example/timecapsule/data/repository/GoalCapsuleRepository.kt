package com.example.timecapsule.data.repository

import androidx.lifecycle.LiveData
import com.example.timecapsule.data.local.GoalCapsuleDao
import com.example.timecapsule.data.model.GoalCapsule
import com.example.timecapsule.data.model.GoalStatus
import org.threeten.bp.LocalDate

class GoalCapsuleRepository(private val goalCapsuleDao: GoalCapsuleDao) {

    fun getGoalCapsuleByIdLiveData(id: String): LiveData<GoalCapsule?> {
        return goalCapsuleDao.getGoalCapsuleByIdLiveData(id)
    }

    suspend fun getDDayGoals(today: LocalDate): List<GoalCapsule> {
        return goalCapsuleDao.getDDayGoals(today)
    }

    fun getAllGoalCapsules(): LiveData<List<GoalCapsule>> {
        return goalCapsuleDao.getAllGoalCapsules()
    }

    fun getActiveGoalCapsules(): LiveData<List<GoalCapsule>> {
        return goalCapsuleDao.getActiveGoalCapsules()
    }

    fun getGaveUpGoals(): LiveData<List<GoalCapsule>> {
        return goalCapsuleDao.getGaveUpGoals()
    }

    fun getArchivedGoalCapsules(): LiveData<List<GoalCapsule>> {
        return goalCapsuleDao.getArchivedGoalCapsules()
    }

    suspend fun getGoalCapsuleById(id: String): GoalCapsule? {
        return goalCapsuleDao.getGoalCapsuleById(id)
    }

    suspend fun getGoalByName(name: String): GoalCapsule? {
        return goalCapsuleDao.getGoalByName(name)
    }

    suspend fun getMandalartGoals(): List<GoalCapsule> {
        return goalCapsuleDao.getMandalartGoals()
    }

    suspend fun insertGoalCapsule(goalCapsule: GoalCapsule) {
        goalCapsuleDao.insertGoalCapsule(goalCapsule)
    }

    suspend fun updateGoalCapsule(goalCapsule: GoalCapsule) {
        goalCapsuleDao.updateGoalCapsule(goalCapsule)
    }

    suspend fun deleteGoalCapsule(goalCapsule: GoalCapsule) {
        goalCapsuleDao.deleteGoalCapsule(goalCapsule)
    }

    suspend fun deleteGoalCapsuleById(id: String) {
        goalCapsuleDao.deleteGoalCapsuleById(id)
    }

    suspend fun updateStatus(id: String, status: GoalStatus) {
        val endDate = if (status == GoalStatus.COMPLETED || status == GoalStatus.GAVE_UP) {
            LocalDate.now()
        } else {
            null
        }
        goalCapsuleDao.updateStatus(id, status, endDate)
    }

    suspend fun autoCompleteOverdueGoals() {
        goalCapsuleDao.autoCompleteOverdueGoals(LocalDate.now())
    }
}