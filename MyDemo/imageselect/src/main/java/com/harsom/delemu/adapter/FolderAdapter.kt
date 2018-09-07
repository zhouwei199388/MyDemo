package com.harsom.delemu.adapter

import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.harsom.delemu.R
import com.harsom.delemu.base.BaseListAdapter
import com.harsom.delemu.base.BaseViewHolder
import com.harsom.delemu.bean.AlbumFolder
import com.harsom.delemu.callback.onFolderItemListener
import kotlinx.android.synthetic.main.item_album_folder_list.view.*

/**
 * Created by ZouWei on 2018/8/3.
 */
class FolderAdapter(val requestManager: RequestManager, val onFolderItemListener: onFolderItemListener) : BaseListAdapter() {
    override fun onCreateNormalViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        mFolderColor = ContextCompat.getColor(parent.context, R.color.divider)
        mNormalColor = ContextCompat.getColor(parent.context, R.color.white)
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_album_folder_list, parent, false)
        return FolderHolder(view)
    }

    private var mSelectPosition: Int = 0
    var mAlbumFolders = ArrayList<AlbumFolder>()
    var mFolderColor: Int = 0
    var mNormalColor: Int = 0

    override fun getDataCount(): Int {
        if (mAlbumFolders.isEmpty()) return 0
        return mAlbumFolders.size
    }

    inner class FolderHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBindViewHolder(position: Int, payloads: List<Any>) {
        }

        override fun onItemClick(view: View, position: Int) {
            itemView.setBackgroundColor(mFolderColor)
            mSelectPosition = position
            onFolderItemListener.onFolderItemClick(mAlbumFolders[position], position)
            notifyDataSetChanged()
        }

        override fun onBindViewHolder(position: Int) {
            val albumFolder: AlbumFolder = mAlbumFolders[position]
            requestManager.load(albumFolder.imageInfos[0].path)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.place_holder)
                    .into(itemView.iv_cover)

            itemView.tv_floder_name.text = albumFolder.folderName
            itemView.tv_folder_size.text = "${albumFolder.imageInfos.size}张"
            if (mSelectPosition == position) {
                itemView.setBackgroundColor(mFolderColor)
            } else {
                itemView.setBackgroundColor(mNormalColor)
            }
        }



    }
}