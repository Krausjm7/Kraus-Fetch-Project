plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24" // Tried 2.2.0-5, but was unstable
}

android {
    namespace = "com.krausfetchproject.application"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.krausfetchproject.application"
        minSdk = 36
        targetSdk = 36
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

dependencies {

    // Core Android and Lifecycle support libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Compose essentials and UI platform/BOM
    implementation(libs.androidx.activity.compose)
    implementation(platform("androidx.compose:compose-bom:2024.04.00"))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Additional Compose UI libraries (foundation, units, core UI)
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui-unit")
    implementation("androidx.compose.ui:ui")

    // Extended material icons for richer UI components
    implementation("androidx.compose.material:material-icons-extended")

    // Kotlinx Serialization for JSON parsing
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3") // Tried 1.9.0-64, but was unstable

    // Unit testing libraries
    testImplementation(libs.junit)

    // Android instrumentation testing libraries
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.00"))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug-only tools for UI inspection and manifest testing
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}