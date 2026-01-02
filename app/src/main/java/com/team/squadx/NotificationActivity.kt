package com.team.squadx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.team.squadx.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val notificationList = mutableListOf<NotificationModel>()
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = NotificationAdapter(notificationList)
        binding.recyclerNotifications.layoutManager = LinearLayoutManager(this)
        binding.recyclerNotifications.adapter = adapter

        loadNotifications()
    }

    private fun loadNotifications() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid).get()
            .addOnSuccessListener { userDoc ->

                // 🔥 FIX HERE
                val userDepartment = userDoc.getString("department") ?: ""
                val userSection = userDoc.getString("section") ?: ""

                db.collection("notification")
                    .orderBy("timestamp")
                    .get()
                    .addOnSuccessListener { snapshot ->

                        notificationList.clear()

                        for (doc in snapshot) {

                            val level = doc.getString("level") ?: ""
                            val notifDepartment = doc.getString("branch") ?: ""
                            val notifSection = doc.getString("section") ?: ""

                            val shouldShow = when (level.lowercase()) {

                                "college" -> true

                                "branch" ->
                                    notifDepartment.equals(userDepartment, true)

                                "section" ->
                                    notifDepartment.equals(userDepartment, true) &&
                                            notifSection.equals(userSection, true)

                                else -> false
                            }

                            if (shouldShow) {
                                notificationList.add(
                                    NotificationModel(
                                        title = doc.getString("title") ?: "",
                                        message = doc.getString("message") ?: "",
                                        timestamp = doc.getLong("timestamp") ?: 0L,
                                        level = level,
                                        branch = notifDepartment,
                                        section = notifSection
                                    )
                                )
                            }
                        }

                        notificationList.sortByDescending { it.timestamp }
                        adapter.notifyDataSetChanged()
                    }
            }
    }
}
