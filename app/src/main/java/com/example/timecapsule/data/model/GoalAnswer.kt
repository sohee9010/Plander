package com.example.timecapsule.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "goal_answers")
data class GoalAnswer(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val goalCapsuleId: String,
    val questionNumber: Int,
    val question: String,

    val startAnswer: String,
    val startAnswerDate: Long = System.currentTimeMillis(),

    val endAnswer: String? = null,
    val endAnswerDate: Long? = null
)