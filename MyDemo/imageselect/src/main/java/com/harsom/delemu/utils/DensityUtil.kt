package com.harsom.delemu.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager

import java.lang.reflect.Field

/**
 * @author GavinKwok
 * 获取设备的高度和宽度，以及提供dp和px单位之间的转换
 */
object DensityUtil {

    /**
     * @return 屏幕高度px值
     */
    fun getHeightInPx(context: Context): Float {
        return context.resources.displayMetrics.heightPixels.toFloat()
    }

    /**
     * @return 屏幕宽度px值
     */
    fun getWidthInPx(context: Context): Float {
        return context.resources.displayMetrics.widthPixels.toFloat()
    }

    /**
     * @return 屏幕高度dp值
     */
    fun getHeightInDp(context: Context): Int {
        val height = context.resources.displayMetrics.heightPixels.toFloat()
        return px2dip(context, height)
    }

    /**
     * @return 屏幕宽度dp值
     */
    fun getWidthInDp(context: Context): Int {
        val height = context.resources.displayMetrics.heightPixels.toFloat()
        return px2dip(context, height)
    }

    /**
     * @param dpValue 需要转换的dp值
     * @return 转换后的px值
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        //还可以通过如下代码获得
        //Resources.getSystem().getDisplayMetrics().density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * @param pxValue 需要转换的px值
     * @return 转换后的dp值
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * @param pxValue 需要转换的px值
     * @return 转换后的sp值
     */
    fun px2sp(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * @param spValue 需要转换的sp值
     * @return 转换后的px值
     */
    fun sp2px(context: Context, spValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (spValue * scale + 0.5f).toInt()
    }

    /**
     * 利用反射获取状态栏的高度
     * @param resources getResource()
     * @return the height of status bar
     */
    fun getStatusBarHeight(resources: Resources): Int {
        var height = 0
        try {
            val c = Class.forName("com.android.internal.R\$dimen")
            val o = c.newInstance()
            val field = c.getField("status_bar_height")
            val x = field.get(o) as Int
            height = resources.getDimensionPixelSize(x)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return height
    }

    //获取NavigationBar的高度：
    fun getNavigationBarHeight(context: Context, windowManager: WindowManager): Int {
        var navigationBarHeight = 0
        val rs = context.resources
        val id = rs.getIdentifier("navigation_bar_height", "dimen", "android")
        if (id > 0 && hasSoftKeys(windowManager)) {
            navigationBarHeight = rs.getDimensionPixelSize(id)
        }
        return navigationBarHeight
    }

    /**
     * 判断是否有虚拟按钮
     * @param windowManager
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun hasSoftKeys(windowManager: WindowManager): Boolean {
        val d = windowManager.defaultDisplay


        val realDisplayMetrics = DisplayMetrics()
        d.getRealMetrics(realDisplayMetrics)


        val realHeight = realDisplayMetrics.heightPixels
        val realWidth = realDisplayMetrics.widthPixels


        val displayMetrics = DisplayMetrics()
        d.getMetrics(displayMetrics)


        val displayHeight = displayMetrics.heightPixels
        val displayWidth = displayMetrics.widthPixels


        return realWidth - displayWidth > 0 || realHeight - displayHeight > 0
    }
}
