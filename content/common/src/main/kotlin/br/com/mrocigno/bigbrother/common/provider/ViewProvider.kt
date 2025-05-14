package br.com.mrocigno.bigbrother.common.provider

import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KProperty

fun <T> id(@IdRes viewId: Int) = ViewProvider<T>(viewId)

class ViewProvider<T>(@IdRes private val viewId: Int) {

    operator fun getValue(parent: RecyclerView.ViewHolder, property: KProperty<*>): T =
        parent.itemView.findViewById(viewId)

    operator fun getValue(parent: View, property: KProperty<*>): T =
        parent.findViewById(viewId)

    operator fun getValue(parent: AppCompatActivity, property: KProperty<*>): T =
        parent.findViewById(viewId)

    operator fun getValue(parent: Fragment, property: KProperty<*>): T =
        parent.requireView().findViewById(viewId)

}