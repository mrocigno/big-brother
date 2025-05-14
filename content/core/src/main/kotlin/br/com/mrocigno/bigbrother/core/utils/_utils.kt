package br.com.mrocigno.bigbrother.core.utils

import android.app.Activity
import br.com.mrocigno.bigbrother.core.BBInterceptor
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import br.com.mrocigno.bigbrother.core.BigBrotherView
import okhttp3.OkHttpClient
import kotlin.reflect.KClass

fun OkHttpClient.Builder.addBigBrotherInterceptor(vararg blockList: String) =
    addInterceptor(BBInterceptor(*blockList))

fun Activity.openBigBrotherBubble() =
    BigBrotherView.get(this).animateToCenter()

fun Activity.closeBigBrotherBubble() =
    BigBrotherView.get(this).animateBack()

fun <T: BigBrotherTask> getBigBrotherTask(clazz: KClass<T>): T? =
    BigBrother.tasks.filterIsInstance(clazz.java).firstOrNull()