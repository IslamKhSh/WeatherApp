import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.gitlab.arturbosch.detekt.Detekt

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    alias(libs.plugins.versionsPlugin)
    alias(libs.plugins.versionsCatalogUpdate)
    alias(libs.plugins.detekt)

    // just apply in the module level with id only
    alias(libs.plugins.androidApp) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.hilt) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
subprojects {
    tasks.withType<Test> {
        useJUnitPlatform()
    }

    // `libs` is not registered yet so we can't use it as `libs.plugins.detekt`
    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        config = files("$rootDir/detekt-config.yml")
        parallel = true // execute rules in parallel.
        autoCorrect = true // Allow to autoCorrect rules that has a property `autoCorrect: true`
        ignoreFailures = true // the build does not fail when the maxIssues count was reached.
    }
}

tasks.withType<Detekt> { jvmTarget = "1.8" }

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

// disallow updating to non-stable version if you use a stable one
tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

versionCatalogUpdate {
    sortByKey.set(false)
  
    keep {
        keepUnusedVersions.set(false)
        keepUnusedLibraries.set(false)
        keepUnusedPlugins.set(false)
    }
}