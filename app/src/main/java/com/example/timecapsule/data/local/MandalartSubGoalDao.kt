package com.example.timecapsule.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.timecapsule.data.model.MandalartSubGoal

@Dao
interface MandalartSubGoalDao {
    @Query("SELECT * FROM mandalart_sub_goals WHERE parentGoalId = :parentGoalId ORDER BY `index` ASC")
    suspend fun findByParentGoalId(parentGoalId: String): List<MandalartSubGoal>

    @Upsert
    suspend fun upsertAll(subGoals: List<MandalartSubGoal>)

    @Query("DELETE FROM mandalart_sub_goals WHERE parentGoalId = :parentGoalId")
    suspend fun deleteByParentGoalId(parentGoalId: String)
}
