package com.harsom.delemu.callback

import com.harsom.delemu.bean.AlbumFolder

/**
 * Created by ZouWei on 2018/8/3.
 */
interface onFolderItemListener {
    fun onFolderItemClick(albumFolder: AlbumFolder,position:Int)
}