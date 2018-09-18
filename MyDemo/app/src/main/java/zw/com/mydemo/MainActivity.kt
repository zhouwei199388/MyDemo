package zw.com.mydemo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.harsom.delemu.ImageSelector
import com.harsom.delemu.utils.Log
import kotlinx.android.synthetic.main.activity_main.*
import zw.com.mydemo.animation.FrameAnimationActivity
import zw.com.mydemo.animation.PropertyAnimationActivity
import zw.com.mydemo.animation.TweendAnimationActivity

/**
 * Created by ZouWei on 2018/9/7.
 */
class MainActivity : Activity(), View.OnClickListener {
    override fun onClick(v: View?) {
        if (v == null) return
        when (v.id) {
            R.id.selectHead -> {
                ImageSelector.setGridColumns(3)
                        .setSelectModel(ImageSelector.AVATOR_MODE)
                        .setShowCamera(true)
                        .startSelect(this)
            }

            R.id.selectPhoto -> {
                ImageSelector.setGridColumns(4)
                        .setSelectModel(ImageSelector.MULTI_MODE)
                        .startSelect(this)
            }
            R.id.all_crash -> {
                object : Thread() {
                    override fun run() {
                        val test: String? = null
                        Log.d(test!!)
                    }
                }.start()
            }
            R.id.frame_animation -> {
                startActivity(Intent(this, FrameAnimationActivity::class.java))
            }
            R.id.tween_animation -> {
                startActivity(Intent(this, TweendAnimationActivity::class.java))
            }
            R.id.value_animation -> {
                startActivity(Intent(this, PropertyAnimationActivity::class.java))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        selectHead.setOnClickListener(this)
        selectPhoto.setOnClickListener(this)
        all_crash.setOnClickListener(this)
        frame_animation.setOnClickListener(this)
        tween_animation.setOnClickListener(this)
        value_animation.setOnClickListener(this)
        myHanlder.sendEmptyMessage(0)
    }


    override fun onResume() {
        super.onResume()
        myHanlder.sendEmptyMessage(0)
        Log.d("onResume")
    }

    private val myHanlder = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)

            Log.d("handlerMessage")
        }
    }
}