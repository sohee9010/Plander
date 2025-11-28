package com.example.timecapsule.ui.create

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.timecapsule.R
import com.example.timecapsule.data.model.GoalAnswer
import com.example.timecapsule.data.model.GoalCapsule
import com.example.timecapsule.data.model.GoalStatus
import com.example.timecapsule.data.model.GoalType
import com.example.timecapsule.databinding.FragmentCreateGoalCapsuleBinding
import com.example.timecapsule.ui.main.GoalCapsuleViewModel
import org.threeten.bp.LocalDate
import java.util.Calendar
import java.util.UUID

class CreateGoalCapsuleFragment : Fragment() {

    private var _binding: FragmentCreateGoalCapsuleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalCapsuleViewModel by activityViewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
    }

    private lateinit var selectedTargetDate: LocalDate
    private var selectedGoalType: GoalType = GoalType.HABIT
    private var targetDaysPerWeek: Int = 7

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGoalCapsuleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialDate()
        setupListeners()
    }

    private fun setupInitialDate() {
        val today = LocalDate.now()
        binding.tvYear.text = "${today.year}년"

        binding.pickerMonth.minValue = 1
        binding.pickerMonth.maxValue = 12
        binding.pickerMonth.value = today.monthValue

        binding.pickerDay.minValue = 1
        binding.pickerDay.maxValue = today.lengthOfMonth()
        binding.pickerDay.value = today.dayOfMonth

        updateSelectedDate()
    }

    private fun showYearPickerDialog() {
        val context = requireContext()
        val yearPicker = NumberPicker(context).apply {
            val currentYear = LocalDate.now().year
            minValue = currentYear
            maxValue = currentYear + 10
            value = binding.tvYear.text.toString().removeSuffix("년").toInt()
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }

        AlertDialog.Builder(context)
            .setTitle("년도 선택")
            .setView(yearPicker)
            .setPositiveButton("확인") { _, _ ->
                val newYear = yearPicker.value
                binding.tvYear.text = "${newYear}년"
                onDateChanged()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun onDateChanged() {
        updateDayPicker()
        updateSelectedDate()
        validateDate()
    }

    private fun validateDate() {
        updateSelectedDate() // 먼저 날짜를 업데이트
        val today = LocalDate.now()

        if (selectedTargetDate.isBefore(today)) {
            binding.tvYear.text = "${today.year}년"
            binding.pickerMonth.value = today.monthValue
            binding.pickerDay.value = today.dayOfMonth
            Toast.makeText(requireContext(), "과거 날짜는 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
            updateSelectedDate() // 날짜를 오늘로 다시 맞춤
        }
    }

    private fun updateDayPicker() {
        val year = binding.tvYear.text.toString().removeSuffix("년").toInt()
        val month = binding.pickerMonth.value
        val day = binding.pickerDay.value

        val date = LocalDate.of(year, month, 1)
        val maxDay = date.lengthOfMonth()

        binding.pickerDay.maxValue = maxDay
        if (day > maxDay) {
            binding.pickerDay.value = maxDay
        }
    }

    private fun updateSelectedDate() {
        val year = binding.tvYear.text.toString().removeSuffix("년").toInt()
        val month = binding.pickerMonth.value
        val day = binding.pickerDay.value
        selectedTargetDate = LocalDate.of(year, month, day)
    }

    private fun setupListeners() {
        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.layoutYearSelector.setOnClickListener {
            showYearPickerDialog()
        }

        binding.pickerMonth.setOnValueChangedListener { _, _, _ -> onDateChanged() }
        binding.pickerDay.setOnValueChangedListener { _, _, _ -> onDateChanged() }

        binding.rgGoalType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_habit -> {
                    selectedGoalType = GoalType.HABIT
                    binding.layoutFrequency.visibility = View.VISIBLE
                }
                R.id.rb_achievement -> {
                    selectedGoalType = GoalType.ACHIEVEMENT
                    binding.layoutFrequency.visibility = View.GONE
                }
            }
        }

        binding.rgFrequency.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_daily -> {
                    targetDaysPerWeek = 7
                    binding.layoutCustomFrequency.visibility = View.GONE
                }
                R.id.rb_five_times -> {
                    targetDaysPerWeek = 5
                    binding.layoutCustomFrequency.visibility = View.GONE
                }
                R.id.rb_three_times -> {
                    targetDaysPerWeek = 3
                    binding.layoutCustomFrequency.visibility = View.GONE
                }
                R.id.rb_custom -> {
                    binding.layoutCustomFrequency.visibility = View.VISIBLE
                }
            }
        }

        binding.btnAddChecklistItem.setOnClickListener {
            addChecklistItemView()
        }

        binding.btnSaveGoalCapsule.setOnClickListener {
            saveGoalCapsule()
        }
    }

    private fun addChecklistItemView() {
        val itemView = layoutInflater.inflate(R.layout.item_checklist, binding.checklistContainer, false)
        val etContent = itemView.findViewById<EditText>(R.id.et_checklist_content)
        val btnRemove = itemView.findViewById<ImageButton>(R.id.btn_remove_checklist)

        btnRemove.setOnClickListener {
            binding.checklistContainer.removeView(itemView)
        }

        binding.checklistContainer.addView(itemView)
    }

    private fun getChecklistItems(): List<String> {
        val items = mutableListOf<String>()
        for (i in 0 until binding.checklistContainer.childCount) {
            val itemView = binding.checklistContainer.getChildAt(i)
            val etContent = itemView.findViewById<EditText>(R.id.et_checklist_content)
            val content = etContent.text.toString().trim()
            if (content.isNotEmpty()) {
                items.add(content)
            }
        }
        return items
    }

    private fun saveGoalCapsule() {
        val goalName = binding.etGoalName.text.toString().trim()
        val startMessage = binding.etStartMessage.text.toString().trim()
        val checklistItemsContent = getChecklistItems()

        if (binding.rbCustom.isChecked) {
            val customFreq = binding.etCustomFrequency.text.toString().trim()
            if (customFreq.isEmpty()) {
                Toast.makeText(requireContext(), "주 몇 회인지 입력하세요", Toast.LENGTH_SHORT).show()
                return
            }
            targetDaysPerWeek = customFreq.toIntOrNull() ?: 1
            if (targetDaysPerWeek < 1 || targetDaysPerWeek > 7) {
                Toast.makeText(requireContext(), "1~7 사이의 숫자를 입력하세요", Toast.LENGTH_SHORT).show()
                return
            }
        }

        if (selectedTargetDate.isBefore(LocalDate.now())) {
            Toast.makeText(requireContext(), "과거 날짜는 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
            return
        }

        when {
            goalName.isEmpty() -> {
                Toast.makeText(requireContext(), "목표 이름을 입력하세요", Toast.LENGTH_SHORT).show()
            }
            checklistItemsContent.isEmpty() -> {
                Toast.makeText(requireContext(), "최소 1개의 체크리스트를 추가하세요", Toast.LENGTH_SHORT).show()
            }
            startMessage.isEmpty() -> {
                Toast.makeText(requireContext(), "목표 및 다짐을 작성하세요", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val goalId = UUID.randomUUID().toString()

                val goalCapsule = GoalCapsule(
                    id = goalId,
                    goalName = goalName,
                    description = startMessage,
                    targetDate = selectedTargetDate,
                    startDate = LocalDate.now(),
                    goalType = selectedGoalType,
                    targetDaysPerWeek = if (selectedGoalType == GoalType.HABIT) targetDaysPerWeek else 7,
                    question1 = "",
                    question2 = "",
                    question3 = "",
                    startMessage = startMessage,
                    startPhotoPath = null,
                    status = GoalStatus.ACTIVE,
                    endDate = null,
                    endMessage = null,
                    endPhotoPath = null,
                    color = "#4FC3F7"
                )

                Log.d("CreateGoal", "===== 목표 저장 시작 =====")
                Log.d("CreateGoal", "목표 이름: $goalName")

                viewModel.insertGoalCapsuleWithChecklistAndAnswers(
                    goalCapsule,
                    checklistItemsContent,
                    emptyList<GoalAnswer>()
                )

                Log.d("CreateGoal", "✅ 저장 완료!")

                val typeText = if (selectedGoalType == GoalType.HABIT) "습관형" else "달성형"
                Toast.makeText(requireContext(), "$typeText 목표가 저장되었습니다", Toast.LENGTH_SHORT).show()

                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
