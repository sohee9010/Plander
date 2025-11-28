package com.example.timecapsule.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.timecapsule.R
import com.example.timecapsule.databinding.FragmentArchiveBinding

class ArchiveFragment : Fragment() {

    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArchiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToggleButtons()
    }

    private fun setupToggleButtons() {
        binding.toggleGroupArchive.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnGaveUp -> showGaveUpList()
                    R.id.btnCompleted -> showCompletedList()
                }
            }
        }
        // 기본으로 '완료' 탭을 선택
        binding.toggleGroupArchive.check(R.id.btnCompleted)
    }

    private fun showGaveUpList() {
        replaceFragment(ArchiveListFragment.newInstance(0)) // 0 for GAVE_UP
    }

    private fun showCompletedList() {
        replaceFragment(ArchiveListFragment.newInstance(1)) // 1 for COMPLETED
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_archive, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}