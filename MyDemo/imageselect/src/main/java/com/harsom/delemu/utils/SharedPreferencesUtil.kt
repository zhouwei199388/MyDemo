package com.harsom.delemu.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/*
 * 应用所有的偏好设置管理类
 */
object SharedPreferencesUtil {

    /**
     * 添加一个偏好
     * @param context context
     * @param key key
     * @param object String,int,boolean,float,long,and so on
     */
    fun put(context: Context, key: String, `object`: Any) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()
        if (`object` is String) {
            editor.putString(key, `object`)
        } else if (`object` is Int) {
            editor.putInt(key, `object`)
        } else if (`object` is Boolean) {
            editor.putBoolean(key, `object`)
        } else if (`object` is Float) {
            editor.putFloat(key, `object`)
        } else if (`object` is Long) {
            editor.putLong(key, `object`)
        } else {
            editor.putString(key, `object`.toString())
        }
        editor.apply()
    }

    /**
     * 获取一个偏好
     *
     * @param context       context
     * @param key           SharedPreferences key
     * @param defaultObject 默认值，注意：类型必须和该偏好的类型一致，
     * @param <T>           偏好类型
     * @return 偏好的值，如果没有记录过，返回defaultObject。
    </T> */
    operator fun <T> get(context: Context, key: String, defaultObject: T): T {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        var value: Any? = defaultObject
        if (defaultObject == null || defaultObject is String) {
            value = sp.getString(key, defaultObject as String?)
        } else if (defaultObject is Int) {
            value = sp.getInt(key, (defaultObject as Int?)!!)
        } else if (defaultObject is Boolean) {
            value = sp.getBoolean(key, (defaultObject as Boolean?)!!)
        } else if (defaultObject is Float) {
            value = sp.getFloat(key, (defaultObject as Float?)!!)
        } else if (defaultObject is Long) {
            value = sp.getLong(key, (defaultObject as Long?)!!)
        }
        val result: T
        result = try {
            value as T
        } catch (e: Exception) {
            defaultObject
        }

        return result
    }

    //删除一个偏好
    fun remove(context: Context, key: String) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()
        editor.remove(key)
        editor.apply()
    }
}
