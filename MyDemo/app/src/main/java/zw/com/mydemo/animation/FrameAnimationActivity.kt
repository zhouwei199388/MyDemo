package zw.com.mydemo.animation

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_animation.*
import zw.com.mydemo.R

/**
 * Created by ZouWei on 2018/9/11.
 *
 * 帧动画   作用对象局限于view
 *
 * 不改变View的属性 只改变View的视图效果
 *
 * 动画效果单一
 *
 *
 */
class FrameAnimationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation)
        //获取控件设定的动画
        val animationDrawable = iv_frame.background as AnimationDrawable
        //启动动画
        start_animation.setOnClickListener {
            animationDrawable.start()
        }
        //暂停动画
        stop_animation.setOnClickListener {
            animationDrawable.stop()
        }
    }
}