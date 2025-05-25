package br.com.mrocigno.bigbrother.deeplink.model

import androidx.recyclerview.widget.DiffUtil

internal interface DeeplinkAdapterItem {

    val viewType: Int

    val comparable: Any

    class Differ : DiffUtil.ItemCallback<DeeplinkAdapterItem>() {

        override fun areItemsTheSame(oldItem: DeeplinkAdapterItem, newItem: DeeplinkAdapterItem): Boolean =
            oldItem.comparable == newItem.comparable

        override fun areContentsTheSame(oldItem: DeeplinkAdapterItem, newItem: DeeplinkAdapterItem): Boolean =
            false
    }

    companion object {
        const val ENTRY = 0
        const val HEADER = 1
    }
}
