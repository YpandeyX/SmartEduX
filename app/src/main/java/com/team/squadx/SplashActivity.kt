package com.team.squadx

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoCard = findViewById<androidx.cardview.widget.CardView>(R.id.logoCard)
        val appName = findViewById<android.widget.TextView>(R.id.tvAppName)

        // 🎬 Load animation
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_scale)

        // Start animation
        logoCard.startAnimation(animation)
        appName.startAnimation(animation)

        logoCard.alpha = 1f
        appName.alpha = 1f

        // ⏳ Wait 1.5 sec then route
        Handler(Looper.getMainLooper()).postDelayed({
            routeUser()
        }, 1500)
    }

    private fun routeUser() {
        val user = auth.currentUser

        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            checkUserProfile(user.uid)
        }
    }

    private fun checkUserProfile(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists() && document.getBoolean("profileCompleted") == true) {
                    startActivity(Intent(this, DashboardActivity::class.java))
                } else {
                    startActivity(Intent(this, StudentInformationActivity::class.java))
                }
                finish()
            }
            .addOnFailureListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
    }
}
