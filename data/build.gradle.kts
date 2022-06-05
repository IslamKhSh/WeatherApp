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

        buildConfigField("String", "BASE_URL", "\"https://api.openweathermap.org/data/2.5/\"")
        buildConfigField("String", "WEATHER_API_KEY", "\"4bc2322e1657902987ceb2448c4385a1\"")
        buildConfigField("String", "WEATHER_ICON_URL", "\"https://openweathermap.org/img/wn/%s@4x.png\"")
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }

        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
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
    testImplementation(testFixtures(projects.domain))

    implementation(projects.domain)
}
