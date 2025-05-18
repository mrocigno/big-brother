package br.com.mrocigno.bigbrother.deeplink.plugin

import org.w3c.dom.NamedNodeMap

val NamedNodeMap?.androidName: String?
    get() = this?.getNamedItem("android:name")?.nodeValue

val NamedNodeMap?.androidExported: Boolean
    get() = this?.getNamedItem("android:exported")?.nodeValue.toBoolean()

val NamedNodeMap?.androidScheme: String?
    get() = this?.getNamedItem("android:scheme")?.nodeValue

val NamedNodeMap?.androidHost: String?
    get() = this?.getNamedItem("android:host")?.nodeValue

val NamedNodeMap?.androidPath: String?
    get() = this?.getNamedItem("android:path")?.nodeValue