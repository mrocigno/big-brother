package br.com.mrocigno.sandman.lucien

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import androidx.fragment.app.Fragment
import br.com.mrocigno.sandman.core.utils.lastClickPosition

@SuppressLint("ClickableViewAccessibility")
class ClickObserverView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        id = R.id.click_observer

        setOnTouchListener { _, event ->
            lastClickPosition = PointF(event.rawX, event.rawY)
            false
        }
    }

    companion object {

        fun getOrCreate(activity: Activity) =
            get(activity) ?: ClickObserverView(activity)

        fun getOrCreate(fragment: Fragment) =
            get(fragment) ?: fragment.activity?.let { ClickObserverView(it) }

        fun get(activity: Activity) =
            activity.findViewById<ClickObserverView>(R.id.click_observer)

        fun get(fragment: Fragment) =
            fragment.view?.findViewById<ClickObserverView>(R.id.click_observer)
    }
}