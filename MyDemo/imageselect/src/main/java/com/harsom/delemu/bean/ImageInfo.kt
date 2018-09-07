package com.harsom.delemu.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by ZouWei on 2018/7/18.
 */
data class ImageInfo
(var path: String,
 var displayName: String,
 var addTime: Long
) : Parcelable {
    var id: Long? = null
    var width: Int? = null
    var height: Int? = null
    var isSelected: Boolean = false
    var isUploaded: Boolean = false
    var orientation: Int? = null
    var size: Long = 0

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readLong()) {
        id = parcel.readValue(Long::class.java.classLoader) as? Long
        width = parcel.readValue(Int::class.java.classLoader) as? Int
        height = parcel.readValue(Int::class.java.classLoader) as? Int
        isSelected = parcel.readByte() != 0.toByte()
        isUploaded = parcel.readByte() != 0.toByte()
        orientation = parcel.readValue(Int::class.java.classLoader) as? Int
        size = parcel.readValue(Long::class.java.classLoader) as Long
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(path)
        parcel.writeString(displayName)
        parcel.writeLong(addTime)
        parcel.writeValue(id)
        parcel.writeValue(width)
        parcel.writeValue(height)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeByte(if (isUploaded) 1 else 0)
        parcel.writeValue(orientation)
        parcel.writeValue(size)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageInfo> {
        override fun createFromParcel(parcel: Parcel): ImageInfo {
            return ImageInfo(parcel)
        }

        override fun newArray(size: Int): Array<ImageInfo?> {
            return arrayOfNulls(size)
        }
    }


}
