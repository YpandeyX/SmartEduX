package com.team.squadx

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val btnNext: Button = findViewById(R.id.btnNext)
        val btnSkip: TextView = findViewById(R.id.btnSkip)

        val items = listOf(
            OnboardingItem(
                "Smart Attendance",
                "Scan QR to mark attendance",
                R.drawable.onboard_first   // ✅ IMAGE ONLY FIRST SLIDE
            ),
            OnboardingItem(
                "Live Tracking",
                "Real-time attendance updates",
                0
            ),
            OnboardingItem(
                "Secure Login",
                "Login using college email",
                0
            ),
            OnboardingItem(
                "Analytics",
                "View reports and insights",
                0
            )
        )

        viewPager.adapter = OnboardingAdapter(items)

        btnNext.setOnClickListener {
            if (viewPager.currentItem < items.size - 1) {
                viewPager.currentItem += 1
            } else {
                finishOnboarding()
            }
        }

        btnSkip.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun finishOnboarding() {
        val prefs = getSharedPreferences("TeamSquadXPrefs", MODE_PRIVATE)
        prefs.edit().putBoolean("onboarding_done", true).apply()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}

