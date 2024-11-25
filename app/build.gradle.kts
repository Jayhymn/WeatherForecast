import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

val localProperties = Properties().apply {
    file("../local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

android {
    namespace = "com.wakeupdev.weatherforecast"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.wakeupdev.weatherforecast"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val apiKey = localProperties["API_KEY"] as String? ?: ""
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true // Enable BuildConfig generation
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation (libs.androidx.lifecycle.viewmodel.ktx)

    // for room database
    implementation(libs.androidx.room.runtime)
    implementation(libs.core.ktx)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.play.services.location)
    testImplementation(libs.testng)
    testImplementation(libs.hilt.android.testing)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // for retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    // for dagger hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.lottie)

    implementation(libs.androidx.core.splashscreen)

    testImplementation (libs.androidx.core.testing)

    // For Coroutine testing
    testImplementation (libs.kotlinx.coroutines.test)

    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    //hilt testing
    androidTestImplementation (libs.hilt.android.testing)
    kaptAndroidTest (libs.hilt.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}