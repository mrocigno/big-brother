package br.com.mrocigno.bigbrother.core.utils

import android.app.Activity
import android.view.View
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import br.com.mrocigno.bigbrother.core.R
import kotlin.reflect.KClass

fun Activity.openBigBrotherBubble() =
    findViewById<View>(R.id.bigbrother).performClick()

fun <T: BigBrotherTask> getTask(clazz: KClass<T>): T? =
    BigBrother.tasks.filterIsInstance(clazz.java).firstOrNull()