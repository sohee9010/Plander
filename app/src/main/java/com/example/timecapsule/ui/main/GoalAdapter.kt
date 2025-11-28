package com.example.timecapsule.ui.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.timecapsule.R
import com.example.timecapsule.data.model.GoalCapsule
import com.example.timecapsule.data.model.GoalStatus
import com.example.timecapsule.databinding.ItemGoalBinding
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit

class GoalAdapter(private val onItemClicked: (GoalCapsule) -> Unit) :
    RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    private var goals: List<GoalCapsule> = emptyList()

    class GoalViewHolder(private val binding: ItemGoalBinding, private val onItemClicked: (GoalCapsule) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: GoalCapsule) {
            val context = binding.root.context
            if (goal.isMandalartGoal) {
                binding.tvGoalName.text = "[${goal.year}년] 만다라트\n${goal.goalName}" // 제목 두 줄로 변경
                binding.root.setCardBackgroundColor(Color.parseColor(goal.color))
            } else {
                binding.tvGoalName.text = goal.goalName
                binding.root.setCardBackgroundColor(ContextCompat.getColor(context, R.color.goal_item_background_color))
            }

            binding.tvGoalDescription.text = goal.startMessage

            val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
            binding.tvGoalDate.text = goal.targetDate.format(formatter)

            val days = ChronoUnit.DAYS.between(LocalDate.now(), goal.targetDate)
            binding.tvDday.text = when {
                days >= 0 -> "D-${days}"
                else -> "D+${-days}"
            }

            val colorRes = when (goal.status) {
                GoalStatus.COMPLETED -> R.color.archive_completed
                GoalStatus.GAVE_UP -> R.color.archive_aborted
                else -> R.color.on_surface
            }
            binding.tvGoalName.setTextColor(ContextCompat.getColor(context, colorRes))

            binding.tvProgress.text = "실천율 0%"

            itemView.setOnClickListener {
                onItemClicked(goal)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(goals[position])
    }

    override fun getItemCount(): Int {
        return goals.size
    }

    fun updateGoals(newGoals: List<GoalCapsule>) {
        goals = newGoals
        notifyDataSetChanged()
    }
}