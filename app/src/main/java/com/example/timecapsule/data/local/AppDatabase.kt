package com.example.timecapsule.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.timecapsule.data.model.ChecklistItem
import com.example.timecapsule.data.model.DailyRecord
import com.example.timecapsule.data.model.GoalAnswer
import com.example.timecapsule.data.model.GoalCapsule
import com.example.timecapsule.data.model.MandalartSubGoal

@Database(
    entities = [
        GoalCapsule::class,
        DailyRecord::class,
        GoalAnswer::class,
        ChecklistItem::class,
        MandalartSubGoal::class
    ],
    version = 17, // Schema changed, so updated version to 17
    exportSchema = false
)
@TypeConverters(GoalStatusConverter::class, com.example.timecapsule.data.database.Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalCapsuleDao(): GoalCapsuleDao
    abstract fun dailyRecordDao(): DailyRecordDao
    abstract fun goalAnswerDao(): GoalAnswerDao
    abstract fun checklistItemDao(): ChecklistItemDao
    abstract fun mandalartSubGoalDao(): MandalartSubGoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "timecapsule_database"
                )
                    .fallbackToDestructiveMigration() // Recreates DB on schema change
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
