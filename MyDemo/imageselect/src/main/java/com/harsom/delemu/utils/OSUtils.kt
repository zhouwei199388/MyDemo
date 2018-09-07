/*
 * Copyright © Qin Yongfang. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harsom.delemu.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager



/**
 * Created on 2016/6/4.
 *
 * @author Qin Yongfang.
 */
object OSUtils {

    /**
     * Hide navigation.
     *
     * @param activity [Activity].
     */
    fun hideNavigation(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 14)
        // Android 14以上可以显示隐藏虚拟导航
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    /**
     * Show navigation.
     *
     * @param activity [Activity].
     */
    fun showNavigation(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 14)
        // Android 14以上可以显示隐藏虚拟导航
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

    /**
     * 随时隐藏状态栏。
     *
     * @param activity [Activity].
     */
    fun hideStatusBar(activity: Activity) {
        //        if (checkDeviceHasNavigationBar(activity)) {
        //            View decorView = activity.getWindow().getDecorView();
        //            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
        //            decorView.setSystemUiVisibility(option);
        //        } else {
        val window = activity.window
        val attrs = window.attributes
        attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.attributes = attrs
        //        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //        }
    }

    /**
     * 随便显示状态栏。
     *
     * @param activity [Activity].
     */
    fun showStatusBar(activity: Activity) {
        if (checkDeviceHasNavigationBar(activity) && Build.VERSION.SDK_INT >= 21) {
            val decorView = activity.window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            activity.window.statusBarColor = Color.TRANSPARENT
        } else {
            val window = activity.window
            val attrs = window.attributes
            attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            window.attributes = attrs
        }
    }

    /**
     * 在onCreate时隐藏状态栏。
     *
     * @param activity [Activity].
     */
    fun hideStatusBarOnCreate(activity: Activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE)//去掉标题栏
        activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    //获取是否存在NavigationBar
    fun checkDeviceHasNavigationBar(context: Context): Boolean {
        var hasNavigationBar = false
        val rs = context.resources
        val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id)
        }
        try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val m = systemPropertiesClass.getMethod("get", String::class.java)
            val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
            if ("1" == navBarOverride) {
                hasNavigationBar = false
            } else if ("0" == navBarOverride) {
                hasNavigationBar = true
            }
        } catch (e: Exception) {

        }

        return hasNavigationBar

    }

    /**
     * 获取虚拟按键的高度
     */
    fun getNavigationBarHeight(context: Context): Int {
        var result = 0
        if (checkDeviceHasNavigationBar(context)) {
            val res = context.resources
            val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId)
            }
        }
        return result
    }

}
