package com.example.timecapsule.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mandalart_sub_goals",
    foreignKeys = [ForeignKey(
        entity = GoalCapsule::class,
        parentColumns = ["id"],
        childColumns = ["parentGoalId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["parentGoalId"])]
)
data class MandalartSubGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val parentGoalId: String,
    val index: Int,
    val content: String
)
