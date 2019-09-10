package com.pedrenrique.githubapp.features.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.pedrenrique.githubapp.features.common.adapter.factory.TypesFactory

open class Adapter(
    private val typesFactory: TypesFactory
) : ListAdapter<ModelHolder, BaseViewHolder<ModelHolder>>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, layout: Int): BaseViewHolder<ModelHolder> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(layout, parent, false)
        return getViewHolderForLayout(layout, view)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getViewHolderForLayout(layout: Int, view: View) =
        typesFactory.holder(layout, view) as BaseViewHolder<ModelHolder>

    override fun onBindViewHolder(holder: BaseViewHolder<ModelHolder>, position: Int) {
        holder.bind(getItemForPosition(position), typesFactory)
    }

    override fun getItemViewType(position: Int) =
        getItemForPosition(position).type(typesFactory)

    fun replace(update: List<ModelHolder>) {
        submitList(update)
    }

    private fun getItemForPosition(position: Int) =
        getItem(position)

    class DiffCallback : DiffUtil.ItemCallback<ModelHolder>() {
        override fun areItemsTheSame(oldItem: ModelHolder, newItem: ModelHolder) =
            oldItem.areItemTheSame(newItem)

        override fun areContentsTheSame(oldItem: ModelHolder, newItem: ModelHolder) =
            oldItem.areContentTheSame(newItem)
    }
}