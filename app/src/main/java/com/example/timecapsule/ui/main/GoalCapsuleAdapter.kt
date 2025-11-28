package com.example.timecapsule.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.timecapsule.data.model.GoalCapsule
import com.example.timecapsule.databinding.ItemGoalCapsuleBinding
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit

class GoalCapsuleAdapter(
    private val onItemClick: (GoalCapsule) -> Unit,
    private val onItemLongClick: (GoalCapsule) -> Unit
) : ListAdapter<GoalCapsule, GoalCapsuleAdapter.GoalCapsuleViewHolder>(GoalCapsuleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalCapsuleViewHolder {
        val binding = ItemGoalCapsuleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GoalCapsuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalCapsuleViewHolder, position: Int) {
        val goalCapsule = getItem(position)
        holder.bind(goalCapsule)

        holder.itemView.setOnClickListener {
            onItemClick(goalCapsule)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClick(goalCapsule)
            true
        }
    }

    class GoalCapsuleViewHolder(
        private val binding: ItemGoalCapsuleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(goalCapsule: GoalCapsule) {
            binding.apply {
                tvGoalName.text = goalCapsule.goalName

                val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
                tvTargetDate.text = "목표일: ${goalCapsule.targetDate.format(formatter)}"

                val dDay = ChronoUnit.DAYS.between(LocalDate.now(), goalCapsule.targetDate)
                tvDday.text = if (dDay >= 0) {
                    "D-$dDay"
                } else {
                    "완료"
                }
            }
        }
    }

    class GoalCapsuleDiffCallback : DiffUtil.ItemCallback<GoalCapsule>() {
        override fun areItemsTheSame(oldItem: GoalCapsule, newItem: GoalCapsule): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GoalCapsule, newItem: GoalCapsule): Boolean {
            return oldItem == newItem
        }
    }
}