@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    id(libs.plugins.androidLibrary.get().pluginId)
    id(libs.plugins.kotlinAndroid.get().pluginId)
    id(libs.plugins.kotlinKapt.get().pluginId)
}

android {
    compileSdk = AndroidConfig.compileSdkVersion

    defaultConfig {
        minSdk = AndroidConfig.minSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }

        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    buildFeatures { dataBinding = true }

    kapt { correctErrorTypes = true }
}

dependencies {

    api(libs.kotlinStd)
    api(libs.bundles.androidKtx)
    api(libs.bundles.lifecycle)
    api(libs.bundles.basicUi)
    api(libs.bundles.navigationComponent)
    api(libs.timber)
    api(libs.coil)
    implementation(libs.kotlinReflect)

    implementation(libs.hiltAndroid)
    kapt(libs.hiltCompiler)

    api(projects.domain)
    api(projects.data)
}
