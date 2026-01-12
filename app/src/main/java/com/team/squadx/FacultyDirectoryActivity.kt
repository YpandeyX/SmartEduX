package com.team.squadx

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.team.squadx.databinding.ActivityFacultyDirectoryBinding

class FacultyDirectoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFacultyDirectoryBinding
    private val db = FirebaseFirestore.getInstance()
    private val list = mutableListOf<Faculty>()
    private lateinit var adapter: FacultyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacultyDirectoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = FacultyAdapter(list) { faculty ->
            val intent = Intent(this, FacultyProfileActivity::class.java)
            intent.putExtra("facultyId", faculty.id)
            startActivity(intent)
        }

        binding.recyclerFaculty.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.recyclerFaculty.setHasFixedSize(true)
        binding.recyclerFaculty.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        binding.recyclerFaculty.adapter = adapter

        loadFaculty()   // 🔥 IMPORTANT
    }

    private fun loadFaculty() {
        db.collection("teachers")
            .get()
            .addOnSuccessListener { snapshot ->
                list.clear()

                for (doc in snapshot.documents) {
                    val faculty = doc.toObject(Faculty::class.java) ?: continue
                    faculty.id = doc.id   // 🔥 store Firestore ID
                    list.add(faculty)
                }

                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Teachers found: ${list.size}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load faculty", Toast.LENGTH_SHORT).show()
            }
    }


}
