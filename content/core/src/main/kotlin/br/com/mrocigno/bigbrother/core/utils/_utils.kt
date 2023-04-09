package br.com.mrocigno.bigbrother.core.utils

import android.app.Activity
import android.view.View
import br.com.mrocigno.bigbrother.core.R

fun Activity.openBigBrotherBubble() =
    findViewById<View>(R.id.bigbrother).performClick()