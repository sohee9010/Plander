package com.example.timecapsule.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.timecapsule.data.model.GoalCapsule
import com.example.timecapsule.data.model.GoalStatus
import org.threeten.bp.LocalDate

@Dao
interface GoalCapsuleDao {

    @Query("SELECT * FROM goal_capsules WHERE id = :id")
    fun getGoalCapsuleByIdLiveData(id: String): LiveData<GoalCapsule?>

    @Query("SELECT * FROM goal_capsules WHERE targetDate = :today AND status = 'ACTIVE'")
    suspend fun getDDayGoals(today: LocalDate): List<GoalCapsule>

    @Query("SELECT * FROM goal_capsules ORDER BY targetDate ASC")
    fun getAllGoalCapsules(): LiveData<List<GoalCapsule>>

    @Query("SELECT * FROM goal_capsules WHERE status = 'ACTIVE' ORDER BY targetDate ASC")
    fun getActiveGoalCapsules(): LiveData<List<GoalCapsule>>

    @Query("SELECT * FROM goal_capsules WHERE status = 'GAVE_UP' ORDER BY targetDate ASC")
    fun getGaveUpGoals(): LiveData<List<GoalCapsule>>

    @Query("SELECT * FROM goal_capsules WHERE status = 'COMPLETED' ORDER BY endDate DESC")
    fun getArchivedGoalCapsules(): LiveData<List<GoalCapsule>>

    @Query("SELECT * FROM goal_capsules WHERE id = :id")
    suspend fun getGoalCapsuleById(id: String): GoalCapsule?

    @Query("SELECT * FROM goal_capsules WHERE goalName = :name LIMIT 1")
    suspend fun getGoalByName(name: String): GoalCapsule?

    @Query("SELECT * FROM goal_capsules WHERE isMandalartGoal = 1")
    suspend fun getMandalartGoals(): List<GoalCapsule>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoalCapsule(goalCapsule: GoalCapsule)

    @Update
    suspend fun updateGoalCapsule(goalCapsule: GoalCapsule)

    @Delete
    suspend fun deleteGoalCapsule(goalCapsule: GoalCapsule)

    @Query("DELETE FROM goal_capsules WHERE id = :id")
    suspend fun deleteGoalCapsuleById(id: String)

    @Query("UPDATE goal_capsules SET status = :status, endDate = :endDate WHERE id = :id")
    suspend fun updateStatus(id: String, status: GoalStatus, endDate: LocalDate?)

    @Query("UPDATE goal_capsules SET status = 'COMPLETED', endDate = :currentDate WHERE status = 'ACTIVE' AND targetDate < :currentDate")
    suspend fun autoCompleteOverdueGoals(currentDate: LocalDate)
}