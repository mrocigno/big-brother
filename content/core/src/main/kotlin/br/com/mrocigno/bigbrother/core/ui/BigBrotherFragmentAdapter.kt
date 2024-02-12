package br.com.mrocigno.bigbrother.core.ui

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import br.com.mrocigno.bigbrother.core.PageData

class BigBrotherFragmentAdapter(
    activity: FragmentActivity,
    private val data: List<PageData>
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = data.size
    override fun createFragment(position: Int) = data[position].creator()
}