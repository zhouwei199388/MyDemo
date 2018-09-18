package zw.com.mydemo.animation

import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.harsom.delemu.utils.Log
import kotlinx.android.synthetic.main.activity_animation_property.*
import zw.com.mydemo.R

/**
 * Created by ZouWei on 2018/9/11.
 *
 *
 * 特点：
 * 属性动画  不再局限于视图View对象
 *
 * 可自定义各种动画效果
 *
 * Android3.0（API11）后引入
 *
 * 原理：
 * 在一定时间间隔内，通过不断对值进行改变，并不断将该值赋给对象的属性
 * 从而是想对该属性上的动画效果
 *
 */
class PropertyAnimationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation_property)
        btn_value.setOnClickListener {
            val valueAnimator = ValueAnimator.ofInt(btn_value.layoutParams.width, 500)
            valueAnimator.duration = 500
            valueAnimator.addUpdateListener { animator ->
                val currentValue: Int = animator.animatedValue as Int
                Log.d("$currentValue")
                btn_value.layoutParams.width = currentValue
                btn_value.requestLayout()
            }
            valueAnimator.start()

        }
    }
}