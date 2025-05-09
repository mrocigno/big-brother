package br.com.mrocigno.bigbrother.common.helpers

import androidx.recyclerview.widget.DiffUtil

class StringDiffer : DiffUtil.ItemCallback<String>() {

    override fun areItemsTheSame(oldItem: String, newItem: String) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: String, newItem: String) =
        oldItem == newItem
}