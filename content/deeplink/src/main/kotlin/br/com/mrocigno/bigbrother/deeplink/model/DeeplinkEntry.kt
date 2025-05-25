package br.com.mrocigno.bigbrother.deeplink.model

import br.com.mrocigno.bigbrother.common.entity.DeeplinkEntity
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkAdapterItem.Companion.ENTRY
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkType.EXTERNAL

internal data class DeeplinkEntry(
    val id: Int = 0,
    val activityName: String = "",
    val exported: Boolean = false,
    val type: DeeplinkType = EXTERNAL,
    val path: String = "",
    val hasView: Boolean = false,
    val hasBrowsable: Boolean = false,
) : DeeplinkAdapterItem {

    override val viewType: Int = ENTRY

    override val comparable: Any
        get() = hashCode()

    constructor(entity: DeeplinkEntity) : this(
        id = entity.id,
        activityName = entity.activityName,
        exported = entity.exported,
        type = DeeplinkType.valueOf(entity.type),
        path = entity.path,
        hasView = entity.hasView,
        hasBrowsable = entity.hasBrowsable
    )

    fun toEntity() = DeeplinkEntity(
        id = id,
        activityName = activityName,
        exported = exported,
        type = type.name,
        path = path,
        hasView = hasView,
        hasBrowsable = hasBrowsable
    )

    companion object {

        fun fromList(list: List<DeeplinkModel>) = list.flatMap { model ->
            model.links.map { link ->
                DeeplinkEntry(
                    activityName = model.activityName.split(".").last(),
                    exported = model.exported,
                    type = model.type,
                    path = link.getFullPath(),
                    hasView = link.hasView,
                    hasBrowsable = link.hasBrowsable
                )
            }
        }.sortedBy {
            it.activityName + it.path
        }
    }
}
