package br.com.mrocigno.sandman.utils

import android.animation.Animator
import android.transition.Transition
import android.transition.TransitionValues
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import kotlin.math.roundToInt

internal class CircularRevealTransition : Transition() {

    override fun captureStartValues(transitionValues: TransitionValues) {
        transitionValues.values["visibility"] = transitionValues.view.visibility
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        transitionValues.values["visibility"] = transitionValues.view.visibility
    }

    override fun createAnimator(
        sceneRoot: ViewGroup?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null) return null

        val startVisibility = startValues.values["visibility"] as Int
        val endVisibility = endValues.values["visibility"] as Int

        return if (startVisibility != endVisibility) {
            val animator = endValues.view.circularReveal(endVisibility == View.VISIBLE)

            if (endVisibility != View.GONE) {
                endValues.view.isVisible = true
                animator.doOnEnd { endValues.view.visibility = endVisibility }
            }

            animator
        } else null
    }

    private fun View.circularReveal(isExpanding: Boolean): Animator {
        val centerX = (x + width / 2).roundToInt()
        val centerY = top
        val expandedRadius = (height - centerY).toFloat()
        val (startRadius, endRadius) =
            if (isExpanding) 0f to expandedRadius else expandedRadius to 0f

        return ViewAnimationUtils
            .createCircularReveal(this, centerX, centerY, startRadius, endRadius)
    }
}