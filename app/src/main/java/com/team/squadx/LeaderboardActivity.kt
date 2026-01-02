package com.team.squadx

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class LeaderboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val tv = findViewById<TextView>(R.id.tvLeaderboard)
        val db = FirebaseFirestore.getInstance()

        db.collection("quiz_results")
            .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { docs ->
                val text = StringBuilder()
                for (doc in docs) {
                    text.append("${doc["name"]} - ${doc["score"]}\n")
                }
                tv.text = text.toString()
            }
    }
}
