package br.com.mrocigno.sandman

import android.transition.TransitionManager
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.transition.doOnEnd
import androidx.core.transition.doOnStart
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TheDreamingView(
    private val vortex: VortexView
) : FrameLayout(ContextThemeWrapper(vortex.context, TheDreaming.config.themeRes)) {

    private val activity get() = (context as ContextThemeWrapper).baseContext as FragmentActivity
    private val statusBarHeight = activity.statusBarHeight
    private val navigationBarHeight = activity.getNavigationBarHeight()
    private val parentVG get() = parent as ViewGroup
    private var isAnimationRunning = false
    var isExpanded = false
        private set

    private val tabHeader: TabLayout get() = findViewById(R.id.td_tab_layout)
    private val viewPager: ViewPager2 get() = findViewById(R.id.td_dreams_vp)

    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            vortex.animateBack()
            remove()
        }
    }

    init {
        setBackgroundResource(R.drawable.the_dreaming_background)
        inflate(activity, R.layout.the_dreaming_layout, this)
        isInvisible = true
        setPadding(resources.getDimensionPixelSize(R.dimen.spacing_stroke))


        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                y = statusBarHeight + vortex.height.toFloat()
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    measuredHeight - vortex.height - navigationBarHeight - statusBarHeight
                )
            }
        })

        val list = mutableListOf<DreamData>()
        TheDreaming.getDream(activity::class)?.let(list::addAll)
        list.addAll(TheDreaming.getNightmares())

        viewPager.adapter = TheDreamingAdapter(activity, list, vortex)
        TabLayoutMediator(tabHeader, viewPager) { tab, position ->
            tab.text = list[position].name
        }.attach()
    }

    fun expand() {
        if (isAnimationRunning) return
        isExpanded = true
        vortex.setBackgroundResource(R.drawable.remove_area_background)

        TransitionManager.beginDelayedTransition(parentVG, CircularRevealTransition())
        isVisible = true

        activity.onBackPressedDispatcher.addCallback(onBackPressed)
    }

    fun collapse() {
        if (isAnimationRunning) return
        onBackPressed.remove()
        isExpanded = false
        vortex.setBackgroundResource(TheDreaming.config.iconRes)

        TransitionManager.beginDelayedTransition(parentVG, CircularRevealTransition().apply {
            doOnStart { isAnimationRunning = true }
            doOnEnd {
                parentVG.removeView(this@TheDreamingView)
                isAnimationRunning = false
            }
        })
        isInvisible = true
    }
}