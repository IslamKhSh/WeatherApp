@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    `java-library`
    `java-test-fixtures`
    id(libs.plugins.kotlinJVM.get().pluginId)
    id(libs.plugins.kotlinKapt.get().pluginId)
}

dependencies {
    implementation(libs.kotlinStd)
    implementation(libs.bundles.coroutines)
    implementation(libs.hiltCore)
    kapt(libs.hiltCompiler)

    testImplementation(libs.bundles.unitTest)
    testImplementation(platform(libs.junit))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
