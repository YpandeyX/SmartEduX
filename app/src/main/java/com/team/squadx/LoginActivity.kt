package com.team.squadx

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.team.squadx.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var googleClient: GoogleSignInClient

    private val demoEmail = "eduxsmart@gmail.com"
    private val demoPassword = "123456"


    // 🔁 Google Sign-In launcher
    private val googleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.result
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: Exception) {
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupGoogleSignIn()
        setupClicks()

        binding.btnDemo.setOnClickListener {
            loginAsDemoUser()
        }

    }

    // ---------------- CLICK HANDLERS ----------------

    private fun setupClicks() {

        // 🔐 EMAIL / PASSWORD LOGIN
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, pass)
        }

        // 🔵 GOOGLE LOGIN (ONLY FOR EXISTING USERS)
        binding.btnGoogleLogin.setOnClickListener {
            googleLauncher.launch(googleClient.signInIntent)
        }

        // 👉 SIGN UP
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, SetupProfileActivity::class.java))
        }
    }

    // ---------------- EMAIL LOGIN ----------------

    private fun loginUser(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {

                // 🚫 Block unverified emails
                if (!auth.currentUser!!.isEmailVerified) {
                    Toast.makeText(
                        this,
                        "Please verify your email before logging in",
                        Toast.LENGTH_LONG
                    ).show()

                    auth.signOut()
                    return@addOnSuccessListener
                }

                checkUserProfile()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Login Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // ---------------- GOOGLE SIGN-IN SETUP ----------------

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleClient = GoogleSignIn.getClient(this, gso)
    }

    /**
     * 🔴 IMPORTANT LOGIC:
     * - Google login is allowed ONLY if email already exists
     * - New users must signup via email/password first
     */
    private fun firebaseAuthWithGoogle(idToken: String) {

        val googleAccount = GoogleSignIn.getLastSignedInAccount(this)
        val email = googleAccount?.email

        if (email == null) {
            Toast.makeText(this, "Google account error", Toast.LENGTH_SHORT).show()
            return
        }

        // 🔍 Check if email already exists in Firebase Auth
        auth.fetchSignInMethodsForEmail(email)
            .addOnSuccessListener { result ->

                val methods = result.signInMethods ?: emptyList()

                // ❌ NEW USER → BLOCK GOOGLE LOGIN
                if (methods.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Please sign up using Email & Password first",
                        Toast.LENGTH_LONG
                    ).show()

                    auth.signOut()
                    googleClient.signOut()
                    return@addOnSuccessListener
                }

                // ✅ EXISTING USER → ALLOW GOOGLE LOGIN
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        checkUserProfile()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Google login check failed", Toast.LENGTH_SHORT).show()
            }
    }

    // ---------------- PROFILE CHECK ----------------

    private fun checkUserProfile() {
        val uid = auth.currentUser?.uid ?: return

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
                Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
    }

    // ---------------- DEMO Button -------------------
    private fun loginAsDemoUser() {

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(demoEmail, demoPassword)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "$demoEmail is logged in",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Demo login failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

}