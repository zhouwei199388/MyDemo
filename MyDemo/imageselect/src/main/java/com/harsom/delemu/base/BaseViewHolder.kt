package com.harsom.delemu.base

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Stay on 1/3/16.
 * Powered by www.stay4it.com
 */
abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    init {
        itemView.setOnClickListener { v -> onItemClick(v, adapterPosition) }
    }

    abstract fun onBindViewHolder(position: Int)
    abstract fun onBindViewHolder(position: Int, payloads: List<Any>)
    abstract fun onItemClick(view: View, position: Int)
}
