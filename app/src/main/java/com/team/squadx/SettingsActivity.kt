package com.team.squadx

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.team.squadx.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val auth = FirebaseAuth.getInstance()

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGoogle()

        binding.btnBack.setOnClickListener { finish() }

        binding.btnLogout.setOnClickListener { logout() }

        binding.btnLinkGoogle.setOnClickListener {
            linkGoogleAccount()
        }
    }

    // ---------------- GOOGLE SETUP ----------------

    private fun setupGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Enable button
        binding.btnLinkGoogle.isEnabled = true
        binding.btnLinkGoogle.alpha = 1f
    }

    // ---------------- LINK GOOGLE ----------------

    private fun linkGoogleAccount() {
        val intent = googleSignInClient.signInIntent
        googleLauncher.launch(intent)
    }

    private val googleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode != RESULT_OK) return@registerForActivityResult

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                val account = task.result
                val credential =
                    GoogleAuthProvider.getCredential(account.idToken, null)

                auth.currentUser
                    ?.linkWithCredential(credential)
                    ?.addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Google account linked successfully ✅",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    ?.addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            e.message ?: "Linking failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }

            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }

    // ---------------- LOGOUT ----------------

    private fun logout() {
        auth.signOut()
        googleSignInClient.signOut()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
