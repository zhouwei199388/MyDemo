package com.harsom.delemu

import android.app.Activity
import android.content.Intent
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import com.harsom.delemu.album.AlbumActivity
import com.harsom.delemu.bean.AlbumConfig

/**
 * Created by ZouWei on 2018/8/1.
 */
object ImageSelector {
    const val SELECTED_RESULT: String = "selected_result"

    const val REQUEST_SELECT_IMAGE: Int = 0x1024
    const val REQUEST_SELECT_VIDEO: Int = 0x8192
    const val REQUEST_SELECT_BATCH_IMPORT: Int = 0x100

    const val REQUEST_OPEN_CAMERA: Int = 0x2048
    const val REQUEST_CROP_IMAGE: Int = 0x4096

    const val ARG_ALBUM_CONFIG: String = "albumConfig"

    const val SINGLE_MODE = 0x0//单选模式
    const val AVATOR_MODE = 0x0//头像选择模式
    const val MULTI_MODE = 0x1//多选模式


    private val mConfig: AlbumConfig = AlbumConfig()

    fun getConfig() = mConfig
    fun setMaxCount(maxCount: Int): ImageSelector {
        mConfig.maxCount = maxCount
        return this
    }

    fun setSelectModel(model: Int): ImageSelector {
        mConfig.selectModel = model
        return this
    }

    fun setShowCamera(isShow: Boolean): ImageSelector {
        mConfig.showCamera = isShow
        return this
    }

    fun setGridColumns(columns: Int): ImageSelector {
        mConfig.gridColumns = columns
        return this
    }


//    fun setToolbarColor(color: Int): ImageSelector {
//        mConfig.toolbarColor = color
//        return this
//    }

    fun setShowOriginalCheckBox(isShow: Boolean): ImageSelector {
        mConfig.showOriginalCheckBox = isShow
        return this
    }


    /**
     * 从Activity跳转选择图片
     */
    fun startSelect(@NonNull activity: Activity) {
        val intent = Intent(activity, AlbumActivity::class.java)
        intent.putExtra(ARG_ALBUM_CONFIG, mConfig)
        activity.startActivityForResult(intent, REQUEST_SELECT_IMAGE)
    }

    /**
     * 从Fragment跳转选择图片
     */
    fun startSelect(@NonNull fragment: Fragment) {
        val intent = Intent(fragment.context, AlbumActivity::class.java)
        intent.putExtra(ARG_ALBUM_CONFIG, mConfig)
        fragment.startActivityForResult(intent, REQUEST_SELECT_IMAGE)
    }

    /**
     * 从Activity跳转批量选择图片
     */
    fun startBuckImport(@NonNull activity: Activity) {
        val intent = Intent(activity, AlbumActivity::class.java)
        intent.putExtra(ARG_ALBUM_CONFIG, mConfig)
        activity.startActivityForResult(intent, REQUEST_SELECT_BATCH_IMPORT)
    }

    /**
     * 从fragment跳转批量选择图片
     */
    fun startBuckImport(@NonNull fragment: Fragment) {
        val intent = Intent(fragment.context, AlbumActivity::class.java);
        intent.putExtra(ARG_ALBUM_CONFIG, mConfig)
        fragment.startActivityForResult(intent, REQUEST_SELECT_BATCH_IMPORT)
    }

}
