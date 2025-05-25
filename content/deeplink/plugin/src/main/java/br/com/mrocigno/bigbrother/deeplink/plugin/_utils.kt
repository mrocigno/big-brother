package br.com.mrocigno.bigbrother.deeplink.plugin

import org.w3c.dom.NamedNodeMap

internal val NamedNodeMap?.androidName: String?
    get() = this?.getNamedItem("android:name")?.nodeValue

internal val NamedNodeMap?.androidExported: Boolean
    get() = this?.getNamedItem("android:exported")?.nodeValue.toBoolean()

internal val NamedNodeMap?.androidScheme: String?
    get() = this?.getNamedItem("android:scheme")?.nodeValue

internal val NamedNodeMap?.androidHost: String?
    get() = this?.getNamedItem("android:host")?.nodeValue

internal val NamedNodeMap?.androidPath: String?
    get() = this?.getNamedItem("android:path")?.nodeValue
