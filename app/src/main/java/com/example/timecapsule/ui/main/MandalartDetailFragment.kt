package com.example.timecapsule.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.timecapsule.R

class MandalartDetailFragment : Fragment() {

    private val args: MandalartDetailFragmentArgs by navArgs()
    private val viewModel: MandalartViewModel by activityViewModels { 
        MandalartViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mandalart_detail, container, false)

        val titleTextView = view.findViewById<TextView>(R.id.mandalart_detail_title)
        titleTextView.text = "세부목표 ${args.goalIndex + 1}"

        val parentGoalEditText = view.findViewById<EditText>(R.id.parent_goal)
        parentGoalEditText.setText(args.goalName)

        // Store the original hint
        val originalHint = parentGoalEditText.hint

        parentGoalEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // When focused, move the current text to the hint and clear the text field
                parentGoalEditText.hint = parentGoalEditText.text
                parentGoalEditText.setText("")
            } else {
                // When focus is lost, if the text is empty, restore the text from the hint
                if (parentGoalEditText.text.isEmpty()) {
                    parentGoalEditText.setText(parentGoalEditText.hint)
                }
                // Restore the original hint
                parentGoalEditText.hint = originalHint
            }
        }

        parentGoalEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.updateSubGoal(args.goalIndex, text.toString())
        }

        val backButton = view.findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }
}