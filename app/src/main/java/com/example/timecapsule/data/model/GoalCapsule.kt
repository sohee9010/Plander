package com.example.timecapsule.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDate
import java.util.UUID

@Parcelize
@Entity(tableName = "goal_capsules")
data class GoalCapsule(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val goalName: String,
    val targetDate: LocalDate,
    val startDate: LocalDate,

    val goalType: GoalType = GoalType.HABIT,
    val targetDaysPerWeek: Int = 7,

    val question1: String,
    val question2: String,
    val question3: String,

    val startMessage: String,
    val startPhotoPath: String? = null,
    val description: String,

    val status: GoalStatus = GoalStatus.ACTIVE,
    val endDate: LocalDate? = null,

    val endMessage: String? = null,
    val endPhotoPath: String? = null,

    val color: String = "#DCEFFA", // Default color changed to light blue

    val congratulated: Boolean = false,
    val isMandalartGoal: Boolean = false,
    val year: Int? = null
) : Parcelable
