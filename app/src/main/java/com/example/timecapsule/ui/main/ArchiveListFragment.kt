package com.example.timecapsule.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timecapsule.R
import com.example.timecapsule.databinding.FragmentArchiveListBinding

class ArchiveListFragment : Fragment() {

    private var _binding: FragmentArchiveListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalCapsuleViewModel by activityViewModels()
    private lateinit var goalAdapter: GoalAdapter
    private var type: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt(ARG_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArchiveListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeGoals()
    }

    private fun setupRecyclerView() {
        goalAdapter = GoalAdapter { goal ->
            if (goal.isMandalartGoal) {
                val action = ArchiveFragmentDirections.actionArchiveToMandalartDetailView(goal.id)
                parentFragment?.findNavController()?.navigate(action)
            } else {
                val action = ArchiveFragmentDirections.actionArchiveToDetail(goal.id)
                parentFragment?.findNavController()?.navigate(action)
            }
        }
        binding.recyclerView.apply {
            adapter = goalAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeGoals() {
        val liveData = if (type == 0) viewModel.gaveUpGoalCapsules else viewModel.archivedGoalCapsules
        val emptyText = if (type == 0) "중단된 목표가 없습니다." else "완료한 목표가 없습니다."

        liveData.observe(viewLifecycleOwner) { goals ->
            if (goals.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
                binding.tvEmpty.text = emptyText
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
                goalAdapter.updateGoals(goals)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TYPE = "ARG_TYPE"

        @JvmStatic
        fun newInstance(type: Int) = ArchiveListFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_TYPE, type)
            }
        }
    }
}