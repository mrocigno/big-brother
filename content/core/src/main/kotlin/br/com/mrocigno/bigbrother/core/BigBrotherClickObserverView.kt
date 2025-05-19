package br.com.mrocigno.bigbrother.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import androidx.fragment.app.Fragment
import br.com.mrocigno.bigbrother.core.utils.lastClickPosition

@SuppressLint("ClickableViewAccessibility")
internal class BigBrotherClickObserverView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        id = R.id.bigbrother_click_observer
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO

        setOnTouchListener { _, event ->
            lastClickPosition = PointF(event.rawX, event.rawY)
            false
        }
    }

    companion object {

        fun getOrCreate(activity: Activity) =
            (get(activity) ?: BigBrotherClickObserverView(activity))
                .takeIf { BigBrother.config.isClickRecorderEnabled }

        fun getOrCreate(fragment: Fragment) =
            (get(fragment) ?: fragment.activity?.let { BigBrotherClickObserverView(it) })
                .takeIf { BigBrother.config.isClickRecorderEnabled }

        fun get(activity: Activity) =
            (activity.findViewById<BigBrotherClickObserverView>(R.id.bigbrother_click_observer))
                .takeIf { BigBrother.config.isClickRecorderEnabled }

        fun get(fragment: Fragment) =
            (fragment.view?.findViewById<BigBrotherClickObserverView>(R.id.bigbrother_click_observer))
                .takeIf { BigBrother.config.isClickRecorderEnabled }
    }
}
