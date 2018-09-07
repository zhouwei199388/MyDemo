package com.harsom.delemu.crop

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.Nullable
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import com.harsom.delemu.utils.CommonUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * Created by ZouWei on 2018/8/13.
 */

class CropView : ImageView {


    private val MAX_TOUCH_POINTS = 2
    private lateinit var touchManager: TouchManager
    //遮罩层画笔
    private val viewportPaint = Paint()
    //图片画笔
    private val bitmapPaint = Paint()

    private var bitmap: Bitmap? = null
    //图片的大小
    private val transform = Matrix()

    constructor(context: Context) : super(context) {
        initCropView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initCropView(context, attrs)
    }

    private fun initCropView(context: Context, attrs: AttributeSet?) {
        val config = CropConfig.from(context, attrs)

        touchManager = TouchManager(MAX_TOUCH_POINTS, config)

        bitmapPaint.isFilterBitmap = true
        viewportPaint.color = config.viewportOverlayColor
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (bitmap == null || canvas == null) {
            return
        }
        drawBitmap(canvas)
        drawOverlay(canvas)
    }

    private fun drawBitmap(canvas: Canvas) {
        transform.reset()
        touchManager.applyPositioningAndScale(transform)

        canvas.drawBitmap(bitmap, transform, bitmapPaint)
    }

    private fun drawOverlay(canvas: Canvas) {
        val viewportWidth = touchManager.getViewportWidth()
        val viewportHeight = touchManager.getViewportHeight()
        val left = (width - viewportWidth) / 2
        val top = (height - viewportHeight) / 2

        canvas.drawRect(0f, top.toFloat(), left.toFloat(), (height - top).toFloat(), viewportPaint)
        canvas.drawRect(0f, 0f, width.toFloat(), top.toFloat(), viewportPaint)
        canvas.drawRect((width - left).toFloat(), top.toFloat(), width.toFloat(), (height - top).toFloat(), viewportPaint)
        canvas.drawRect(0f, (height - top).toFloat(), width.toFloat(), height.toFloat(), viewportPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        resetTouchManager()
    }

    @SuppressLint("ResourceType")
    override fun setImageResource(@DrawableRes resId: Int) {
        val bitmap = if (resId > 0)
            BitmapFactory.decodeResource(resources, resId)
        else
            null
        setImageBitmap(bitmap)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        this.bitmap = bm
        resetTouchManager()
        invalidate()
    }

    override fun setImageDrawable(@Nullable drawable: Drawable?) {
        val bitmap: Bitmap?
        bitmap = when {
            drawable is BitmapDrawable -> {
                val bitmapDrawable = drawable as BitmapDrawable?
                bitmapDrawable!!.bitmap
            }
            drawable != null -> asBitmap(drawable, width, height)
            else -> null
        }

        setImageBitmap(bitmap)
    }


    private fun asBitmap(drawable: Drawable, minWidth: Int, minHeight: Int): Bitmap {
        val tmpRect = Rect()
        drawable.copyBounds(tmpRect)
        if (tmpRect.isEmpty) {
            tmpRect.set(0, 0, Math.max(minWidth, drawable.intrinsicWidth), Math.max(minHeight, drawable.intrinsicHeight))
            drawable.bounds = tmpRect
        }
        val bitmap = Bitmap.createBitmap(tmpRect.width(), tmpRect.height(), Bitmap.Config.ARGB_8888)
        drawable.draw(Canvas(bitmap))
        return bitmap
    }

    private fun resetTouchManager() {
        val invalidBitmap = bitmap == null
        val bitmapWidth = if (invalidBitmap) 0 else bitmap!!.width
        val bitmapHeight = if (invalidBitmap) 0 else bitmap!!.height
        touchManager.resetFor(bitmapWidth, bitmapHeight, width, height)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        super.dispatchTouchEvent(event)
        touchManager.onEvent(event!!)
        invalidate()
        return true
    }

    fun crop(): Bitmap? {
        if (bitmap == null) {
            return null
        }

        val src = bitmap
        val srcConfig = src!!.config
        val config = srcConfig ?: Bitmap.Config.ARGB_8888
        val viewportHeight = touchManager.getViewportHeight()
        val viewportWidth = touchManager.getViewportWidth()

        val dst = Bitmap.createBitmap(viewportWidth, viewportHeight, config)

        val canvas = Canvas(dst)
        val left = (width - viewportWidth) / 2
        val top = (height - viewportHeight) / 2
        canvas.translate((-left).toFloat(), (-top).toFloat())

        drawBitmap(canvas)

        return dst
    }

    fun getViewportWidth(): Int {
        return touchManager.getViewportWidth()
    }

    fun getViewportHeight(): Int {
        return touchManager.getViewportHeight()
    }

    class CropRequest(private val cropView: CropView) {
        private var format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
        private var quality = CropConfig.DEFAULT_IMAGE_QUALITY

        init {
            CommonUtils.checkNotNull(cropView, "cropView == null")
        }

        /**
         * Compression format to use, defaults to [Bitmap.CompressFormat.JPEG].
         *
         * @return current request for chaining.
         */
        fun format(format: Bitmap.CompressFormat): CropRequest {
            CommonUtils.checkNotNull(format, "format == null")
            this.format = format
            return this
        }

        /**
         * Compression quality to use (must be 0..100), defaults to {@value CropViewConfig#DEFAULT_IMAGE_QUALITY}.
         *
         * @return current request for chaining.
         */
        fun quality(quality: Int): CropRequest {
            CommonUtils.checkArgument(quality >= 0 && quality <= 100, "quality must be 0..100")
            this.quality = quality
            return this
        }


        //TODO 有必要的话 采用Callable + Future (将此方法在非主线程中执行)

        /**
         * 同步地将裁剪后的bitmap 写入到提供的file中，  如果需要会创建父目录
         *
         *
         * 同步将才将后的bitmap返回给等待的activity  不存入手机了(现在)
         *
         *
         * Must have permissions to write, will be created if doesn't exist or overwrite if it does.
         */
        @Throws(IOException::class)
        fun into(): Bitmap? {
//            flushToFile(croppedBitmap, format, quality, file);
            return cropView.crop()
        }

        @Throws(IOException::class)
        private fun flushToFile(bitmap: Bitmap,
                                format: Bitmap.CompressFormat,
                                quality: Int,
                                file: File) {

            var outputStream: OutputStream? = null
            try {
                file.parentFile.mkdirs()
                outputStream = FileOutputStream(file)
                bitmap.compress(format, quality, outputStream)
                outputStream.flush()
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

        }
    }
}