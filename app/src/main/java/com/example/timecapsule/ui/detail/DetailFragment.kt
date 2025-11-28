package com.example.timecapsule.ui.detail

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.timecapsule.R
import com.example.timecapsule.data.model.GoalCapsule
import com.example.timecapsule.data.model.GoalStatus
import com.example.timecapsule.databinding.FragmentDetailBinding
import com.example.timecapsule.ui.main.GoalCapsuleViewModel
import com.example.timecapsule.ui.main.GoalCompleteActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.threeten.bp.format.DateTimeFormatter

class DetailFragment : DialogFragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalCapsuleViewModel by activityViewModels()
    private val args: DetailFragmentArgs by navArgs()

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val displayMetrics = resources.displayMetrics
            val width = (displayMetrics.widthPixels * 0.90).toInt()
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setGravity(Gravity.CENTER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()

        viewModel.setGoalId(args.goalId)

        viewModel.selectedGoal.observe(viewLifecycleOwner) { goal ->
            goal?.let { updateUi(it) }
        }

        viewModel.practiceRate.observe(viewLifecycleOwner) { rate ->
            binding.progressIndicator.progress = rate.toInt()
        }
    }

    private fun setupListeners() {
        binding.btnDelete.setOnClickListener { showDeleteConfirmDialog() }
        binding.btnMoreActions.setOnClickListener { view ->
            showStatusMenu(view)
        }
    }

    private fun updateUi(goal: GoalCapsule) {
        binding.tvGoalTitle.text = goal.goalName
        binding.tvPledge.text = goal.startMessage
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        binding.tvTargetDate.text = goal.targetDate.format(formatter)
        binding.tvGoalStatus.text = getStatusText(goal.status)

        binding.btnMoreActions.visibility = if (goal.status == GoalStatus.ACTIVE) View.VISIBLE else View.GONE
    }

    private fun showStatusMenu(anchorView: View) {
        val goal = viewModel.selectedGoal.value ?: return
        if (goal.status != GoalStatus.ACTIVE) return

        val popup = PopupMenu(requireContext(), anchorView)
        popup.menu.add(0, MENU_ID_COMPLETE, 0, "완료")
        popup.menu.add(0, MENU_ID_GIVE_UP, 0, "중단")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                MENU_ID_COMPLETE -> {
                    viewModel.updateGoalStatus(args.goalId, GoalStatus.COMPLETED)
                    val intent = Intent(requireActivity(), GoalCompleteActivity::class.java).apply {
                        putExtra("GOAL_TITLE", goal.goalName)
                    }
                    startActivity(intent)
                    dismiss()
                }
                MENU_ID_GIVE_UP -> showGiveUpConfirmDialog()
            }
            true
        }
        popup.show()
    }

    private fun showGiveUpConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("정말 중단하시겠어요?")
            .setMessage("목표를 중단하면 보관함으로 이동하며, 다시 되돌릴 수 없습니다.")
            .setNegativeButton("취소", null)
            .setPositiveButton("중단") { _, _ ->
                viewModel.updateGoalStatus(args.goalId, GoalStatus.GAVE_UP)
                dismiss()
            }
            .show()
    }

    private fun showDeleteConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("정말 삭제하시겠어요?")
            .setMessage("이 목표와 관련된 모든 기록이 영구적으로 삭제됩니다.")
            .setNegativeButton("취소", null)
            .setPositiveButton("삭제") { _, _ ->
                viewModel.deleteGoal(args.goalId)
                dismiss()
            }
            .show()
    }

    private fun getStatusText(status: GoalStatus): String {
        return when (status) {
            GoalStatus.ACTIVE -> "진행중"
            GoalStatus.COMPLETED -> "완료"
            GoalStatus.GAVE_UP -> "중단됨"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val MENU_ID_COMPLETE = 1
        private const val MENU_ID_GIVE_UP = 2
    }
}