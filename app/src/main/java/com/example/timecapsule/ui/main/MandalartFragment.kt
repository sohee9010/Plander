package com.example.timecapsule.ui.main

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.timecapsule.R
import com.example.timecapsule.databinding.FragmentMandalartBinding
import java.io.IOException
import java.util.Calendar

class MandalartFragment : Fragment() {

    private var _binding: FragmentMandalartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MandalartViewModel by activityViewModels {
        MandalartViewModelFactory(requireActivity().application)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                saveMandalartImage()
            } else {
                Toast.makeText(requireContext(), "이미지를 저장하려면 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    private val tips = arrayOf(
        "가장 중요한 최종 목표를 중앙에 적어보세요.",
        "주변 8칸에 최종 목표를 달성하기 위한 하위 목표를 채워보세요.",
        "각 하위 목표를 클릭하면 세부 목표를 설정할 수 있습니다."
    )
    private var tipIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var tipRunnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMandalartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textViews = listOf(
            binding.goal1, binding.goal2, binding.goal3, binding.goal4,
            binding.goal5, binding.goal6, binding.goal7, binding.goal8
        )

        setupMandalartGrid(textViews)
        observeViewModel(textViews)
        setupTipBox()

        viewModel.saveMandalartImage.observe(viewLifecycleOwner) {
            if (it) {
                checkPermissionAndSaveImage()
                viewModel.onSaveImageHandled()
            }
        }
    }

    private fun setupMandalartGrid(textViews: List<TextView>) {
        textViews.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                val subGoalName = viewModel.getSubGoalContent(index)
                if (subGoalName != null) {
                    val bundle = bundleOf("goalName" to subGoalName, "goalIndex" to index)
                    findNavController().navigate(R.id.action_mandalartFragment_to_mandalartDetailFragment, bundle)
                }
            }
        }

        binding.finalGoal.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.updateAndSaveFinalGoal(binding.finalGoal.text.toString())
            }
        }

        binding.mandalartYearText.setOnClickListener {
            showYearSelectionDialog()
        }

        binding.finalGoal.setOnClickListener {
             val goalId = viewModel.getCurrentGoalId()
            if (goalId != null) {
                val bundle = bundleOf("goalId" to goalId)
                findNavController().navigate(R.id.action_mandalartFragment_to_mandalartDetailViewFragment, bundle)
            }
        }
    }

    private fun showYearSelectionDialog() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val numberPicker = NumberPicker(requireContext()).apply {
            minValue = currentYear - 10
            maxValue = currentYear + 10
            value = viewModel.year.value?.toIntOrNull() ?: currentYear
        }

        AlertDialog.Builder(requireContext())
            .setTitle("연도 변경")
            .setView(numberPicker)
            .setPositiveButton("확인") { _, _ ->
                viewModel.updateYear(numberPicker.value.toString())
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun setupTipBox() {
        binding.tipTextSwitcher.setFactory {
            TextView(context).apply {
                textSize = 14f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.on_background))
                gravity = Gravity.CENTER_VERTICAL
            }
        }

        val slideIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        val slideOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
        binding.tipTextSwitcher.inAnimation = slideIn
        binding.tipTextSwitcher.outAnimation = slideOut

        tipRunnable = Runnable {
            tipIndex = (tipIndex + 1) % tips.size
            binding.tipTextSwitcher.setText(tips[tipIndex])
            handler.postDelayed(tipRunnable, 5000) // Change tip every 5 seconds
        }
        handler.postDelayed(tipRunnable, 100) // Start the runnable
    }

    private fun observeViewModel(textViews: List<TextView>) {
        viewModel.finalGoal.observe(viewLifecycleOwner) {
            if (binding.finalGoal.text.toString() != it) {
                binding.finalGoal.setText(it)
            }
        }

        viewModel.year.observe(viewLifecycleOwner) {
            if (binding.mandalartYearText.text.toString() != it) {
                binding.mandalartYearText.text = it
            }
        }

        viewModel.goals.forEachIndexed { index, liveData ->
            liveData.observe(viewLifecycleOwner) {
                if (textViews[index].text.toString() != it) {
                    textViews[index].text = it
                }
            }
        }
    }

    private fun checkPermissionAndSaveImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveMandalartImage()
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    saveMandalartImage()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun saveMandalartImage() {
        val view = binding.mandalartGrid
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(ContextCompat.getColor(requireContext(), R.color.white))
        view.draw(canvas)

        val resolver = requireActivity().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "mandalart_${System.currentTimeMillis()}.png")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/Mandalart")
            }
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            imageUri?.let { uri ->
                resolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    Toast.makeText(requireContext(), "만다라트 이미지를 갤러리에 저장했습니다.", Toast.LENGTH_SHORT).show()
                } ?: throw IOException("Failed to get output stream.")
            } ?: throw IOException("Failed to create new MediaStore record.")
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "이미지 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(tipRunnable)
        _binding = null
    }
}