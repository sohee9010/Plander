package com.example.timecapsule.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.timecapsule.data.model.GoalCapsule
import com.example.timecapsule.databinding.BottomSheetGoalQuickViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.threeten.bp.format.DateTimeFormatter

class GoalQuickViewBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetGoalQuickViewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalCapsuleViewModel by activityViewModels()

    private var goalId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetGoalQuickViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goalId = arguments?.getString("goalId")
        goalId?.let {
            viewModel.setGoalId(it) // ViewModel에 ID 설정
        }

        setupButtons()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.selectedGoal.observe(viewLifecycleOwner) { goal ->
            goal?.let { setupUI(it) }
        }

        viewModel.practiceRate.observe(viewLifecycleOwner) { rate ->
            binding.progressBar.progress = rate.toInt()
            binding.tvStatus.text = "${String.format("%.1f", rate)}%"
        }
    }

    private fun setupUI(goal: GoalCapsule) {
        binding.apply {
            tvGoalTitle.text = goal.goalName
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            tvTargetDate.text = goal.targetDate.format(formatter)
            progressBar.progress = 0
            tvStatus.text = "계산중..."
        }
    }

    private fun setupButtons() {
        binding.btnMoveToArchive.setOnClickListener {
            android.widget.Toast.makeText(
                requireContext(),
                "보관함 기능은 추후 구현 예정입니다",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            dismiss()
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmDialog()
        }
    }

    private fun showDeleteConfirmDialog() {
        viewModel.selectedGoal.value?.let {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("목표 삭제")
                .setMessage("정말로 '${it.goalName}'을(를) 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    deleteGoal()
                }
                .setNegativeButton("취소", null)
                .show()
        }
    }

    private fun deleteGoal() {
        goalId?.let {
            viewModel.deleteGoal(it)
            android.widget.Toast.makeText(
                requireContext(),
                "삭제되었습니다",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(goalId: String): GoalQuickViewBottomSheet {
            return GoalQuickViewBottomSheet().apply {
                arguments = Bundle().apply {
                    putString("goalId", goalId)
                }
            }
        }
    }
}