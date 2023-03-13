package br.com.mrocigno.sandman.vortex

import android.app.Activity
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

object TheDreaming {

    var config = VortexConfig()

    private val dreams: HashMap<KClass<out Activity>, List<DreamData>> = hashMapOf()
    private val nightmares: MutableList<DreamData> = mutableListOf()

    fun addLocation(location: KClass<out Activity>, wrapper: DreamWrapper.() -> Unit) {
        dreams[location] = DreamWrapper().apply(wrapper).pages
    }

    fun addNightmare(name: String, creator: (VortexView) -> Fragment) {
        nightmares.add(DreamData(name, creator))
    }

    internal fun getDream(location: KClass<*>) = dreams[location]

    internal fun getNightmares() = nightmares
}