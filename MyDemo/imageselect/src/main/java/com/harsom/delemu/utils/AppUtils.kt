package com.harsom.delemu.utils

import android.content.Context


object AppUtils {
    //是否原图
    private val IS_ORIGINAL = "is_original"

    /**
     * 设置是否原图
     *
     * @param context
     * @param isChecked
     */
    fun setOriginal(context: Context, isChecked: Boolean) {
        SharedPreferencesUtil.put(context, IS_ORIGINAL, isChecked)
    }

    /**
     * 获取是否原图
     *
     * @param context
     * @param def
     */
    fun isOriginal(context: Context, def: Boolean): Boolean {
        return SharedPreferencesUtil[context, IS_ORIGINAL, def]
    }
}
