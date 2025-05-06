package br.com.mrocigno.bigbrother.core.utils

import android.app.Activity
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import br.com.mrocigno.bigbrother.core.BigBrotherView
import kotlin.reflect.KClass

fun Activity.openBigBrotherBubble() =
    BigBrotherView.get(this).animateToCenter()

fun Activity.closeBigBrotherBubble() =
    BigBrotherView.get(this).animateBack()

fun <T: BigBrotherTask> getTask(clazz: KClass<T>): T? =
    BigBrother.tasks.filterIsInstance(clazz.java).firstOrNull()