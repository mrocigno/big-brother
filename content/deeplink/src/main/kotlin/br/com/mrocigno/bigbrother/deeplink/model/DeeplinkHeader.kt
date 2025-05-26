package br.com.mrocigno.bigbrother.deeplink.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.mrocigno.bigbrother.deeplink.model.DeeplinkAdapterItem.Companion.HEADER

internal class DeeplinkHeader(
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
    val showClear: Boolean = false
) : DeeplinkAdapterItem {

    override val viewType: Int = HEADER

    override val comparable: Any
        get() = title
}
