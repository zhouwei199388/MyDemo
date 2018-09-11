package zw.com.mydemo.animation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.*
import kotlinx.android.synthetic.main.acitivity_animation_tween.*
import zw.com.mydemo.R

/**
 * Created by ZouWei on 2018/9/11.
 */
class TweendAnimationActivity : AppCompatActivity(), View.OnClickListener {

    private var isJava = true
    override fun onClick(v: View?) {
        if (v == null) return
        when (v.id) {
            R.id.btn_translate -> { //平移动画
                if (isJava) {
                    val translateAnimation = TranslateAnimation(0f, 500f, 0f, 0f)
                    translateAnimation.fillAfter = false //视图是否留在动画完结后的地方
                    translateAnimation.duration = 1000   //动画持续时间
                    translateAnimation.repeatMode = TranslateAnimation.REVERSE //动画重复播放模式 restart：正序重放  reverse:倒序回放
                    translateAnimation.repeatCount = 20  //重复播放次数
                    btn_translate.startAnimation(translateAnimation)

                } else {
                    val translateAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_translate)
                    btn_translate.startAnimation(translateAnimation)
                }
            }
            R.id.btn_scale -> {//缩放动画
                if (isJava) {
                    val scaleAnimation = ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                    scaleAnimation.fillAfter = false //视图是否留在动画完结后的地方
                    scaleAnimation.duration = 1000   //动画持续时间
                    scaleAnimation.repeatMode = TranslateAnimation.REVERSE //动画重复播放模式 restart：正序重放  reverse:倒序回放
                    scaleAnimation.repeatCount = 20  //重复播放次数
                    btn_scale.startAnimation(scaleAnimation)
                } else {
                    val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_scale)
                    btn_scale.startAnimation(scaleAnimation)
                }
            }
            R.id.btn_rotate -> {//旋转动画
                if (isJava) {
                    val rotateAnimation = RotateAnimation(0f, 100f, 0.5f, 0f)
                    rotateAnimation.fillAfter = false //视图是否留在动画完结后的地方
                    rotateAnimation.duration = 1000   //动画持续时间
                    rotateAnimation.repeatMode = TranslateAnimation.REVERSE //动画重复播放模式 restart：正序重放  reverse:倒序回放
                    rotateAnimation.repeatCount = 20  //重复播放次数
                    btn_rotate.startAnimation(rotateAnimation)

                } else {
                    val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_rotate)
                    btn_rotate.startAnimation(rotateAnimation)
                }
            }
            R.id.btn_alpha -> {//渐变动画
                if (isJava) {
                    val alphaAnimation = AlphaAnimation(1f, 0f)
                    alphaAnimation.fillAfter = false //视图是否留在动画完结后的地方
                    alphaAnimation.duration = 1000   //动画持续时间
                    alphaAnimation.repeatMode = TranslateAnimation.REVERSE //动画重复播放模式 restart：正序重放  reverse:倒序回放
                    alphaAnimation.repeatCount = 20  //重复播放次数
                    btn_alpha.startAnimation(alphaAnimation)

                } else {
                    val alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_alpha)
                    btn_alpha.startAnimation(alphaAnimation)
                }
            }
            R.id.isjava -> {
                isJava = !isJava
                isjava.text = if (isJava) getString(R.string.java_animation) else getString(R.string.xml_animation)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_animation_tween)
        btn_translate.setOnClickListener(this)
        btn_scale.setOnClickListener(this)
        btn_rotate.setOnClickListener(this)
        btn_alpha.setOnClickListener(this)
        isjava.setOnClickListener(this)
    }
}