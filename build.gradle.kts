// FILE: build.gradle.kts (Project: TeamSquadX)
plugins {
    // These versions define the plugins for the whole project
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false

    // This is the Google Services plugin you were missing
    id("com.google.gms.google-services") version "4.4.1" apply false
}