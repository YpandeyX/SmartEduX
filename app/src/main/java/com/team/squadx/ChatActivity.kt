package com.team.squadx

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.team.squadx.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSend.setOnClickListener {
            val msg = binding.etMessage.text.toString().trim()
            if (msg.isNotEmpty()) {
                binding.etMessage.setText("")
                handleChat(msg)
            }
        }
    }

    private fun handleChat(userText: String) {

        Toast.makeText(this, "Thinking...", Toast.LENGTH_SHORT).show()

        // Load all teaching assignments
        db.collection("teaching_assignments").get()
            .addOnSuccessListener { assignDocs ->

                val assignmentText = StringBuilder()

                for (a in assignDocs) {
                    assignmentText.append(
                        "TeacherId=${a["teacherId"]}, Subject=${a["subject"]}, Branch=${a["branch"]}, Section=${a["section"]}\n"
                    )
                }

                // Load all teachers
                db.collection("teachers").get()
                    .addOnSuccessListener { teacherDocs ->

                        val teacherText = StringBuilder()

                        for (t in teacherDocs) {
                            teacherText.append(
                                "${t.id}: Name=${t["name"]}, Email=${t["email"]}, Phone=${t["phone"]}\n"
                            )
                        }

                        val prompt = """
You are a college assistant.

Here is teaching data:
$assignmentText

Here is teacher data:
$teacherText

Student asked: "$userText"

Find the correct teacher and reply in simple English.
"""

                        // Call Gemini
                        Thread {
                            val reply = GeminiApi.call(prompt)

                            runOnUiThread {
                                Toast.makeText(this, reply, Toast.LENGTH_LONG).show()
                            }
                        }.start()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Firestore error", Toast.LENGTH_LONG).show()
            }
    }
}