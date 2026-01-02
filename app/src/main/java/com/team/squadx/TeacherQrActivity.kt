package com.team.squadx

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.team.squadx.databinding.ActivityTeacherQrBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.text.SimpleDateFormat
import java.util.*

class TeacherQrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherQrBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var fusedClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        setupSpinners()

        binding.btnGenerateQr.setOnClickListener {
            generateQrWithLocation()
        }
    }

    // ---------------- SPINNERS ----------------

    private fun setupSpinners() {

        val subjects = listOf("DBMS", "OS", "CN", "DSA", "MATHS")
        val sections = listOf("CSE-A", "CSE-B", "CSE-C", "CSE-D", "IT-A", "IT-B", "ECE-A", "ECE-B", "ECE-C", "ECE(IOT)", "EE-A", "EE-B", "ME-A", "ME-B", "CE-A", "CE-B", "CHE-A", "CHE-B")

        binding.spinnerSubject.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, subjects)

        binding.spinnerSection.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sections)
    }

    // ---------------- QR + LOCATION ----------------

    private fun generateQrWithLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                201
            )
            return
        }

        fusedClient.lastLocation.addOnSuccessListener { location ->
            if (location == null) {
                Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }
            generateQr(location)
        }
    }

    private fun generateQr(location: Location) {

        val teacherId = auth.currentUser?.uid ?: return

        val subject = binding.spinnerSubject.selectedItem.toString().trim().uppercase()
        val section = binding.spinnerSection.selectedItem.toString().trim().uppercase()
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val timestamp = System.currentTimeMillis()

        // 🔑 SESSION ID
        val sessionId = db.collection("sessions").document().id

        // 🔥 SAVE SESSION IN FIRESTORE
        db.collection("sessions")
            .document(sessionId)
            .set(
                mapOf(
                    "teacherId" to teacherId,
                    "subject" to subject,
                    "section" to section,
                    "date" to date,
                    "timestamp" to timestamp,
                    "latitude" to location.latitude,
                    "longitude" to location.longitude,
                    "radius" to 50,               // meters
                    "expiresAt" to timestamp + (2 * 60 * 1000) // 2 min
                )
            )
            .addOnSuccessListener {

                // ✅ QR ONLY CONTAINS sessionId
                val qrData = "TEACHER_QR|sessionId=$sessionId"

                val bitmap = createQrBitmap(qrData)

                binding.imgQr.setImageBitmap(bitmap)
                binding.imgQr.visibility = android.view.View.VISIBLE
                binding.tvInfo.visibility = android.view.View.VISIBLE
                binding.tvInfo.text = "Ask students to scan this QR"

            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to generate QR", Toast.LENGTH_SHORT).show()
            }
    }

    // ---------------- QR BITMAP ----------------

    private fun createQrBitmap(text: String): Bitmap {

        val matrix: BitMatrix =
            MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 600, 600)

        val bitmap = Bitmap.createBitmap(600, 600, Bitmap.Config.RGB_565)

        for (x in 0 until 600) {
            for (y in 0 until 600) {
                bitmap.setPixel(
                    x,
                    y,
                    if (matrix[x, y]) android.graphics.Color.BLACK
                    else android.graphics.Color.WHITE
                )
            }
        }
        return bitmap
    }
}
