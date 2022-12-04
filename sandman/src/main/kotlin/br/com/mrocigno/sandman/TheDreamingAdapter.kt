package br.com.mrocigno.sandman

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TheDreamingAdapter(
    activity: FragmentActivity,
    private val data: List<DreamData>,
    private val vortex: VortexView
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = data.size
    override fun createFragment(position: Int) = data[position].creator(vortex)
}

class DreamData(
    val name: String,
    val creator: (VortexView) -> Fragment
)

class DreamWrapper {

    internal val pages: MutableList<DreamData> = mutableListOf()

    fun dream(name: String, creator: (VortexView) -> Fragment) {
        pages.add(DreamData(name, creator))
    }
}