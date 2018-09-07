package com.harsom.delemu.callback

import com.harsom.delemu.bean.AlbumFolder

/**
 * Created by ZouWei on 2018/7/19.
 */

interface InitAlbumCallback {
    fun onInitFinish(folders: ArrayList<AlbumFolder>?)
    fun onDataNoAvaliable()
}
