package com.harsom.delemu.bean

import android.os.Parcel
import android.os.Parcelable
import com.harsom.delemu.ImageSelector

/**
 * Created by ZouWei on 2018/8/1.
 */
data class AlbumConfig(
        var maxCount: Int = 9,
        var showCamera: Boolean = false,
        var selectModel: Int = ImageSelector.MULTI_MODE,
        var gridColumns: Int = 3,
        var toolbarColor: Int = -1) : Parcelable {

    var showOriginalCheckBox:Boolean = false

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt()) {
        showOriginalCheckBox = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(maxCount)
        parcel.writeByte(if (showCamera) 1 else 0)
        parcel.writeInt(selectModel)
        parcel.writeInt(gridColumns)
        parcel.writeInt(toolbarColor)
        parcel.writeByte(if (showOriginalCheckBox) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlbumConfig> {
        override fun createFromParcel(parcel: Parcel): AlbumConfig {
            return AlbumConfig(parcel)
        }

        override fun newArray(size: Int): Array<AlbumConfig?> {
            return arrayOfNulls(size)
        }
    }


}