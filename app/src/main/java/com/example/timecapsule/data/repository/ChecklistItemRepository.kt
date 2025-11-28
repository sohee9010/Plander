package com.example.timecapsule.data.repository

import androidx.lifecycle.LiveData
import com.example.timecapsule.data.local.ChecklistItemDao
import com.example.timecapsule.data.model.ChecklistItem

/**
 * 체크리스트 항목 데이터에 접근하기 위한 레포지토리 클래스입니다.
 * 데이터 소스(현재는 Room DB)와의 통신을 담당합니다.
 */
class ChecklistItemRepository(private val checklistItemDao: ChecklistItemDao) {

    /**
     * 여러 목표 ID에 해당하는 모든 체크리스트 항목을 비동기적으로 가져옵니다.
     */
    suspend fun getChecklistsByGoalIds(goalIds: List<String>): List<ChecklistItem> {
        return checklistItemDao.getChecklistsByGoalIds(goalIds)
    }

    /**
     * 특정 ID를 가진 체크리스트 항목 하나를 비동기적으로 가져옵니다.
     */
    suspend fun getChecklistItemById(id: String): ChecklistItem? {
        return checklistItemDao.getChecklistItemById(id)
    }

    /**
     * 특정 목표 ID에 속한 모든 체크리스트 항목을 LiveData 형태로 가져옵니다.
     * 데이터 변경 시 자동으로 UI가 업데이트될 수 있습니다.
     */
    fun getChecklistByGoalId(goalId: String): LiveData<List<ChecklistItem>> {
        return checklistItemDao.getChecklistByGoalId(goalId)
    }

    /**
     * 여러 체크리스트 항목들을 데이터베이스에 삽입합니다.
     */
    suspend fun insertChecklistItems(items: List<ChecklistItem>) {
        checklistItemDao.insertChecklistItems(items)
    }

    /**
     * 특정 목표 ID에 속한 모든 체크리스트 항목을 데이터베이스에서 삭제합니다.
     */
    suspend fun deleteChecklistByGoalId(goalId: String) {
        checklistItemDao.deleteChecklistByGoalId(goalId)
    }
}