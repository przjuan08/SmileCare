plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "sv.edu.udb.smilecare"
    compileSdk = 35

    defaultConfig {
        applicationId = "sv.edu.udb.smilecare"
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
    }

    buildFeatures {
        viewBinding = true
    }

    configurations.all {
        resolutionStrategy {
            // Forzar uso de AndroidX
            force("androidx.core:core:1.16.0")
            force("androidx.core:core-ktx:1.16.0")
            force("androidx.appcompat:appcompat:1.7.0")
            force("androidx.fragment:fragment-ktx:1.7.0")

            // Excluir Support Libraries si se incluyen transitivamente
            exclude(group = "com.android.support", module = "support-compat")
            exclude(group = "com.android.support", module = "support-media-compat")
            exclude(group = "com.android.support", module = "support-v4")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.circleimageview)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.lifecycle.service)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)

    // Facebook Login
    implementation(libs.facebook.login) {
        exclude(group = "com.android.support")
        exclude(group = "androidx.legacy", module = "legacy-support-v4")
        exclude(group = "androidx.appcompat", module = "appcompat")
        exclude(group = "androidx.fragment", module = "fragment")
    }

    // Google Sign-In
    implementation(libs.play.services.auth)

    // Loading animations
    implementation(libs.android.spinkit) {
        exclude(group = "com.android.support")
    }

    // Calendar view
    implementation(libs.material.calendarview) {
        exclude(group = "com.android.support")
        exclude(group = "androidx.legacy", module = "legacy-support-v4")
        exclude(group = "androidx.appcompat", module = "appcompat")
        exclude(group = "androidx.fragment", module = "fragment")
    }

    // Image loading
    implementation(libs.glide) {
        exclude(group = "com.android.support")
    }

    implementation(libs.androidx.activity)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}