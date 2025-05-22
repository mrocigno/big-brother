package br.com.mrocigno.bigbrother.common.utils

import java.lang.reflect.Field
import java.lang.reflect.Proxy


fun <T> Any?.field(name: String): T? =
    this?.javaClass?.findField(name)?.run {
        isAccessible = true
        (get(this@field) as? T).also {
            isAccessible = false
        }
    }

fun Any?.setField(name: String, value: Any?) {
    this?.javaClass?.findField(name)?.run {
        isAccessible = true
        set(this@setField, value)
        isAccessible = false
    }
}

private fun Class<*>.findField(name: String): Field? =
    runCatching { getDeclaredField(name) }
        .recoverCatching { superclass?.findField(name) }
        .getOrNull()

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

class ReflectProxyBuilder<T>(
    private val obj: Any,
    private val clazz: Class<T>
) {

    private val invocations = mutableMapOf<String, (args: Array<Any?>) -> Unit>()

    fun onMethod(name: String, execute: (args: Array<Any?>) -> Unit) = apply {
        invocations[name] = execute
    }

    fun build(): T {
        return Proxy.newProxyInstance(
            clazz.classLoader,
            arrayOf<Class<*>>(clazz)
        ) { proxy, method, args ->
            invocations[method.name]?.invoke(args ?: emptyArray())
            method.invoke(obj, *args)
        } as T
    }
}

// TODO: Refactor
inline fun <reified T> Any.buildProxy(clazz: Class<T>): ReflectProxyBuilder<T> =
    ReflectProxyBuilder(this, clazz)
//{
//    try {
//
//        val proxyWindowManager =
//
//        // Injetar o proxy no contexto da Activity
//        // Isso requer reflexão para acessar o campo mWindowManager do ContextImpl
//        // OU sobrescrever o getSystemService() na Activity
//        val windowManagerField: Field = ContextImpl::class.java.getDeclaredField("mWindowManager")
//        windowManagerField.setAccessible(true)
//        val contextImpl: ContextImpl = getBaseContext() as ContextImpl
//        windowManagerField.set(contextImpl, proxyWindowManager)
//    } catch (e: Exception) {
//        Log.e("WindowManagerProxy", "Erro ao injetar WindowManager", e)
//    }
//}

