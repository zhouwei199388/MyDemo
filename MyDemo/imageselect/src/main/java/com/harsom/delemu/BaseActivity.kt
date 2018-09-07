package com.harsom.delemu

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.githang.statusbar.StatusBarCompat
import com.harsom.delemu.utils.DensityUtil
import kotlinx.android.synthetic.main.layout_appbar.*

/**
 * Created by ZouWei on 2018/8/23.
 */
open class BaseActivity : AppCompatActivity() {
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if (!transparent()) {
                val statusBarHeight = DensityUtil.getStatusBarHeight(resources)
                val contentView = window.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
                contentView.setPadding(0, statusBarHeight, 0, 0)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarCompat.setStatusBarColor(this, Color.WHITE, true)
            }
        }
        iv_back.setOnClickListener { finish() }
        tv_submit.isEnabled = false
    }

    /**
     * Activity 状态栏是否透明通栏
     * @return 默认不透明通栏
     */
    open fun transparent(): Boolean {
        return false
    }

    fun showSelectedCount(count: Int, maxCount: Int) {
        if (count > 0) {
            tv_submit.text = "完成($count/$maxCount)"
        } else {
            tv_submit.text = getText(R.string.btn_submit_text)
        }
        tv_submit.isEnabled = count > 0
    }
}