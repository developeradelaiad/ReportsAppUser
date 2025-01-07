plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp").version("1.9.0-1.0.13")
}

android {
    namespace = "com.example.reportsapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.reportsapp"
        minSdk = 26
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
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.retrofit)
    implementation (libs.converter.gson)
    //Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.1")
    // Biometric
    implementation("androidx.biometric:biometric:1.4.0-alpha02")
    // For fragment-ktx
    implementation ("androidx.fragment:fragment:1.8.5")
    //Navigation Bar
    implementation ("com.github.ismaeldivita:chip-navigation-bar:1.4.0")
    //Glide Compiler
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    // Core Ktor client
    implementation("io.ktor:ktor-client-core:2.3.0")
    // Ktor client engine (CIO or any engine of your choice)
    implementation("io.ktor:ktor-client-cio:2.3.0")
    // Ktor JSON serialization
    implementation("io.ktor:ktor-client-json:2.3.0")
    implementation("io.ktor:ktor-client-serialization:2.3.0")
    // Content Negotiation (this is the key for the issue)
    implementation("io.ktor:ktor-client-content-negotiation:2.3.0")
    // JSON serialization library (Kotlinx)
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.0")
    // For ViewModel and viewModels delegate
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0")
//Work
    implementation ("androidx.work:work-runtime:2.10.0")
    implementation ("androidx.work:work-runtime-ktx:2.10.0")

}