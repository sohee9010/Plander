package com.example.timecapsule.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.timecapsule.data.model.DailyRecord
import org.threeten.bp.LocalDate

@Dao
interface DailyRecordDao {

    @Query("SELECT COUNT(DISTINCT date) FROM daily_records WHERE goalCapsuleId = :goalId AND completedChecklistIds != ''")
    fun getCompletedChecklistCount(goalId: String): LiveData<Int>

    @Query("SELECT * FROM daily_records WHERE date = :date")
    fun getRecordsForDate(date: LocalDate): LiveData<List<DailyRecord>>

    @Query("SELECT * FROM daily_records WHERE goalCapsuleId = :goalId")
    fun getRecordsForGoal(goalId: String): LiveData<List<DailyRecord>>

    @Query("SELECT * FROM daily_records WHERE date = :date AND goalCapsuleId = :goalId")
    suspend fun getRecordForDateAndGoal(date: LocalDate, goalId: String): DailyRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecord(record: DailyRecord)

    @Query("SELECT * FROM daily_records WHERE date BETWEEN :startDate AND :endDate")
    fun getRecordsBetweenDates(startDate: LocalDate, endDate: LocalDate): LiveData<List<DailyRecord>>

    @Query("SELECT * FROM daily_records WHERE goalCapsuleId = :goalCapsuleId ORDER BY date ASC")
    fun getRecordsByGoalCapsule(goalCapsuleId: String): LiveData<List<DailyRecord>>

    @Delete
    suspend fun deleteRecord(record: DailyRecord)

}