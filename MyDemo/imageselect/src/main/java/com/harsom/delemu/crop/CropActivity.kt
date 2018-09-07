package com.harsom.delemu.crop

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewTreeObserver
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.harsom.delemu.BuildConfig
import com.harsom.delemu.R
import com.harsom.delemu.utils.BitmapUtil
import com.harsom.delemu.utils.Log
import kotlinx.android.synthetic.main.activity_album_crop.*
import kotlinx.android.synthetic.main.layout_appbar.*
import java.io.IOException

/**
 * Created by ZouWei on 2018/8/7.
 */
class CropActivity : AppCompatActivity() {

    companion object {
        const val CROP_RESULT = "cropResult"

        const val ARG_IMAGE_PATH = "imageInfo"
    }

    private lateinit var mImagePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_crop)
        mImagePath = intent.getStringExtra(ARG_IMAGE_PATH)
        tv_title.text = "裁剪"
        setSubmitText("完成", true)
        performLoad()
        iv_back.setOnClickListener { finish() }
        tv_submit.setOnClickListener { cropImage() }
    }


    /**
     * 获取图片尺寸 加载时按合适大小缩放
     */
    private fun performLoad() {
        //得到图片尺寸，合适的缩放图片大小
        if (crop_view.width == 0 && crop_view.height == 0) {
            if (!crop_view.viewTreeObserver.isAlive) {
                return
            }
            crop_view.viewTreeObserver.addOnGlobalLayoutListener(
                    object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            if (crop_view.viewTreeObserver.isAlive) {
                                crop_view.viewTreeObserver.removeGlobalOnLayoutListener(this)
                            }
                            loadImage()
                        }
                    }
            )
            return
        }
        loadImage()
    }

    /**
     * 加载图片
     */
    private fun loadImage() {
        Glide.with(applicationContext)
                .load(mImagePath)
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .transform(FillViewportTransformation(
                        Glide.get(applicationContext).bitmapPool,
                        crop_view.getViewportWidth(),
                        crop_view.getViewportHeight()))
                .into(crop_view)

    }

    private fun setSubmitText(text: String, enable: Boolean) {
        tv_submit.text = text
        tv_submit.isEnabled = enable
    }

    /**
     * 返回裁剪的图片byte[]
     *
     * @param path
     */
    private fun onCropCompleted(path: ByteArray) {
        val intent = Intent()
        intent.putExtra(CROP_RESULT, path)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        Glide.get(applicationContext).clearMemory()
    }

    /**
     * 获取裁剪的图片
     */
    private fun cropImage() {
        try {
            val bitmap = CropView.CropRequest(crop_view)
                    //                    .quality(70)
                    //                    .format(Bitmap.CompressFormat.JPEG)
                    .into()
            if (bitmap != null) {
                onCropCompleted(BitmapUtil.bitmapToByteArray(bitmap, Bitmap.CompressFormat.JPEG, 70))
                bitmap.recycle()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            if (BuildConfig.DEBUG) {
                android.util.Log.e("tag", "Error save cropImage file")
                Log.e("Error save cropImage file")
            }
        }

    }

    /**
     * 按合适比例加载图片
     */
    internal class FillViewportTransformation(bitmapPool: BitmapPool, private val viewportWidth: Int, private val viewportHeight: Int) : BitmapTransformation(bitmapPool) {

        override fun transform(bitmapPool: BitmapPool, source: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
            val sourceWidth = source.width
            val sourceHeight = source.height

            val target = computeTargetSize(sourceWidth, sourceHeight, viewportWidth, viewportHeight)

            val targetWidth = target.width()
            val targetHeight = target.height()

            return Bitmap.createScaledBitmap(
                    source,
                    targetWidth,
                    targetHeight,
                    true)
        }

        override fun getId(): String {
            return javaClass.name
        }

        private fun computeTargetSize(sourceWidth: Int, sourceHeight: Int, viewportWidth: Int, viewportHeight: Int): Rect {

            if (sourceWidth == viewportWidth && sourceHeight == viewportHeight) {
                return Rect(0, 0, viewportWidth, viewportHeight) // Fail fast for when source matches exactly on viewport
            }

            val scale: Float = if (sourceWidth * viewportHeight > viewportWidth * sourceHeight) {
                viewportHeight.toFloat() / sourceHeight.toFloat()
            } else {
                viewportWidth.toFloat() / sourceWidth.toFloat()
            }
            val recommendedWidth = (sourceWidth * scale + 0.5f).toInt()
            val recommendedHeight = (sourceHeight * scale + 0.5f).toInt()
            return Rect(0, 0, recommendedWidth, recommendedHeight)
        }

    }

}