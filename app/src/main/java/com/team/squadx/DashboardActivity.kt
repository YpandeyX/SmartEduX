package com.team.squadx

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.team.squadx.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionScanQr.setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }

        binding.icBell.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        binding.teacherDirectory.setOnClickListener {
            startActivity(Intent(this, FacultyDirectoryActivity::class.java))
        }

        binding.chatBot.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.nav_dashboard -> true
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        calculateAttendance()
    }

    private fun calculateAttendance() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid).get()
            .addOnSuccessListener { userDoc ->
                if (!userDoc.exists()) return@addOnSuccessListener

                val section = userDoc.getString("section") ?: return@addOnSuccessListener
                val subject = "DBMS"

                var totalSessions = 0
                var attendedSessions = 0
                val allTasks = mutableListOf<com.google.android.gms.tasks.Task<*>>()

                db.collection("classes").get()
                    .addOnSuccessListener { dateDocs ->
                        for (dateDoc in dateDocs) {
                            val date = dateDoc.id

                            val sessionTask = dateDoc.reference
                                .collection(subject)
                                .document(section)
                                .collection("sessions")
                                .get()
                                .addOnSuccessListener { sessions ->
                                    totalSessions += sessions.size()

                                    for (session in sessions) {
                                        val attendTask = db.collection("attendance")
                                            .document(date)
                                            .collection(subject)
                                            .document(section)
                                            .collection("sessions")
                                            .document(session.id)
                                            .collection("students")
                                            .document(uid)
                                            .get()
                                            .addOnSuccessListener { doc ->
                                                if (doc.exists()) attendedSessions++
                                            }
                                        allTasks.add(attendTask)
                                    }
                                }
                            allTasks.add(sessionTask)
                        }

                        Tasks.whenAllComplete(allTasks).addOnSuccessListener {
                            val percent =
                                if (totalSessions == 0) 0
                                else (attendedSessions * 100) / totalSessions

                            binding.tvAttendance.text = "$percent%"
                            binding.tvProgress.text = when {
                                percent >= 90 -> "Excellent Progress"
                                percent >= 75 -> "Good Progress"
                                percent >= 60 -> "Average Progress"
                                else -> "Low Attendance"
                            }
                        }
                    }
            }
    }
}
