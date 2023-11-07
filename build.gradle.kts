// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.0.1" apply false
    id("com.android.library") version "8.0.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.21" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("org.jetbrains.kotlin.jvm") version "1.7.20" apply false
}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.android.tools.build:gradle:8.0.1")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.5")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
    }
}
