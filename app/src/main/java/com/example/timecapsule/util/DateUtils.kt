package com.example.timecapsule.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val DATE_FORMAT = "yyyy년 MM월 dd일"
    private const val DATETIME_FORMAT = "yyyy.MM.dd HH:mm"

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.KOREAN)
        return sdf.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat(DATETIME_FORMAT, Locale.KOREAN)
        return sdf.format(Date(timestamp))
    }

    fun getDDayString(unlockDate: Long): String {
        val now = System.currentTimeMillis()
        val diff = unlockDate - now

        return when {
            diff < 0 -> "열람 가능"
            diff < 24 * 60 * 60 * 1000 -> "D-Day"
            else -> {
                val days = diff / (24 * 60 * 60 * 1000)
                "D-$days"
            }
        }
    }

    fun dateToTimestamp(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}