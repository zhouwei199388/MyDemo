package com.harsom.delemu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.harsom.delemu.AlbumRepository
import com.harsom.delemu.ImageSelector
import com.harsom.delemu.R
import com.harsom.delemu.base.BaseListAdapter
import com.harsom.delemu.base.BaseViewHolder
import com.harsom.delemu.bean.AlbumConfig
import com.harsom.delemu.bean.ImageInfo
import com.harsom.delemu.callback.ImageItemListener
import com.harsom.delemu.utils.ToastUtil
import kotlinx.android.synthetic.main.item_image_grid.view.*

/**
 * Created by ZouWei on 2018/7/30.
 */
class AlbumAdapter(val mRequestManager: RequestManager, private val mImageItemListener: ImageItemListener) : BaseListAdapter() {
    private val NORMAL_ITEM = 0
    private val CAMERA_ITEM = 1
    private var mImageInfos: ArrayList<ImageInfo>
    private var mAlbumConfig: AlbumConfig

    init {
        mImageInfos = ArrayList()
        mAlbumConfig = ImageSelector.getConfig()
    }

    fun setData(imageInfos: ArrayList<ImageInfo>) {
        mImageInfos = imageInfos
        notifyDataSetChanged()
    }


    override fun onCreateNormalViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        var view: View
        val width = parent.width / mAlbumConfig.gridColumns
        return if (viewType == NORMAL_ITEM) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_grid, parent, false)
            view.layoutParams.height = width
            AlbumHolder(view)
        } else {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_camera, parent, false)
            view.layoutParams.height = width
            DefHolder(view)
        }

    }

    override fun getDataCount(): Int {
        if (mImageInfos.isNotEmpty()) return mImageInfos.size
        return 0
    }

    override fun getDataViewType(position: Int): Int {
        if (mAlbumConfig.showCamera && position == 0) return CAMERA_ITEM
        return NORMAL_ITEM
    }

    inner class DefHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBindViewHolder(position: Int) {
        }

        override fun onBindViewHolder(position: Int, payloads: List<Any>) {
        }

        override fun onItemClick(view: View, position: Int) {
            mImageItemListener.onCameraItemClick()
        }
    }

    inner class AlbumHolder(itemView: View) : BaseViewHolder(itemView) {

        private fun getItem(position: Int): ImageInfo {
            if (mAlbumConfig.showCamera) return mImageInfos[position - 1]
            return mImageInfos[position]
        }

        override fun onBindViewHolder(position: Int) {
            val imageInfo = getItem(position)
            if (mAlbumConfig.selectModel == ImageSelector.MULTI_MODE) {
                itemView.cb_checkbox.setOnClickListener {
                    if (imageInfo.size.toInt() == 0) {
                        ToastUtil.showToast(itemView.context, "图片已损坏!")
                        itemView.cb_checkbox.isChecked = false
                        return@setOnClickListener
                    }

                    val maxCount = ImageSelector.getConfig().maxCount
                    if (AlbumRepository.getInstance().selectSize >= maxCount) {
                        itemView.cb_checkbox.isChecked = false
                        ToastUtil.showToast(itemView.context, "图片最多选${maxCount}张")
                        return@setOnClickListener
                    }
                    if (imageInfo.isSelected) {
                        itemView.mask.alpha = 0.1f
                        itemView.cb_checkbox.isChecked = false
                        mImageItemListener.onUnSelectedImageClick(imageInfo, position)
                    } else {
                        itemView.mask.alpha = 0.5f
                        itemView.cb_checkbox.isChecked = true
                        mImageItemListener.onSelectedImageClick(imageInfo, mAlbumConfig.maxCount, position)
                    }
                }
                itemView.cb_checkbox.isChecked = imageInfo.isSelected
                itemView.mask.alpha = if (imageInfo.isSelected) 0.5f else 0.1f
                itemView.iv_upload_status.visibility = if (imageInfo.isUploaded) View.VISIBLE else View.GONE

            } else {
                itemView.cb_checkbox.visibility = View.GONE
                itemView.mask.alpha = 0.1f
            }

            mRequestManager.load(mImageInfos[position].path)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.place_holder)
                    .error(R.drawable.ic_image_error)
                    .into(itemView.iv_image)
        }

        override fun onBindViewHolder(position: Int, payloads: List<Any>) {
        }

        override fun onItemClick(view: View, position: Int) {
            var itemPosition = position
            if (mAlbumConfig.showCamera)
                itemPosition--
            mImageItemListener.onImageClick(view, itemPosition, mImageInfos[position], mAlbumConfig.selectModel)
        }

    }
}