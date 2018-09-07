package com.harsom.delemu.utils

import android.util.Log

/**
 * Created by ZouWei on 2018/9/7.
 */
object Log {
    private var mTag: String = "log"
    fun d(value: String) {
        Log.d(mTag, value)
    }

    fun e(value: String) {
        Log.d(mTag, value)
    }

}