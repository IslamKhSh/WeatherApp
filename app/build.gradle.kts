import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    id(libs.plugins.androidApp.get().pluginId)
    id(libs.plugins.kotlinAndroid.get().pluginId)
    id(libs.plugins.kotlinKapt.get().pluginId)
    id(libs.plugins.hilt.get().pluginId)
    alias(libs.plugins.safeArgs)
}

android {
    compileSdk = AndroidConfig.compileSdkVersion
    buildToolsVersion = AndroidConfig.buildToolsVersion
    defaultConfig {
        applicationId = "com.musala.weatherApp"
        minSdk = AndroidConfig.minSdkVersion
        targetSdk = AndroidConfig.targetSdkVersion
        versionCode = AndroidConfig.versionCode
        versionName = AndroidConfig.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // you must add your key in local.properties file with key `MAPS_API_KEY`
        val mapsApiKey = "${gradleLocalProperties(rootDir)["MAPS_API_KEY"]}"
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey

        // init this field in defaultConfig as it doesn't change from build type to another
        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }

        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

    implementation(libs.bundles.androidKtx)
    implementation(libs.bundles.navigationComponent)
    implementation(libs.splashScreenApi)
    implementation(libs.locationService)
    implementation(libs.googlePlaces)
    implementation(libs.lottie)
    implementation(libs.hiltAndroid)
    kapt(libs.hiltCompiler)

    // testing
    testImplementation(libs.livedataTestHelper)
    testImplementation(libs.bundles.unitTest)
    testImplementation(platform(libs.junit))
    testImplementation(testFixtures(projects.domain))

    implementation(projects.core)
}
