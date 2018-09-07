package com.harsom.delemu.utils

import android.content.Context
import android.os.Handler
import android.widget.Toast

object ToastUtil {

    private var mToast: Toast? = null
    private val mHandler = Handler()
    private val DURATION = 1500
    private val r = Runnable {
        if (mToast != null) {
            mToast!!.cancel()
            // Release mToast because mToast hold a context object and the
            // context object will not be will not be released since mToast
            // is static.
            mToast = null
        }
    }

    fun showToast(context: Context?, text: String) {
        // Check context null point.
        if (context == null) {
            return
        }

        mHandler.removeCallbacks(r)
        if (mToast != null) {
            mToast!!.setText(text)
        } else {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        }
        mToast!!.show()
        mHandler.postDelayed(r, DURATION.toLong())
    }

    fun showToast(context: Context?, resId: Int) {
        // Check context null point.
        if (context == null) {
            return
        }
        showToast(context, context.resources.getString(resId))
    }

    fun showNetworkFailToast(context: Context?) {
        if (context == null) {
            return
        }
        showToast(context, "无法连接到服务器，请重试")
    }
}