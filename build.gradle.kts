import com.android.build.gradle.BaseExtension
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.gitlab.arturbosch.detekt.Detekt
import org.jlleitschuh.gradle.ktlint.KtlintPlugin

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    alias(libs.plugins.versionsPlugin)
    alias(libs.plugins.versionsCatalogUpdate)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)

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

    apply<KtlintPlugin>()

    ktlint {
        verbose.set(true)
        android.set(extensions.findByType(BaseExtension::class.java) != null)
        outputToConsole.set(true)
        enableExperimentalRules.set(false)
        reporters { reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML) }
        filter { exclude { it.file.path.contains("generated/") } }
    }
}

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

tasks.register("ktlintFormatAll") {
    group = project.tasks.ktlintFormat.get().group ?: "formatting"
    description = "Reformat all projects with ktlint"

    dependsOn(tasks.ktlintFormat)
    subprojects.forEach { dependsOn(it.tasks.ktlintFormat) }
}

tasks.withType<Detekt> { jvmTarget = "1.8" }

val detektAll by tasks.registering(Detekt::class) {
    description = "Run Detekt for all projects"

    parallel = true
    setSource(rootDir)
    include("**/*.kt", "**/*.kts")
    exclude("**/resources/**", "**/build/**")
    config.setFrom(files("$rootDir/detekt-config.yml"))
    buildUponDefaultConfig = false
}
