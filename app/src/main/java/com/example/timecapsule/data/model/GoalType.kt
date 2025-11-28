package com.example.timecapsule.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class GoalType : Parcelable {
    HABIT,        // 습관 형성
    ACHIEVEMENT   // 달성형
}