package com.harsom.delemu.callback

import android.view.View
import com.harsom.delemu.bean.ImageInfo

/**
 * Created by ZouWei on 2018/8/3.
 */
interface ImageItemListener {
    fun onSelectedImageClick(imageInfo: ImageInfo, maxCount: Int, position: Int)

    fun onUnSelectedImageClick(imageInfo: ImageInfo, position: Int)

    fun onCameraItemClick()

    fun onImageClick(view: View, realPosition: Int, imageInfo: ImageInfo, selectModel: Int)
}