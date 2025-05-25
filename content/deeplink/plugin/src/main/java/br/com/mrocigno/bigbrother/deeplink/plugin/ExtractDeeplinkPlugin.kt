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

                val fileName = "bb_deeplinks.json"
                val outputFile = File(buildDir, "intermediates/assets/$variantName/merge${capitalizedVariant}Assets/$fileName")

                val taskName = "extract${capitalizedVariant}DeepLinks"
                val processManifestTaskName = "process${capitalizedVariant}MainManifest"
                val manifestFile = project.tasks.getByName(processManifestTaskName).outputs.files.first {
                    it.absolutePath.contains("/$variantName/") && it.name == "AndroidManifest.xml"
                }

                val task = project.tasks.register(taskName) {
                    it.group = "big-brother"
                    it.description = "Extracts deeplinks from the merged AndroidManifest for the $variantName variant"

                    it.inputs.file(manifestFile)
                    it.outputs.file(outputFile)

                    it.doLast {
                        val encoder = Json {
                            prettyPrint = true
                            encodeDefaults = true
                        }

                        if (!manifestFile.exists()) {
                            println("❌ Manifest file not found: $manifestFile")
                            println("❌ Aborting deeplink extraction")
                            return@doLast
                        }

                        val visitor = ManifestVisitor(manifestFile)

                        outputFile.parentFile.mkdirs()
                        val json = runCatching { encoder.encodeToString(visitor.deepLinks.toList()) }
                            .recoverCatching { it.stackTraceToString() }
                            .getOrElse { "" }
                        outputFile.writeText(json)
                        println("✅ ${visitor.deepLinks.size} deeplinks extracted to: ${outputFile.absolutePath}")
                    }
                }

                project.tasks.matching { it.name == taskName }
                    .configureEach {
                        it.dependsOn(project.tasks.named(processManifestTaskName))
                        it.dependsOn(project.tasks.named("merge${capitalizedVariant}Assets"))
                        it.dependsOn(project.tasks.named("compress${capitalizedVariant}Assets"))
                    }

                project.tasks.matching { it.name == processManifestTaskName }
                    .configureEach { it.finalizedBy(task) }
            }
        }
    }
}
