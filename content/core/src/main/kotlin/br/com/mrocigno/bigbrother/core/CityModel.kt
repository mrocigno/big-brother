package br.com.mrocigno.bigbrother.core

import androidx.fragment.app.Fragment

class CityData(
    val name: String,
    val creator: (BigBrotherView) -> Fragment
)

class CityWrapper {

    internal val pages: MutableList<CityData> = mutableListOf()

    fun city(name: String, creator: (BigBrotherView) -> Fragment) {
        pages.add(CityData(name, creator))
    }
}