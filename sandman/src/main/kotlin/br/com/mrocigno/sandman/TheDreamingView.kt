package br.com.mrocigno.sandman

import android.transition.TransitionManager
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.transition.doOnEnd
import androidx.core.transition.doOnStart
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class TheDreamingView(
    private val vortex: VortexView
) : FrameLayout(vortex.context) {

    private val list: MutableList<DreamData> = mutableListOf()
    private val activity get() = context as FragmentActivity
    private val statusBarHeight = activity.statusBarHeight
    private val navigationBarHeight = activity.getNavigationBarHeight()
    private val parentVG get() = parent as ViewGroup
    private var isAnimationRunning = false
    var isExpanded = false
        private set

    private val tabHeader: TabLayout get() = findViewById(R.id.td_tab_layout)

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

        afterMeasure {
            y = statusBarHeight + vortex.height.toFloat()
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                measuredHeight - vortex.height - navigationBarHeight - statusBarHeight
            )
        }

        TheDreaming.getDream(activity::class)?.let(list::addAll)
        list.addAll(TheDreaming.getNightmares())

        setupHeader()
    }

    fun expand() {
        if (isAnimationRunning) return
        addFragment(tabHeader.selectedTabPosition)

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
                clearFragment()
                parentVG.removeView(this@TheDreamingView)
                isAnimationRunning = false
            }
        })
        isInvisible = true
    }

    private fun setupHeader() {
        list.forEach { data ->
            tabHeader.addTab(tabHeader.newTab().apply {
                contentDescription = data.name
                text = data.name
            })
        }

        tabHeader.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab ?: return
                addFragment(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
            override fun onTabReselected(tab: TabLayout.Tab?) = Unit
        })
    }

    private fun addFragment(position: Int) {
        val data = list[position]
        val fragment = data.creator(vortex)
        (context as AppCompatActivity).supportFragmentManager.apply {
            beginTransaction()
                .replace(R.id.td_dreams_container, fragment, "page ${data.name}")
                .commit()
        }
    }

    private fun clearFragment() {
        (context as FragmentActivity).supportFragmentManager.apply {
            val data = list[tabHeader.selectedTabPosition]
            val fragment = findFragmentByTag("page ${data.name}") ?: return
            beginTransaction()
                .remove(fragment)
                .commit()
        }
    }
}