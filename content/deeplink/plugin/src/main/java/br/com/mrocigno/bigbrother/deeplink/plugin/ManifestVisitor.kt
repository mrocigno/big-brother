package br.com.mrocigno.bigbrother.deeplink.plugin

import br.com.mrocigno.bigbrother.deeplink.plugin.model.DeeplinkFilterModel
import br.com.mrocigno.bigbrother.deeplink.plugin.model.DeeplinkModel
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class ManifestVisitor(file: File) {

    val deepLinks = mutableListOf<DeeplinkModel>()

    init {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
        doc.documentElement.normalize()

        val activities = doc.getElementsByTagName("activity")
        for (i in 0 until activities.length) {
            val activity = activities.item(i)
            val filters = activity.childNodes

            val activityName = checkNotNull(activity.attributes.androidName)
            val exported = activity.attributes.androidExported
            val links = mutableListOf<DeeplinkFilterModel>()

            for (j in 0 until filters.length) {
                val filter = filters.item(j)
                if (filter.nodeName != "intent-filter") continue

                val actions = filter.childNodes
                val filterModel = DeeplinkFilterModel()

                for (k in 0 until actions.length) {
                    val element = actions.item(k)
                    when (element.nodeName) {
                        "action" -> {
                            if (element.attributes.androidName == "android.intent.action.VIEW") {
                                filterModel.hasView = true
                            }
                        }
                        "category" -> {
                            if (element.attributes.androidName == "android.intent.category.BROWSABLE") {
                                filterModel.hasBrowsable = true
                            }
                        }
                        "data" -> {
                            element.attributes.androidScheme?.also { filterModel.scheme = it }
                            element.attributes.androidHost?.also { filterModel.host = it }
                            element.attributes.androidPath?.also { filterModel.path = it }
                        }
                    }
                }

                if (!filterModel.isEmpty) links.add(filterModel.copy())
            }

            if (links.isEmpty()) continue
            deepLinks.add(DeeplinkModel(activityName, exported, links))
        }
    }
}