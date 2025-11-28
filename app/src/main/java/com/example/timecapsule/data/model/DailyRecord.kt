package com.example.timecapsule.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import java.util.UUID

@Entity(tableName = "daily_records")
data class DailyRecord(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val date: LocalDate, // LocalDate 타입으로 변경
    val goalCapsuleId: String, // 어떤 목표 캡슐인지

    // 완료된 체크리스트 항목들의 ID 리스트
    val completedChecklistIds: List<String> = emptyList(),

    val sticker: String? = null, // 스티커 정보

    val createdAt: Long = System.currentTimeMillis()
)
