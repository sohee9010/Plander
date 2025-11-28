package com.example.timecapsule.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.timecapsule.databinding.FragmentArchiveBottleBinding

class ArchiveBottleFragment : Fragment() {

    private var _binding: FragmentArchiveBottleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalCapsuleViewModel by activityViewModels()
    private lateinit var bottleView: BottleView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArchiveBottleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottleView = BottleView(requireContext())
        binding.bottleContainer.addView(bottleView)

        viewModel.archivedGoalCapsules.observe(viewLifecycleOwner) {
            bottleView.updateMarbles(it)
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        bottleView.resumeSensor()
    }

    override fun onPause() {
        super.onPause()
        bottleView.pauseSensor()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}