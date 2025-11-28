package com.example.timecapsule.ui.main

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timecapsule.R
import com.example.timecapsule.data.model.GoalCapsule
import com.example.timecapsule.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalCapsuleViewModel by activityViewModels()
    private lateinit var goalAdapter: GoalAdapter
    private lateinit var bottleView: BottleView

    // Tip Box Properties
    private val handler = Handler(Looper.getMainLooper())
    private var tipIndex = 0
    private val tips = listOf(
        "구슬 색이 진할수록 마감일이 가까워져요!",
        "구슬을 터치하거나 드래그 해보세요!",
        "병을 기울이면 구슬이 굴러가요",
        "목표를 작성하여 구슬을 채워보세요"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupBottleView()
        observeGoals()
        setupFab()
        setupTipBox()

        binding.fabAddGoal.setOnClickListener {
            findNavController().navigate(R.id.action_list_to_createGoalCapsule)
        }

        binding.fabShowArchive.setOnClickListener {
            findNavController().navigate(R.id.action_list_to_archive_list)
        }

        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnListView -> {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.bottleContainer.visibility = View.GONE
                        binding.tipBoxBottle.visibility = View.GONE
                        binding.fabAddGoal.visibility = View.VISIBLE
                        binding.fabShowArchive.visibility = View.GONE
                        bottleView.pauseSensor()
                        handler.removeCallbacks(tipRunnable)
                        handler.removeCallbacks(hideTipRunnable)
                    }
                    R.id.btnBottleView -> {
                        binding.recyclerView.visibility = View.GONE
                        binding.bottleContainer.visibility = View.VISIBLE
                        binding.tipBoxBottle.visibility = View.VISIBLE
                        binding.fabAddGoal.visibility = View.GONE
                        binding.fabShowArchive.visibility = View.VISIBLE
                        bottleView.resumeSensor()
                        startTipCycle()
                    }
                }
            }
        }
    }

    private fun setupTipBox() {
        val tossfaceTypeface = ResourcesCompat.getFont(requireContext(), R.font.tossface_emoji)
        binding.tipIconBottle.typeface = tossfaceTypeface

        binding.tipTextSwitcherBottle.setFactory {
            TextView(context).apply {
                textSize = 14f
                gravity = Gravity.CENTER_VERTICAL
            }
        }

        val inAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_from_bottom)
        val outAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_to_top)
        binding.tipTextSwitcherBottle.inAnimation = inAnim
        binding.tipTextSwitcherBottle.outAnimation = outAnim

        binding.tipTextSwitcherBottle.setText(tips[tipIndex])
    }

    private val tipRunnable = object : Runnable {
        override fun run() {
            tipIndex = (tipIndex + 1) % tips.size
            binding.tipTextSwitcherBottle.setText(tips[tipIndex])
            handler.postDelayed(this, 3000) // Switch every 3 seconds
        }
    }

    private val hideTipRunnable = Runnable {
        binding.tipBoxBottle.animate().alpha(0f).withEndAction { binding.tipBoxBottle.visibility = View.GONE }.duration = 1500
    }

    private fun startTipCycle(){
        binding.tipBoxBottle.visibility = View.VISIBLE
        binding.tipBoxBottle.alpha = 1f
        handler.postDelayed(tipRunnable, 3000)
        handler.postDelayed(hideTipRunnable, 12000)
    }

    private fun setupBottleView() {
        bottleView = BottleView(requireContext())
        binding.bottleContainer.addView(bottleView)
        bottleView.onMarbleClickListener = object : BottleView.OnMarbleClickListener {
            override fun onMarbleClick(goal: GoalCapsule) {
                if (goal.isMandalartGoal) {
                    val action = ListFragmentDirections.actionListToMandalartDetailView(goal.id)
                    findNavController().navigate(action)
                } else {
                    val action = ListFragmentDirections.actionListToDetail(goal.id)
                    findNavController().navigate(action)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.toggleGroup.checkedButtonId == R.id.btnBottleView) {
            bottleView.resumeSensor()
            startTipCycle()
        }
    }

    override fun onPause() {
        super.onPause()
        bottleView.pauseSensor()
        handler.removeCallbacks(tipRunnable)
        handler.removeCallbacks(hideTipRunnable)
    }

    private fun setupFab() {
        val fabAddGoal = binding.fabAddGoal
        val fabShowArchive = binding.fabShowArchive
        val context = requireContext()

        val backgroundColor = ContextCompat.getColor(context, R.color.fab_add_goal_background)
        fabAddGoal.backgroundTintList = ColorStateList.valueOf(backgroundColor)
        fabShowArchive.backgroundTintList = ColorStateList.valueOf(backgroundColor)

        val iconColor = ContextCompat.getColor(context, R.color.fab_add_goal_icon)
        fabAddGoal.imageTintList = ColorStateList.valueOf(iconColor)
        fabShowArchive.imageTintList = ColorStateList.valueOf(iconColor)
    }

    private fun setupRecyclerView() {
        goalAdapter = GoalAdapter { goal ->
            if (goal.isMandalartGoal) {
                val action = ListFragmentDirections.actionListToMandalartDetailView(goal.id)
                findNavController().navigate(action)
            } else {
                val action = ListFragmentDirections.actionListToDetail(goal.id)
                findNavController().navigate(action)
            }
        }
        binding.recyclerView.apply {
            adapter = goalAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeGoals() {
        viewModel.activeGoalCapsules.observe(viewLifecycleOwner) { goals ->
            goalAdapter.updateGoals(goals)
            bottleView.updateMarbles(goals)

            if (goals.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.bottleContainer.visibility = View.GONE
            } else {
                if (binding.toggleGroup.checkedButtonId == R.id.btnListView) {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.bottleContainer.visibility = View.GONE
                } else {
                    binding.recyclerView.visibility = View.GONE
                    binding.bottleContainer.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}