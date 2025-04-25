    plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
        id("kotlin-kapt")
    }

android {
    namespace = "com.example.gymuz"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gymuz"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {


    val room_version = "2.6.1" // Use the latest stable version

    implementation ("org.maplibre.gl:android-sdk:11.8.0")
    implementation ("com.google.code.gson:gson:2.11.0")
    implementation ("com.mapbox.maps:android:11.11.0")
    implementation(libs.androidx.room.runtime.v252)  // Use parentheses for functions
    annotationProcessor (libs.androidx.room.compiler.v252)
    kapt("androidx.room:room-compiler:$room_version")
    implementation ("androidx.room:room-runtime:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1") // ⬅ potrzebne dla korutyn


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.drawerlayout)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}