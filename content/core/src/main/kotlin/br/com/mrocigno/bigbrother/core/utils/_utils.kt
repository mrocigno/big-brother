package br.com.mrocigno.bigbrother.core.utils

import android.app.Activity
import br.com.mrocigno.bigbrother.core.BigBrother
import br.com.mrocigno.bigbrother.core.BigBrotherTask
import br.com.mrocigno.bigbrother.core.R
import br.com.mrocigno.bigbrother.core.ui.BigBrotherView
import kotlin.reflect.KClass

fun Activity.openBigBrotherBubble() =
    findViewById<BigBrotherView>(R.id.bigbrother)?.expand()

fun <T: BigBrotherTask> getTask(clazz: KClass<T>): T? =
    BigBrother.tasks.filterIsInstance(clazz.java).firstOrNull()