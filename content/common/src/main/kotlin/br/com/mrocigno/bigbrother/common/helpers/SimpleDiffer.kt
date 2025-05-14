package br.com.mrocigno.bigbrother.common.helpers

import androidx.recyclerview.widget.DiffUtil

class SimpleDiffer<T : Any> : DiffUtil.ItemCallback<T>() {

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem == newItem

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem == newItem

}