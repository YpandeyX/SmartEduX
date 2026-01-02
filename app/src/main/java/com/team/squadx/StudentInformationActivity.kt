package com.team.squadx

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.team.squadx.databinding.ActivityStudentInformationBinding

class StudentInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentInformationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadStudentName()
        setupDepartmentDropdown()

        binding.btnSubmit.setOnClickListener {
            saveStudentInfo()
        }
    }

    // 🔹 Fetch student name from Firestore
    private fun loadStudentName() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    binding.tvStudentName.text = doc.getString("name") ?: ""
                }
            }
    }

    // 🔹 Department dropdown
    private fun setupDepartmentDropdown() {
        val departments = arrayOf(
            "CSED", "ITCA", "ECED", "EED", "CED", "MED", "CHE"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            departments
        )
        binding.spinnerDepartment.adapter = adapter
    }

    // 🔹 Save student info (ONLY ONCE)
    private fun saveStudentInfo() {
        val department = binding.spinnerDepartment.selectedItem.toString()
        val section = binding.etSection.text.toString().trim()
        val rollNumber = binding.etRollNumber.text.toString().trim()

        if (section.isEmpty() || rollNumber.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = auth.currentUser?.uid ?: return

        val data = mapOf(
            "department" to department,
            "section" to section,
            "rollNumber" to rollNumber,
            "profileCompleted" to true
        )

        db.collection("users").document(uid)
            .update(data)
            .addOnSuccessListener {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
            }
    }
}
