// Root build.gradle.kts (NOT the one inside your app module)

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply true
    id("androidx.navigation.safeargs") version "2.7.5" apply false //ADD THIS
}

