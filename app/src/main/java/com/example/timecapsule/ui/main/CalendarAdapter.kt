package com.example.timecapsule.ui.main

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.timecapsule.R
import java.util.Calendar

class CalendarAdapter(
    private var days: List<CalendarDay>,
    private val onDayClick: (CalendarDay) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    private var selectedPosition = -1

    inner class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val container: LinearLayout = view.findViewById(R.id.dayContainer)
        private val tvDay: TextView = view.findViewById(R.id.tvDay)
        private val ivOpeningDayMarker: ImageView = view.findViewById(R.id.ivOpeningDayMarker)

        fun bind(day: CalendarDay) {
            tvDay.text = Calendar.getInstance().apply { time = day.date }.get(Calendar.DAY_OF_MONTH).toString()

            // --- 1. 스타일 초기화 ---
            container.background = null
            tvDay.background = null
            tvDay.setTextColor(ContextCompat.getColor(itemView.context, R.color.on_surface))
            tvDay.setTypeface(null, Typeface.NORMAL)
            ivOpeningDayMarker.visibility = View.GONE

            // --- 2. 상태에 따라 스타일 적용 ---
            if (!day.isCurrentMonth) {
                // 현재 월에 속하지 않는 날짜는 연하게 표시
                tvDay.setTextColor(Color.LTGRAY)
                container.isClickable = false
            } else {
                // 현재 월에 속하는 날짜
                container.isClickable = true

                if (day.isOpeningDay) {
                    ivOpeningDayMarker.visibility = View.VISIBLE
                }

                if (day.isSelected) {
                    // 선택된 날짜는 파란 배경으로 강조
                    container.background = ContextCompat.getDrawable(itemView.context, R.drawable.selected_day_background)
                }

                if (day.isToday) {
                    // 오늘 날짜는 동그란 배경과 굵은 글씨로 강조
                    tvDay.background = ContextCompat.getDrawable(itemView.context, R.drawable.today_circle_background)
                    tvDay.setTextColor(Color.WHITE)
                    tvDay.setTypeface(null, Typeface.BOLD)
                }
            }

            // --- 3. 클릭 이벤트 처리 ---
            container.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && selectedPosition != adapterPosition) {
                    if (selectedPosition != -1) {
                        days.getOrNull(selectedPosition)?.isSelected = false
                        notifyItemChanged(selectedPosition)
                    }
                    day.isSelected = true
                    selectedPosition = adapterPosition
                    notifyItemChanged(selectedPosition)

                    onDayClick(day)
                }
            }
        }
    }

    fun getSelectedDay(): CalendarDay? {
        return if (selectedPosition in days.indices) days[selectedPosition] else null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount() = days.size

    fun updateDays(newDays: List<CalendarDay>) {
        days = newDays
        val newSelectedPosition = newDays.indexOfFirst { it.isSelected }

        if (newSelectedPosition != -1) {
            selectedPosition = newSelectedPosition
        } else {
            val todayPosition = newDays.indexOfFirst { it.isToday }
            if (todayPosition != -1) {
                days[todayPosition].isSelected = true
                selectedPosition = todayPosition
            }
        }
        notifyDataSetChanged()
    }
}