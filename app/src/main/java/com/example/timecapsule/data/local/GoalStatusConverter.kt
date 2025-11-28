package com.example.timecapsule.data.local

import androidx.room.TypeConverter
import com.example.timecapsule.data.model.GoalStatus

class GoalStatusConverter {
    @TypeConverter
    fun toGoalStatus(value: String) = enumValueOf<GoalStatus>(value)

    @TypeConverter
    fun fromGoalStatus(value: GoalStatus) = value.name
}