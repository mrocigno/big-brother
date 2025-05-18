package br.com.mrocigno.bigbrother.deeplink.plugin

import com.android.build.gradle.AbstractAppExtension
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ExtractDeeplinkPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.afterEvaluate {
            val android = project.extensions.findByType(AbstractAppExtension::class.java)
                ?: throw IllegalStateException("The deeplink extractor plugin should be applied to an AndroidApplication project")

            val variants = android.applicationVariants
            val buildDir = project.layout.buildDirectory.asFile.get()

            variants.forEach { variant ->
                val variantName = variant.name
                val capitalizedVariant = variantName.replaceFirstChar(Char::uppercase)

                val manifestPath = "${buildDir}/intermediates/merged_manifests/$variantName/process${capitalizedVariant}Manifest/AndroidManifest.xml"
                val outputFile = File(buildDir, "generated/assets/$variantName/deeplinks.json")
                val taskName = "extract${capitalizedVariant}DeepLinks"
                val processManifestTaskName = "process${capitalizedVariant}Manifest"

                val task = project.tasks.register(taskName) {
                    it.group = "big-brother"
                    it.description = "Extracts deeplinks from the merged AndroidManifest for the $variantName variant"

                    it.inputs.file(manifestPath)
                    it.outputs.file(outputFile)

                    android.sourceSets { sourceSets ->
                        sourceSets.findByName(variantName)?.assets {
                            srcDir(outputFile.parentFile)
                        }
                    }

                    it.doLast {
                        val manifestFile = File(manifestPath)
                        val encoder = Json {
                            prettyPrint = true
                        }

                        if (!manifestFile.exists()) {
                            println("❌ Manifest file not found: $manifestPath")
                            println("❌ Aborting deeplink extraction")
                            return@doLast
                        }

                        val visitor = ManifestVisitor(manifestFile)

                        outputFile.parentFile.mkdirs()
                        outputFile.writeText(encoder.encodeToString(visitor.deepLinks))
                        println("✅ ${visitor.deepLinks.size} deeplinks extracted to: ${outputFile.absolutePath}")
                    }
                }

                project.tasks.matching { it.name == taskName }
                    .configureEach {
                        it.dependsOn(project.tasks.named(processManifestTaskName))
                    }

                project.tasks.matching { it.name == processManifestTaskName }
                    .configureEach { it.finalizedBy(task) }
            }
        }
    }
}
