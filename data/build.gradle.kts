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
            buildConfigField("String", "BASE_URL", "\"https://api.openweathermap.org/data/3.0/\"")
        }

        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            buildConfigField("String", "BASE_URL", "\"https://api.openweathermap.org/data/3.0/\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {

    implementation(libs.bundles.retrofit)
    debugImplementation(libs.chuckerDebug)
    releaseImplementation(libs.chuckerRelease)

    implementation(libs.hiltAndroid)
    kapt(libs.hiltCompiler)

    testImplementation(libs.bundles.unitTest)
    testImplementation(platform(libs.junit))

    implementation(projects.domain)
}