package com.example.timecapsule.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "checklist_items")
data class ChecklistItem(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val goalCapsuleId: String,  // 어떤 목표에 속하는지
    val content: String,         // "매일 30분 운동하기"
    val orderIndex: Int = 0,     // 순서
    val createdAt: Long = System.currentTimeMillis()
)