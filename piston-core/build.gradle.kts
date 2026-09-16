plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    // Apply Kotlin Serialization plugin from `gradle/libs.versions.toml`.
    alias(libs.plugins.kotlinPluginSerialization)
    kotlin("jvm")
}

dependencies {
    // Apply the kotlinx bundle of dependencies from the version catalog (`gradle/libs.versions.toml`).
    implementation(libs.bundles.kotlinxEcosystem)
    implementation(project(":piston-noise"))
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib-jdk8"))

    implementation("ch.qos.logback:logback-classic:1.5.32")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
}
repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(25)
}
