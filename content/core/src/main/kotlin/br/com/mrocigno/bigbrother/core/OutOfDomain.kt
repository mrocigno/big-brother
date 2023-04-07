package br.com.mrocigno.bigbrother.core

import android.app.Activity
import androidx.fragment.app.Fragment

@Target(AnnotationTarget.CLASS)
annotation class OutOfDomain

val Activity.isOutOfDomain: Boolean get() =
    this::class.annotations.find { it is OutOfDomain } != null

val Fragment.isOutOfDomain: Boolean get() =
    this::class.annotations.find { it is OutOfDomain } != null
