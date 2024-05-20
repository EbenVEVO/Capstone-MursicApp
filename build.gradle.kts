buildscript{
    val agp_version by extra("8.1.3")
    val kotlin_version by extra("1.9.0")
    val agp_version1 by extra("8.3.0")
    val agp_version2 by extra("8.3.0")
    val agp_version3 by extra("8.1.3")
    dependencies{

        classpath("com.google.gms:google-services:4.4.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    }
    repositories {
        mavenCentral()
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.4.0" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}