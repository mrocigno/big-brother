package br.com.mrocigno.bigbrother.deeplink.plugin

import com.android.build.gradle.AbstractAppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class ExtractDeeplinkPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.afterEvaluate {
            val android = project.extensions.getByType(AbstractAppExtension::class.java)
            val variants = android.applicationVariants
            val buildDir = project.layout.buildDirectory.asFile.get()

            variants.forEach { variant ->
                val variantName = variant.name
                val capitalizedVariant = variantName.replaceFirstChar(Char::uppercase)

                val manifestPath = "${buildDir}/intermediates/merged_manifests/$variantName/process${capitalizedVariant}Manifest/AndroidManifest.xml"
                val outputFile = File(buildDir, "generated/assets/$variantName/deeplinks.txt")
                val taskName = "extract${capitalizedVariant}DeepLinks"
                val processManifestTaskName = "process${capitalizedVariant}Manifest"

                android.sourceSets {
                    it.findByName(variantName)?.assets {
                        srcDir(outputFile.parentFile)
                    }
                }

                val task = project.tasks.register(taskName) {
                    it.group = "big-brother"
                    it.description = "Extracts deeplinks from the merged AndroidManifest for the $variantName variant"

                    it.inputs.file(manifestPath)
                    it.outputs.file(outputFile)

                    it.doLast {
                        val manifestFile = File(manifestPath)
                        if (!manifestFile.exists()) {
                            throw RuntimeException("Manifest file not found: $manifestPath")
                        }

                        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(manifestFile)
                        doc.documentElement.normalize()

                        val deepLinks = mutableListOf<String>()
                        val activities = doc.getElementsByTagName("activity")
                        for (i in 0 until activities.length) {
                            val activity = activities.item(i)
                            val filters = activity.childNodes
                            for (j in 0 until filters.length) {
                                val node = filters.item(j)
                                if (node.nodeName != "intent-filter") continue

                                val filter = node
                                val actions = filter.childNodes
                                var hasView = false
                                var hasBrowsable = false
                                val links = mutableListOf<String>()

                                for (k in 0 until actions.length) {
                                    val element = actions.item(k)
                                    when (element.nodeName) {
                                        "action" -> {
                                            if (element.attributes?.getNamedItem("android:name")?.nodeValue == "android.intent.action.VIEW") {
                                                hasView = true
                                            }
                                        }
                                        "category" -> {
                                            if (element.attributes?.getNamedItem("android:name")?.nodeValue == "android.intent.category.BROWSABLE") {
                                                hasBrowsable = true
                                            }
                                        }
                                        "data" -> {
                                            val scheme = element.attributes?.getNamedItem("android:scheme")?.nodeValue ?: ""
                                            val host = element.attributes?.getNamedItem("android:host")?.nodeValue ?: ""
                                            val path = element.attributes?.getNamedItem("android:path")?.nodeValue ?: ""
                                            if (scheme.isNotBlank() && host.isNotBlank()) {
                                                links.add("$scheme://$host$path")
                                            }
                                        }
                                    }
                                }

                                if (hasView && hasBrowsable) {
                                    deepLinks.addAll(links)
                                }
                            }
                        }

                        outputFile.parentFile.mkdirs()
                        outputFile.writeText(deepLinks.joinToString("\n"))
                        println("✅ ${deepLinks.size} deeplinks extracted to: ${outputFile.absolutePath}")
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
