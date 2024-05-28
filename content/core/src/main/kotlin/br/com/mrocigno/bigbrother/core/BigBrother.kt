package br.com.mrocigno.bigbrother.core

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

object BigBrother {

    internal val tasks = mutableListOf<BigBrotherTask>()
    internal val config = BigBrotherConfig()

    private val activityPages: HashMap<KClass<out Activity>, List<PageData>> = hashMapOf()
    private val pages: MutableList<PageData> = mutableListOf()

    fun addPage(location: KClass<out Activity>, wrapper: ActivityPageWrapper.() -> Unit) {
        activityPages[location] = ActivityPageWrapper().apply(wrapper).pages
    }

    fun addPage(name: String, creator: (BigBrotherView) -> Fragment) {
        pages.add(PageData(name, creator))
    }

    internal fun getPages(location: KClass<*>) = activityPages[location]

    internal fun getPages() = pages

    fun config(configuration: BigBrotherConfig.() -> Unit) = apply {
        config.apply(configuration)
    }

    fun watch(context: Application, isBubbleEnabled: Boolean = true) {
        if (!isBubbleEnabled) tasks.removeAll { it is BigBrotherWatchTask }
        context.registerActivityLifecycleCallbacks(BigBrotherObserver())
    }
}