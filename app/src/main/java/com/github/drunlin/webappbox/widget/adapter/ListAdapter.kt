package com.github.drunlin.webappbox.widget.adapter

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class ListAdapter<T, VH: ListAdapter.ViewHolder<T>> : RecyclerView.Adapter<VH>() {
    var list: List<T>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = list?.size ?: 0

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindViewHolder(holder, list!![position], position)
    }

    open fun onBindViewHolder(holder: VH, data: T, position: Int) {
        holder.data = data
    }

    abstract class ViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var data: T? = null
            set(value) {
                field = value
                onBind(value!!)
            }

        abstract fun onBind(data: T)
    }
}
