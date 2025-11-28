package com.example.timecapsule

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.timecapsule.databinding.ActivityMainBinding
import com.example.timecapsule.ui.dialog.CongratulationsDialog
import com.example.timecapsule.ui.main.GoalCapsuleViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: GoalCapsuleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        observeCongratulationEvent() // ✨ 축하 이벤트 구독
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)
    }

    /**
     * ✨ ViewModel의 축하 이벤트를 구독하고, D-Day가 된 목표가 있으면 다이얼로그를 띄웁니다.
     */
    private fun observeCongratulationEvent() {
        viewModel.congratulationEvent.observe(this) { goal ->
            // 이미 화면에 다이얼로그가 떠 있는지 확인하여 중복 표시를 방지합니다.
            if (supportFragmentManager.findFragmentByTag(CongratulationsDialog.TAG) == null) {
                val dialog = CongratulationsDialog.newInstance(goal.goalName)
                dialog.show(supportFragmentManager, CongratulationsDialog.TAG)

                // 다이얼로그를 보여준 후, 축하 상태를 업데이트합니다.
                viewModel.markAsCongratulated(goal)
            }
        }
    }
}