package com.example.timecapsule.ui.main

import com.example.timecapsule.data.model.DailyRecord
import java.util.Date

/**
 * 캘린더의 각 날짜를 표현하는 데이터 클래스
 *
 * @param date 날짜
 * @param isCurrentMonth 현재 캘린더가 보여주는 월에 속하는지 여부
 * @param isToday 오늘 날짜인지 여부
 * @param dailyRecord 해당 날짜의 기록 (체크리스트 완료 정보 등)
 * @param isSelected 현재 선택된 날짜인지 여부
 * @param isOpeningDay 목표 캡슐이 개봉되는 날짜인지 여부
 */
data class CalendarDay(
    val date: Date,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    var dailyRecord: DailyRecord? = null,
    var isSelected: Boolean = false,
    val isOpeningDay: Boolean = false
)
