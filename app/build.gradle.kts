import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id ("kotlin-parcelize")
    id ("androidx.navigation.safeargs.kotlin")
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
        val mapKey = localProperties["GOOGLE_MAPS_API_KEY"] as String? ?: ""

        buildConfigField("String", "API_KEY", "\"$apiKey\"")

        manifestPlaceholders.putAll(
            mapOf(
                "GOOGLE_MAPS_API_KEY" to mapKey
            )
        )
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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation (libs.androidx.lifecycle.viewmodel.ktx)

    implementation (libs.okhttp.urlconnection)
    implementation (libs.logging.interceptor)

    // for room database
    implementation(libs.androidx.room.runtime)
    implementation(libs.core.ktx)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.play.services.location)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.viewpager2)

    testImplementation(libs.testng)
    testImplementation(libs.junit.jupiter)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation(libs.androidx.fragment.ktx)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    implementation (libs.play.services.location)
    implementation (libs.play.services.maps)
    implementation (libs.play.services.iid)

    // for retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    implementation(libs.lottie)
    implementation (libs.mpandroidchart)

    implementation(libs.androidx.core.splashscreen)
//    implementation (libs.material.calendarview)

    testImplementation (libs.androidx.core.testing)

    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    //hilt
    // for dagger hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
//    kapt(libs.androidx.hilt.compiler)

    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.work)
    testImplementation(libs.hilt.android.testing)

    androidTestImplementation (libs.hilt.android.testing)
    kaptAndroidTest (libs.hilt.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    testImplementation(libs.mockk)
    testImplementation (libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core.testing)
}

kapt {
    correctErrorTypes = true
}