package br.com.mrocigno.sandman

import android.app.Activity

@Target(AnnotationTarget.CLASS)
annotation class NoVortexAllowed

val Activity.isVortexAllowed: Boolean get() =
    this::class.annotations.find { it is NoVortexAllowed } == null