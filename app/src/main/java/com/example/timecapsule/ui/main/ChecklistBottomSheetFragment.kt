package com.example.timecapsule.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timecapsule.databinding.FragmentChecklistBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class ChecklistBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentChecklistBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalCapsuleViewModel by activityViewModels()
    private lateinit var checklistAdapter: ChecklistAdapter

    private var selectedDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedDate = LocalDate.ofEpochDay(it.getLong(ARG_SELECTED_DATE))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChecklistBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupRecyclerView()
        observeViewModel()

        selectedDate?.let {
            viewModel.setSelectedDate(it)
        }
    }

    private fun setupUI() {
        selectedDate?.let {
            val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
            binding.tvSelectedDate.text = it.format(formatter)
        }
    }

    private fun setupRecyclerView() {
        checklistAdapter = ChecklistAdapter { item, isChecked ->
            selectedDate?.let { date ->
                viewModel.updateChecklistState(item.id, date, isChecked)
            }
        }
        binding.checklistRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.checklistRecyclerView.adapter = checklistAdapter
    }

    private fun observeViewModel() {
        viewModel.checklistDataForUi.observe(viewLifecycleOwner) { (checklist, completedIds) ->
            checklistAdapter.updateItems(checklist, completedIds)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_SELECTED_DATE = "selected_date"

        fun newInstance(date: LocalDate): ChecklistBottomSheetFragment {
            return ChecklistBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_SELECTED_DATE, date.toEpochDay())
                }
            }
        }
    }
}