package com.example.timecapsule.ui.main

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.timecapsule.databinding.FragmentMandalartDetailViewBinding

class MandalartDetailViewFragment : DialogFragment() {

    private var _binding: FragmentMandalartDetailViewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MandalartViewModel by activityViewModels { 
        MandalartViewModelFactory(requireActivity().application)
    }
    private val args: MandalartDetailViewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMandalartDetailViewBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
            it.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        binding.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.saveImageButton.setOnClickListener {
            viewModel.onSaveImageClicked()
            dismiss()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("현재 만다라트 계획을 초기화하고 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                viewModel.deleteCurrentMandalart()
                dismiss()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.finalGoal.observe(viewLifecycleOwner) {
            binding.finalGoalText.text = it
        }

        val subGoalsObserver = Observer<String> { _ ->
            binding.subGoalsContainer.removeAllViews()
            viewModel.goals.forEach { goalLiveData ->
                val goal = goalLiveData.value
                if (!goal.isNullOrEmpty()) {
                    val textView = TextView(requireContext()).apply {
                        text = goal
                        textSize = 16f
                        setPadding(0, 8, 0, 8)
                    }
                    binding.subGoalsContainer.addView(textView)
                }
            }
        }

        viewModel.goals.forEach { liveData ->
            liveData.observe(viewLifecycleOwner, subGoalsObserver)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
