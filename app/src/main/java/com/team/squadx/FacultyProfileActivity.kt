package com.team.squadx

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.team.squadx.databinding.ActivityFacultyProfileBinding

class FacultyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFacultyProfileBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacultyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val facultyId = intent.getStringExtra("facultyId")

        if (facultyId.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid Faculty", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        FirebaseFirestore.getInstance()
            .collection("teachers")
            .document(facultyId)
            .get()
            .addOnSuccessListener { doc ->

                if (!doc.exists()) {
                    Toast.makeText(this, "Teacher not found", Toast.LENGTH_LONG).show()
                    finish()
                    return@addOnSuccessListener
                }

                val name = doc.getString("name") ?: ""
                val dept = doc.getString("department") ?: ""
                val desig = doc.getString("designation") ?: ""
                val email = doc.getString("email") ?: ""
                val phone = doc.getString("phone") ?: ""
                val photo = doc.getString("photoUrl") ?: ""
                val subjects = doc.get("subjects") as? List<*> ?: emptyList<Any>()

                binding.tvName.text = name
                binding.tvDepartment.text = "$dept • $desig"
                binding.tvEmail.text = email
                binding.tvPhone.text = phone
                binding.tvBio.text = subjects.joinToString(", ")

                if (photo.isNotEmpty()) {
                    Glide.with(this)
                        .load(photo)
                        .into(binding.imgFaculty)
                }
            }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnCall.setOnClickListener {
            val phone = binding.tvPhone.text.toString()
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
        }

        binding.btnEmail.setOnClickListener {
            val email = binding.tvEmail.text.toString()
            startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")))
        }
    }


    private fun loadFacultyProfile(facultyId: String) {

        db.collection("teachers")
            .document(facultyId)
            .get()
            .addOnSuccessListener { doc ->

                if (!doc.exists()) {
                    Toast.makeText(this, "Faculty not found", Toast.LENGTH_SHORT).show()
                    finish()
                    return@addOnSuccessListener
                }

                val name = doc.getString("name") ?: ""
                val department = doc.getString("department") ?: ""
                val designation = doc.getString("designation") ?: ""
                val email = doc.getString("email") ?: ""
                val phone = doc.getString("phone") ?: ""
                val photoUrl = doc.getString("photoUrl") ?: ""
                val subjects = doc.get("subjects") as? List<String>

                binding.tvName.text = name
                binding.tvDepartment.text = "$department • $designation"
                binding.tvEmail.text = email
                binding.tvPhone.text = phone
                binding.tvBio.text = subjects?.joinToString(", ") ?: ""

                // Load image
                if (photoUrl.isNotEmpty()) {
                    Glide.with(this)
                        .load(photoUrl)
                        .placeholder(R.drawable.ic_profile)
                        .into(binding.imgFaculty)
                }

                // 📞 Call
                binding.btnCall.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:$phone")
                    startActivity(intent)
                }

                // 📧 Email
                binding.btnEmail.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:$email")
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load faculty", Toast.LENGTH_SHORT).show()
            }
    }
}
