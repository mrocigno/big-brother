package br.com.mrocigno.bigbrother.core

import android.app.Activity
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

object Oceania {

    var config = BigBrotherConfig()

    private val dreams: HashMap<KClass<out Activity>, List<CityData>> = hashMapOf()
    private val nightmares: MutableList<CityData> = mutableListOf()

    fun addLocation(location: KClass<out Activity>, wrapper: CityWrapper.() -> Unit) {
        dreams[location] = CityWrapper().apply(wrapper).pages
    }

    fun addPage(name: String, creator: (BigBrotherView) -> Fragment) {
        nightmares.add(CityData(name, creator))
    }

    internal fun getDream(location: KClass<*>) = dreams[location]

    internal fun getNightmares() = nightmares
}