package com.team.squadx

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.team.squadx.databinding.ActivityProfileBinding
import java.io.File
import java.io.FileOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // 📸 Image picker
    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data ?: return@registerForActivityResult
                saveImageToLocal(imageUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadStudentProfile()

        binding.imgProfile.setOnClickListener {
            openGallery()
        }
        binding.root.alpha = 0f
        binding.root.translationY = 50f

        binding.root.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .start()

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

    }

    // ---------------- OPEN GALLERY ----------------

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePicker.launch(intent)
    }

    // ---------------- SAVE IMAGE LOCALLY ----------------

    private fun saveImageToLocal(uri: Uri) {

        val uid = auth.currentUser?.uid ?: return

        val inputStream = contentResolver.openInputStream(uri) ?: return
        val file = File(filesDir, "profile_$uid.jpg")
        val outputStream = FileOutputStream(file)

        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()

        val localPath = file.absolutePath

        // Save path in Firestore
        db.collection("users")
            .document(uid)
            .update("photoPath", localPath)
            .addOnSuccessListener {
                loadLocalImage(localPath)
                Toast.makeText(this, "Profile photo updated ✅", Toast.LENGTH_SHORT).show()
            }
    }

    // ---------------- LOAD PROFILE ----------------

    private fun loadStudentProfile() {

        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) return@addOnSuccessListener

                binding.tvName.text = doc.getString("name") ?: "Student"
                binding.tvStudentId.text =
                    "Student ID : ${doc.getString("rollNumber") ?: "N/A"}"

                val photoPath = doc.getString("photoPath")
                if (!photoPath.isNullOrEmpty()) {
                    loadLocalImage(photoPath)
                }
            }
    }

    // ---------------- LOAD IMAGE ----------------

    private fun loadLocalImage(path: String) {
        val file = File(path)
        if (file.exists()) {
            binding.imgProfile.setImageURI(Uri.fromFile(file))
        }
    }

    private fun animateProfileImage() {
        binding.imgProfile.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .withEndAction {
                binding.imgProfile.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }.start()
    }

    private fun animateCards() {

        val views = listOf(
            binding.imgProfile,
            binding.tvName,
            binding.tvStudentId
        )

        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 30f

            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay((index * 80).toLong())
                .setDuration(300)
                .start()
        }
    }


}
