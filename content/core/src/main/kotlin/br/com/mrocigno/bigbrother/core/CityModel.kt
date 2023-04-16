package br.com.mrocigno.bigbrother.core

import androidx.fragment.app.Fragment

class PageData(
    val name: String,
    val creator: (BigBrotherView) -> Fragment
)

class ActivityPageWrapper {

    internal val pages: MutableList<PageData> = mutableListOf()

    fun page(name: String, creator: (BigBrotherView) -> Fragment) {
        pages.add(PageData(name, creator))
    }
}