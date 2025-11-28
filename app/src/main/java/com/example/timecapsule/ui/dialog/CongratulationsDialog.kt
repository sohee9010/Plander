package com.example.timecapsule.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.timecapsule.databinding.DialogCongratulationsBinding

class CongratulationsDialog : DialogFragment() {

    private var _binding: DialogCongratulationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCongratulationsBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent) // 투명 배경
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 원하는 메시지나 이미지를 여기서 설정할 수 있습니다.
        // arguments?.getString("goalTitle")?.let {
        //     binding.tvCongratsMessage.text = "'${it}' 목표 달성을 축하합니다!"
        // }

        binding.btnConfirm.setOnClickListener {
            dismiss() // 확인 버튼 클릭 시 다이얼로그 닫기
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "CongratulationsDialog"

        fun newInstance(goalTitle: String): CongratulationsDialog {
            val args = Bundle()
            args.putString("goalTitle", goalTitle)
            val fragment = CongratulationsDialog()
            fragment.arguments = args
            return fragment
        }
    }
}