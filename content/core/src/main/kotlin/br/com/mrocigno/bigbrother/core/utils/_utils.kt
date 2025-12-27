package br.com.mrocigno.bigbrother.core.utils

import android.app.Activity
import android.util.Base64
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import br.com.mrocigno.bigbrother.core.BigBrotherView
import br.com.mrocigno.bigbrother.core.interceptor.BigBrotherKtorInterceptor
import br.com.mrocigno.bigbrother.core.interceptor.BigBrotherOkHttpInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import okhttp3.OkHttpClient
import okio.Buffer
import kotlin.reflect.KClass

fun OkHttpClient.Builder.addBigBrotherInterceptor(vararg blockList: String) =
    addInterceptor(BigBrotherOkHttpInterceptor(*blockList))

fun HttpClient.addBigBrotherInterceptor(vararg blockList: String) =
    plugin(HttpSend).intercept(BigBrotherKtorInterceptor(*blockList))

fun Activity.openBigBrotherBubble() =
    BigBrotherView.get(this).animateToCenter()

fun Activity.closeBigBrotherBubble() =
    BigBrotherView.get(this).animateBack()

fun <T: BigBrotherTask> getBigBrotherTask(clazz: KClass<T>): T? =
    BigBrother.tasks.filterIsInstance(clazz.java).firstOrNull()

internal fun Buffer.toBase64(): String =
    Base64.encodeToString(readByteArray(), Base64.NO_WRAP)