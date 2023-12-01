plugins {
    kotlin("multiplatform") version "1.9.21"
    id("maven-publish")
}

group = "org.koitharu"
version = "1.0"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()
    jvm()
    js()
    mingwX64()
    linuxX64()
    val publicationsFromMainHost =
        listOf(jvm(), js()).map { it.name } + "kotlinMultiplatform"
    publishing {
        publications {
            matching { it.name in publicationsFromMainHost }.all {
                val targetPublication = this@all
                tasks.withType<AbstractPublishToMaven>()
                    .matching { it.publication == targetPublication }
                    .configureEach { onlyIf { findProperty("isMainHost") == "true" } }
            }
        }
    }
    
    sourceSets {
        all {
            languageSettings.optIn("kotlinx.coroutines.InternalCoroutinesApi")
            languageSettings.optIn("kotlin.ExperimentalStdlibApi")
        }
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
    }
}
