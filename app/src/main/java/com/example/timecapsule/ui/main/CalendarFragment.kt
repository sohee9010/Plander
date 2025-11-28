package com.example.timecapsule.ui.main

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timecapsule.R
import com.example.timecapsule.data.model.DailyRecord
import com.example.timecapsule.data.model.GoalCapsule
import com.example.timecapsule.databinding.FragmentCalendarBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import org.threeten.bp.LocalDate
import java.util.HashSet

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalCapsuleViewModel by activityViewModels()
    private lateinit var checklistAdapter: ChecklistAdapter

    private lateinit var eventDecorator: EventDecorator
    private lateinit var ddayDecorator: DdayDecorator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCalendar()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupCalendar() {
        binding.calendarView.selectionColor = ContextCompat.getColor(requireContext(), R.color.primary)
        
        binding.calendarView.setOnDateChangedListener { _, day, _ ->
            viewModel.setSelectedDate(LocalDate.of(day.year, day.month, day.day))
        }
        binding.calendarView.setOnMonthChangedListener { _, day ->
            val monthStart = LocalDate.of(day.year, day.month, 1)
            val monthEnd = monthStart.plusMonths(1).minusDays(1)
            viewModel.setVisibleDateRange(monthStart, monthEnd)
        }

        val today = CalendarDay.today()
        binding.calendarView.selectedDate = today
        viewModel.setSelectedDate(LocalDate.now())

        eventDecorator = EventDecorator(requireContext().getColor(R.color.primary))
        ddayDecorator = DdayDecorator()
        binding.calendarView.addDecorators(
            TodayDecorator(requireContext()),
            eventDecorator,
            ddayDecorator
        )
    }

    private fun setupRecyclerView() {
        checklistAdapter = ChecklistAdapter { item, isChecked ->
            val selectedDate = binding.calendarView.selectedDate?.let {
                LocalDate.of(it.year, it.month, it.day)
            } ?: LocalDate.now()
            viewModel.updateChecklistState(item.id, selectedDate, isChecked)
        }
        binding.checklistRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.checklistRecyclerView.adapter = checklistAdapter
    }

    private fun observeViewModel() {
        viewModel.monthlyRecords.observe(viewLifecycleOwner) { records ->
            val eventDates = records.filter { it.completedChecklistIds.isNotEmpty() }
                .map { CalendarDay.from(it.date.year, it.date.monthValue, it.date.dayOfMonth) }
                .toSet()
            eventDecorator.setDates(eventDates)
            binding.calendarView.invalidateDecorators()
        }

        viewModel.activeGoalCapsules.observe(viewLifecycleOwner) { goals ->
            val ddayDates = goals.map { CalendarDay.from(it.targetDate.year, it.targetDate.monthValue, it.targetDate.dayOfMonth) }
            ddayDecorator.setDates(ddayDates)
            binding.calendarView.invalidateDecorators()
        }

        viewModel.checklistDataForUi.observe(viewLifecycleOwner) { (checklist, completedIds) ->
            checklistAdapter.updateItems(checklist, completedIds)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class DdayDecorator : DayViewDecorator {
    private val dates = HashSet<CalendarDay>()

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(5f, Color.RED))
    }

    fun setDates(newDates: Collection<CalendarDay>) {
        this.dates.clear()
        this.dates.addAll(newDates)
    }
}

class EventDecorator(private val color: Int) : DayViewDecorator {
    private val dates = HashSet<CalendarDay>()

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(5f, color))
    }

    fun setDates(newDates: Collection<CalendarDay>) {
        this.dates.clear()
        this.dates.addAll(newDates)
    }
}

class TodayDecorator(context: Context) : DayViewDecorator {
    private val today = CalendarDay.today()
    private val drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.today_circle_background)

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day == today
    }

    override fun decorate(view: DayViewFacade?) {
        drawable?.let { view?.setBackgroundDrawable(it) }
    }
}
