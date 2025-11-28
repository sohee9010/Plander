package com.example.timecapsule.data.database

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate

/**
 * Room 데이터베이스에서 사용할 타입 변환기 클래스.
 */
class Converters {
    /**
     * 쉼표로 구분된 String을 List<String>으로 변환합니다.
     */
    @TypeConverter
    fun fromString(value: String?): List<String> {
        return value?.split(",")?.map { it.trim() } ?: emptyList()
    }

    /**
     * List<String>을 쉼표로 구분된 String으로 변환합니다.
     */
    @TypeConverter
    fun fromList(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }

    /**
     * Long 타입의 Timestamp를 LocalDate로 변환합니다.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    /**
     * LocalDate를 Long 타입의 Timestamp로 변환합니다.
     */
    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}
