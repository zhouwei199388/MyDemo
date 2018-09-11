package zw.com.mydemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.harsom.delemu.ImageSelector
import kotlinx.android.synthetic.main.activity_main.*
import zw.com.mydemo.animation.FrameAnimationActivity
import zw.com.mydemo.animation.TweendAnimationActivity

/**
 * Created by ZouWei on 2018/9/7.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectHead.setOnClickListener {
            ImageSelector.setGridColumns(3)
                    .setSelectModel(ImageSelector.AVATOR_MODE)
                    .setShowCamera(true)
                    .startSelect(this)
        }
        selectPhoto.setOnClickListener {
            ImageSelector.setGridColumns(4)
                    .setSelectModel(ImageSelector.MULTI_MODE)
                    .startSelect(this)
        }

        all_crash.setOnClickListener {
            object : Thread() {
                override fun run() {
                    val test: String? = null
                    Log.d(test, test)
                }
            }.start()
        }

        frame_animation.setOnClickListener {
            startActivity(Intent(this, FrameAnimationActivity::class.java))
        }
        tween_animation.setOnClickListener {
            startActivity(Intent(this, TweendAnimationActivity::class.java))
        }
    }
}