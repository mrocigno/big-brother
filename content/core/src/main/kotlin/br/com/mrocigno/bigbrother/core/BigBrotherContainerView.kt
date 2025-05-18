package br.com.mrocigno.bigbrother.core

import android.annotation.SuppressLint
import android.transition.TransitionManager
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.transition.doOnEnd
import androidx.core.transition.doOnStart
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import br.com.mrocigno.bigbrother.common.CircularRevealTransition
import br.com.mrocigno.bigbrother.common.utils.afterMeasure
import br.com.mrocigno.bigbrother.common.utils.getNavigationBarHeight
import br.com.mrocigno.bigbrother.common.utils.statusBarHeight
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import br.com.mrocigno.bigbrother.common.R as CommonR

@SuppressLint("ViewConstructor")
internal class BigBrotherContainerView(
    private val vortex: BigBrotherView
) : FrameLayout(vortex.context) {

    private val activity get() = (context as ContextThemeWrapper).baseContext as FragmentActivity
    private val statusBarHeight = activity.statusBarHeight
    private val navigationBarHeight = activity.getNavigationBarHeight()
    private val parentVG get() = parent as ViewGroup
    private var isAnimationRunning = false
    var isExpanded = false
        private set

    private val tabHeader: TabLayout by lazy { findViewById(R.id.td_tab_layout) }
    private val pager: ViewPager2 by lazy { findViewById(R.id.td_dreams_pager) }

    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            vortex.animateBack()
            remove()
        }
    }

    init {
        setBackgroundResource(CommonR.drawable.bigbrother_content_background)
        inflate(context, R.layout.bigbrother_content_layout, this)
        isInvisible = true
        setPadding(resources.getDimensionPixelSize(CommonR.dimen.bb_spacing_stroke))

        afterMeasure {
            y = statusBarHeight + vortex.height.toFloat()
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                measuredHeight - vortex.height - navigationBarHeight - statusBarHeight
            )
        }

        setupPager()
    }

    fun expand() {
        if (isAnimationRunning) return

        isExpanded = true
        vortex.setBackgroundResource(CommonR.drawable.bigbrother_remove_area_background)

        TransitionManager.beginDelayedTransition(parentVG, CircularRevealTransition())
        isVisible = true

        activity.onBackPressedDispatcher.addCallback(onBackPressed)
    }

    fun collapse() {
        if (isAnimationRunning) return
        onBackPressed.remove()
        isExpanded = false
        vortex.setBackgroundResource(BigBrother.config.iconRes)

        runCatching {
            TransitionManager.beginDelayedTransition(parentVG, CircularRevealTransition().apply {
                doOnStart { isAnimationRunning = true }
                doOnEnd {
                    parentVG.removeView(this@BigBrotherContainerView)
                    isAnimationRunning = false
                }
            })
        }.onFailure {
            parentVG.removeView(this@BigBrotherContainerView)
            isAnimationRunning = false
        }
        isInvisible = true
    }

    private fun setupPager() {
        val list: MutableList<PageData> = mutableListOf()
        BigBrother.getPages(activity::class)?.let(list::addAll)
        list.addAll(BigBrother.getPages())

        pager.offscreenPageLimit = 1
        pager.adapter = TheDreamingAdapter(activity, list, vortex)
        TabLayoutMediator(tabHeader, pager) { tab, position ->
            tab.text = list[position].name
        }.attach()
    }
}

private class TheDreamingAdapter(
    activity: FragmentActivity,
    private val data: List<PageData>,
    private val vortex: BigBrotherView
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = data.size
    override fun createFragment(position: Int) = data[position].creator(vortex)
}