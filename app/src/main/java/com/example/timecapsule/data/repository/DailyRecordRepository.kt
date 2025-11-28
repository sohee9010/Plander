package com.example.timecapsule.data.repository

import androidx.lifecycle.LiveData
import com.example.timecapsule.data.local.DailyRecordDao
import com.example.timecapsule.data.model.DailyRecord
import org.threeten.bp.LocalDate

class DailyRecordRepository(private val dailyRecordDao: DailyRecordDao) {

    fun getCompletedChecklistCount(goalId: String): LiveData<Int> {
        return dailyRecordDao.getCompletedChecklistCount(goalId)
    }

    fun getRecordsForDate(date: LocalDate): LiveData<List<DailyRecord>> {
        return dailyRecordDao.getRecordsForDate(date)
    }

    fun getRecordsForGoal(goalId: String): LiveData<List<DailyRecord>> {
        return dailyRecordDao.getRecordsForGoal(goalId)
    }

    suspend fun getRecordForDateAndGoal(date: LocalDate, goalId: String): DailyRecord? {
        return dailyRecordDao.getRecordForDateAndGoal(date, goalId)
    }

    suspend fun upsertRecord(record: DailyRecord) {
        dailyRecordDao.upsertRecord(record)
    }

    fun getRecordsBetweenDates(startDate: LocalDate, endDate: LocalDate): LiveData<List<DailyRecord>> {
        return dailyRecordDao.getRecordsBetweenDates(startDate, endDate)
    }

    fun getRecordsByGoalCapsule(goalCapsuleId: String): LiveData<List<DailyRecord>> {
        return dailyRecordDao.getRecordsByGoalCapsule(goalCapsuleId)
    }

    suspend fun deleteRecord(record: DailyRecord) {
        dailyRecordDao.deleteRecord(record)
    }
}