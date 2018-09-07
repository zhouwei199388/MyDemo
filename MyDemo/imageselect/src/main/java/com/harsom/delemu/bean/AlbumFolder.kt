package com.harsom.delemu.bean

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Created by ZouWei on 2018/7/18.
 */
data class AlbumFolder(var path: String? = null) : Parcelable {
    var folderName: String? = null //相册目录名称
    var imageInfos: ArrayList<ImageInfo> = ArrayList() //目录集合
    var cover: ImageInfo? = null //目录封面

    constructor(parcel: Parcel) : this(parcel.readString()) {
        path = parcel.readString()
        folderName = parcel.readString()
        cover = parcel.readParcelable(ImageInfo::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(path)
        parcel.writeString(folderName)
        parcel.writeTypedList(imageInfos)
        parcel.writeParcelable(cover, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlbumFolder> {
        override fun createFromParcel(parcel: Parcel): AlbumFolder {
            return AlbumFolder(parcel)
        }

        override fun newArray(size: Int): Array<AlbumFolder?> {
            return arrayOfNulls(size)
        }
    }
}
