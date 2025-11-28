package com.example.timecapsule.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timecapsule.R
import com.example.timecapsule.data.model.ChecklistItem

/**
 * 체크리스트 항목을 표시하는 RecyclerView 어댑터
 * @param onCheckChanged 체크박스 상태가 변경될 때 호출될 콜백
 */
class ChecklistAdapter(
    private val onCheckChanged: (ChecklistItem, Boolean) -> Unit
) : RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder>() {

    private var items: List<ChecklistItem> = emptyList()
    private var completedIds: Set<String> = emptySet()

    inner class ChecklistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val checkBox: CheckBox = view.findViewById(R.id.cbChecklistItem)
        private val content: TextView = view.findViewById(R.id.tvChecklistItemContent)

        fun bind(item: ChecklistItem) {
            content.text = item.content
            // 체크 상태 변경 리스너를 잠시 해제 (무한 루프 방지)
            checkBox.setOnCheckedChangeListener(null)
            // 완료된 ID 목록에 현재 아이템 ID가 있는지 확인하여 체크 상태를 설정
            checkBox.isChecked = completedIds.contains(item.id)
            // 리스너를 다시 설정
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onCheckChanged(item, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_checklist, parent, false)
        return ChecklistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    /**
     * 어댑터의 데이터를 갱신합니다.
     * @param newItems 표시할 전체 체크리스트 항목
     * @param newCompletedIds 완료된 것으로 표시할 항목들의 ID 집합
     */
    fun updateItems(newItems: List<ChecklistItem>, newCompletedIds: Set<String>) {
        items = newItems
        completedIds = newCompletedIds
        notifyDataSetChanged()
    }
}