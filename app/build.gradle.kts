// FILE: app/build.gradle.kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")

    // ✅ KSP instead of KAPT (FIX)
    id("com.google.devtools.ksp") version "1.9.23-1.0.20"
}

android {
    namespace = "com.team.squadx"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.team.squadx"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {

    // ---------------- CORE ----------------
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // ---------------- FIREBASE (BOM) ----------------
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // ---------------- CAMERA X ----------------
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    implementation("com.google.guava:guava:32.1.3-android")

    // ---------------- ML KIT QR ----------------
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // ---------------- ZXING ----------------
    implementation("com.google.zxing:core:3.5.2")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // ---------------- GOOGLE SERVICES ----------------
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // ---------------- GLIDE (FIXED) ----------------
    implementation("com.github.bumptech.glide:glide:4.16.0")
    ksp("com.github.bumptech.glide:ksp:4.16.0")

    // ---------------- AI ----------------
    implementation("com.google.ai.client.generativeai:generativeai:0.4.0")

    // ---------------- NETWORK ----------------
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // ---------------- OPTIONAL ----------------
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // ---------------- TESTING ----------------
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}