package br.com.mrocigno.bigbrother.common.utils

fun <T> Any?.field(name: String): T? =
    runCatching { this?.javaClass?.getDeclaredField(name) }
        .recoverCatching { this?.javaClass?.superclass?.getDeclaredField(name) }
        .getOrNull()?.run {
            isAccessible = true
            (get(this@field) as? T).also {
                isAccessible = false
            }
        }

fun <T> Any?.property(name: String): T? =
    runCatching { this?.javaClass?.getDeclaredMethod("get${name.compatCapitalize()}") }
        .recoverCatching { this?.javaClass?.superclass?.getDeclaredMethod("get${name.compatCapitalize()}") }
        .getOrNull()?.run {
            isAccessible = true
            (invoke(this@property) as? T).also {
                isAccessible = false
            }
        }

fun <T> Any?.method(name: String, vararg args: Any?): T? {
    val argsClazz = args.map { it?.javaClass }.toTypedArray()
    return runCatching { this?.javaClass?.getDeclaredMethod(name, *argsClazz) }
        .recoverCatching { this?.javaClass?.superclass?.getDeclaredMethod(name, *argsClazz) }
        .getOrNull()?.run {
            isAccessible = true
            (invoke(this@method, *args) as? T).also {
                isAccessible = false
            }
        }
}

