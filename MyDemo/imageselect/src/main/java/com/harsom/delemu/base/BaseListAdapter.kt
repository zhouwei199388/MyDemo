package com.harsom.delemu.base

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.harsom.delemu.R
import com.harsom.delemu.utils.Log


/**
 * Created by Stay on 7/3/16.
 * Powered by www.stay4it.com
 */
abstract class BaseListAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private var isLoadMoreFooterShown: Boolean = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == VIEW_TYPE_LOAD_MORE_FOOTER) {
            onCreateLoadMoreFooterViewHolder(parent)
        } else onCreateNormalViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        Log.d("onBindViewHolder is not payloads")
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            if (isLoadMoreFooterShown && position == itemCount - 1) {
                if (holder.itemView.layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                    val params = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
                    params.isFullSpan = true
                }
            }
            holder.onBindViewHolder(position)
        } else {
            //            Log.d(payloads.get(0).toString());
            holder.onBindViewHolder(position, payloads)
        }
    }

    override fun getItemCount(): Int {
        return getDataCount() + if (isLoadMoreFooterShown) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoadMoreFooterShown && position == itemCount - 1) {
            VIEW_TYPE_LOAD_MORE_FOOTER
        } else getDataViewType(position)
    }

    protected abstract fun onCreateNormalViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
    protected abstract fun getDataCount(): Int
    private fun onCreateLoadMoreFooterViewHolder(parent: ViewGroup): BaseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout
                .widget_pull_to_refresh_footer, parent, false)
        return LoadMoreFooterViewHolder(view)
    }

    protected open fun getDataViewType(position: Int): Int {
        return 0
    }

    private inner class LoadMoreFooterViewHolder(view: View) : BaseViewHolder(view) {

        override fun onBindViewHolder(position: Int) {}

        override fun onBindViewHolder(position: Int, payloads: List<Any>) {

        }

        override fun onItemClick(view: View, position: Int) {

        }
    }

    companion object {

        protected const val VIEW_TYPE_LOAD_MORE_FOOTER = 100
    }
}
