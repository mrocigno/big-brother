import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.plugin.publish)
    id("java-library")
    id("kotlin")
    id("java-gradle-plugin")
    id("kotlinx-serialization")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType(KotlinCompile::class.java) {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

group = "io.github.mrocigno.extract.deeplink"
version = project.findProperty("libVersion") ?: "0.0.1"

@Suppress("UnstableApiUsage")
gradlePlugin {
    website = "https://github.com/mrocigno/big-brother"
    vcsUrl = "https://github.com/mrocigno/big-brother"
    plugins {
        create("extractDeeplinkPlugin") {
            id = group.toString()
            implementationClass = "br.com.mrocigno.bigbrother.deeplink.plugin.ExtractDeeplinkPlugin"
            displayName = "Deeplink extractor"
            description = "Extract all deeplinks from AndroidManifest.xml"
            tags = listOf("BigBrother", "Deeplink")
        }
    }
}

dependencies {
    implementation(libs.agp.core)
    implementation(libs.agp.api)
    implementation(libs.kotlin.serialization)
}
