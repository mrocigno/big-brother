package br.com.mrocigno.sandman.core

import androidx.fragment.app.Fragment

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