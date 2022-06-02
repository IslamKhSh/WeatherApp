rootProject.name = "WeatherApp"
include(":app", ":core", ":data", ":domain")

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "android.android.application", "android.android.library" ->
                    useModule("android.android.tools.build:gradle:${requested.version}")
                "androidx.navigation.safeargs.kotlin" ->
                    useModule("androidx.navigation:navigation-safe-args-gradle-plugin:${requested.version}")
                "dagger.hilt.android.plugin" ->
                    useModule("com.google.dagger:hilt-android-gradle-plugin:${requested.version}")
            }
        }
    }
}
