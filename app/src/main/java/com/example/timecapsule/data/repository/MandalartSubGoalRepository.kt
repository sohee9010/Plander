package com.example.timecapsule.data.repository

import com.example.timecapsule.data.local.MandalartSubGoalDao
import com.example.timecapsule.data.model.MandalartSubGoal

class MandalartSubGoalRepository(private val subGoalDao: MandalartSubGoalDao) {

    suspend fun getSubGoals(parentGoalId: String): List<MandalartSubGoal> {
        return subGoalDao.findByParentGoalId(parentGoalId)
    }

    suspend fun saveSubGoals(subGoals: List<MandalartSubGoal>) {
        subGoalDao.upsertAll(subGoals)
    }

    suspend fun deleteSubGoalsByParentId(parentGoalId: String) {
        subGoalDao.deleteByParentGoalId(parentGoalId)
    }
}
