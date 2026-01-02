package com.team.squadx

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.team.squadx.databinding.ActivityScanAttendanceBinding
import java.util.concurrent.Executors
import kotlin.math.*
import android.content.Intent


class AttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanAttendanceBinding
    private lateinit var fusedClient: FusedLocationProviderClient

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var isScanning = false

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        binding.cardScanQr.setOnClickListener {
            if (hasCameraPermission()) startScanner()
            else requestCameraPermission()
        }

        binding.btnOpenTeacherQr.setOnClickListener { startActivity(Intent(this, TeacherQrActivity::class.java)) }
    }

    // ---------------- CAMERA ----------------

    private fun startScanner() {
        isScanning = true
        binding.previewView.visibility = View.VISIBLE

        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({

            val provider = providerFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val analysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                processQr(imageProxy)
            }

            provider.unbindAll()
            provider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                analysis
            )

        }, ContextCompat.getMainExecutor(this))
    }

    private fun processQr(imageProxy: ImageProxy) {

        if (!isScanning) {
            imageProxy.close()
            return
        }

        val image = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val input = InputImage.fromMediaImage(
            image,
            imageProxy.imageInfo.rotationDegrees
        )

        BarcodeScanning.getClient().process(input)
            .addOnSuccessListener { barcodes ->
                for (code in barcodes) {
                    isScanning = false
                    handleQr(code.rawValue ?: "")
                    break
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    }

    // ---------------- QR ----------------

    private fun handleQr(data: String) {

        if (!data.startsWith("TEACHER_QR|")) {
            toast("Invalid QR")
            return
        }

        val map = data.split("|")
            .mapNotNull {
                val p = it.split("=")
                if (p.size == 2) p[0] to p[1] else null
            }.toMap()

        val sessionId = map["sessionId"] ?: return
        validateSession(sessionId)
    }

    // ---------------- VALIDATION ----------------

    private fun validateSession(sessionId: String) {

        db.collection("sessions").document(sessionId).get()
            .addOnSuccessListener { doc ->

                if (!doc.exists()) {
                    toast("Session expired")
                    return@addOnSuccessListener
                }

                val expiresAt = doc.getLong("expiresAt") ?: 0L
                if (System.currentTimeMillis() > expiresAt) {
                    toast("Session expired")
                    return@addOnSuccessListener
                }

                val lat = doc.getDouble("latitude")!!
                val lng = doc.getDouble("longitude")!!
                val radius = doc.getLong("radius")!!.toDouble()

                getStudentLocation { location ->

                    val distance = distanceBetween(
                        location.latitude,
                        location.longitude,
                        lat,
                        lng
                    )

                    if (distance > radius) {
                        toast("Outside classroom")
                    } else {
                        markAttendance(sessionId)
                    }
                }
            }
    }

    // ---------------- LOCATION ----------------

    private fun getStudentLocation(callback: (Location) -> Unit) {

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000
        ).build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            toast("Location permission required")
            return
        }

        fusedClient.requestLocationUpdates(
            request,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    fusedClient.removeLocationUpdates(this)
                    callback(result.lastLocation!!)
                }
            },
            Looper.getMainLooper()
        )
    }

    // ---------------- ATTENDANCE ----------------

    private fun markAttendance(sessionId: String) {

        val uid = auth.currentUser!!.uid

        val ref = db.collection("attendance")
            .document(sessionId)
            .collection("students")
            .document(uid)

        ref.get().addOnSuccessListener {
            if (it.exists()) {
                toast("Already marked")
                return@addOnSuccessListener
            }

            ref.set(
                mapOf(
                    "uid" to uid,
                    "time" to System.currentTimeMillis()
                )
            ).addOnSuccessListener {
                toast("Attendance marked ✅")
            }
        }
    }

    // ---------------- UTILS ----------------

    private fun distanceBetween(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {

        val R = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        return 2 * R * atan2(sqrt(a), sqrt(1 - a))
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

    // ---------------- PERMISSION ----------------

    private fun hasCameraPermission() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            102
        )
    }
}
