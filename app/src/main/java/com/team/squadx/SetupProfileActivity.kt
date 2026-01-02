package com.team.squadx

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.team.squadx.databinding.ActivitySetupProfileBinding

class SetupProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnCreateAccount.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            createAccount(name, email, password)
        }
    }

    // ---------------- CREATE ACCOUNT ----------------
    private fun createAccount(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                sendVerificationEmail()
                saveUserProfile(name, email)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
    }

    // ---------------- EMAIL VERIFICATION ----------------
    private fun sendVerificationEmail() {
        auth.currentUser?.sendEmailVerification()
            ?.addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Verification email sent. Please verify before login.",
                    Toast.LENGTH_LONG
                ).show()

                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
    }

    // ---------------- SAVE PROFILE ----------------
    private fun saveUserProfile(name: String, email: String) {
        val uid = auth.currentUser?.uid ?: return

        val user = hashMapOf(
            "name" to name,
            "email" to email,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users").document(uid).set(user)
    }
}
