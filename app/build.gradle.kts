plugins {
    alias(libs.plugins.android)
    alias(libs.plugins.kotlin)
}

android {
    namespace = "xyz.teamgravity.swipetoaction"
    compileSdk = libs.versions.sdk.compile.get().toInt()

    defaultConfig {
        applicationId = "xyz.teamgravity.swipetoaction"
        minSdk = libs.versions.sdk.min.get().toInt()
        targetSdk = libs.versions.sdk.target.get().toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = libs.versions.java.target.get()
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // material
    implementation(libs.material)

    // constraintlayout
    implementation(libs.constraintlayout)

    // recyclerview
    implementation(libs.recyclerview)

    // core
    implementation(libs.core)

    // appcompat
    implementation(libs.appcompat)

    // activity
    implementation(libs.activity)
}