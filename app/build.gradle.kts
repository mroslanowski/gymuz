plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.gymuz"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gymuz"
        minSdk = 24
        targetSdk = 35
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
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
        )
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}

dependencies {

    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.google.zxing:core:3.4.1")
    implementation(libs.zxing.android.embedded)
    implementation(libs.itext7.core)
    implementation(libs.kernel)
    implementation(libs.layout)
    implementation(libs.android.sdk)
    implementation(libs.gson)
    implementation(libs.android)
    implementation(libs.androidx.room.runtime.v261)
    implementation(libs.androidx.room.ktx)
    //noinspection KaptUsageInsteadOfKsp
    kapt(libs.androidx.room.compiler.v261)

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
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("org.robolectric:robolectric:4.10.3")
    testImplementation ("org.mockito:mockito-core:4.11.0")
    testImplementation ("org.mockito:mockito-inline:4.11.0")
    testImplementation ("androidx.test:core:1.5.0")
    testImplementation ("androidx.test.ext:junit:1.1.5")
    testImplementation ("androidx.fragment:fragment-testing:1.6.1")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
}