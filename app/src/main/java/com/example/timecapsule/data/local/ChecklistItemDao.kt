package com.example.timecapsule.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.timecapsule.data.model.ChecklistItem

@Dao
interface ChecklistItemDao {

    // ViewModel에서 사용할 새로운 함수들
    @Query("SELECT * FROM checklist_items WHERE goalCapsuleId IN (:goalIds) ORDER BY orderIndex ASC")
    suspend fun getChecklistsByGoalIds(goalIds: List<String>): List<ChecklistItem>

    @Query("SELECT * FROM checklist_items WHERE id = :id")
    suspend fun getChecklistItemById(id: String): ChecklistItem?

    // 기존 함수들
    @Query("SELECT * FROM checklist_items WHERE goalCapsuleId = :goalId ORDER BY orderIndex ASC")
    fun getChecklistByGoalId(goalId: String): LiveData<List<ChecklistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItems(items: List<ChecklistItem>)

    @Delete
    suspend fun deleteChecklistItem(item: ChecklistItem)

    @Query("DELETE FROM checklist_items WHERE goalCapsuleId = :goalId")
    suspend fun deleteChecklistByGoalId(goalId: String)
}