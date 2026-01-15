plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.ecorouteapp"
    compileSdk = 36

    buildFeatures {
        compose = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.ecorouteapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
/*
        buildConfigField(
            "Integer",
            "SERVER_PORT",
            "\"${project.findProperty("SERVER_PORT")}\""
        )*/

        buildConfigField(
            "String",
            "BASE_URL",
            "\"${providers.gradleProperty("BASE_URL").getOrElse("")}\""
        )
    }
    println("BASE_URL from gradle = ${project.findProperty("BASE_URL")}")

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }



    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.play.services.location)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.runtime.saved.instance.state)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.transport.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    testImplementation("junit:junit:4.13.2")

    // Kotlin + coroutines
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Mockito (mocki)
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")

    // Arch components (LiveData / ViewModel)
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    debugImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

}
