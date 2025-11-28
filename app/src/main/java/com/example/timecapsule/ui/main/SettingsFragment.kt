package com.example.timecapsule.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.timecapsule.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // 알림 설정
        binding.notificationLayout.setOnClickListener {
            // TODO: 알림 설정 화면으로 이동
        }

        // 테마 설정
        binding.themeLayout.setOnClickListener {
            // TODO: 다크모드 토글
        }

        // 데이터 백업
        binding.backupLayout.setOnClickListener {
            // TODO: 백업 기능
        }

        // 데이터 초기화
        binding.resetLayout.setOnClickListener {
            // TODO: 데이터 초기화 확인 다이얼로그
        }

        // 앱 정보
        binding.appInfoLayout.setOnClickListener {
            // TODO: 앱 정보 화면
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}