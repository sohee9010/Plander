package com.example.timecapsule.ui.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.timecapsule.MainActivity
import com.example.timecapsule.databinding.ActivityGoalCompleteBinding
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class GoalCompleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 목표 제목 받아와서 설정하기
        val goalTitle = intent.getStringExtra("GOAL_TITLE") ?: ""
        binding.tvGoalTitle.text = goalTitle

        setupListeners()
        startConfettiAnimation()
    }

    private fun setupListeners() {
        binding.btnAddNewGoal.setOnClickListener {
            // TODO: 새 목표 추가 화면으로 이동하는 코드 구현
            // 예: startActivity(Intent(this, AddGoalActivity::class.java))
            finish()
        }

        binding.btnLater.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun startConfettiAnimation() {
        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
            position = Position.Relative(0.5, 0.3)
        )
        binding.konfettiView.start(party)
    }
}