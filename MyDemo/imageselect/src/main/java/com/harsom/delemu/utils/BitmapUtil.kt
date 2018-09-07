package com.harsom.delemu.utils

import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.media.ThumbnailUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object BitmapUtil {

    private const val LONG_IMAGE_RATIO = 3


    private fun getWidthHeight(imagePath: String): IntArray {
        if (imagePath.isEmpty()) {
            return intArrayOf(0, 0)
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        try {
            BitmapFactory.decodeFile(imagePath, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 使用第一种方式获取原始图片的宽高
        var srcWidth = options.outWidth
        var srcHeight = options.outHeight

        // 使用第二种方式获取原始图片的宽高
        if (srcHeight == -1 || srcWidth == -1) {
            try {
                val exifInterface = ExifInterface(imagePath)
                srcHeight = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH,
                        ExifInterface.ORIENTATION_NORMAL)
                srcWidth = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH,
                        ExifInterface.ORIENTATION_NORMAL)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        // 使用第三种方式获取原始图片的宽高
        if (srcWidth <= 0 || srcHeight <= 0) {
            val bitmap2 = BitmapFactory.decodeFile(imagePath)
            if (bitmap2 != null) {
                srcWidth = bitmap2.width
                srcHeight = bitmap2.height
                try {
                    if (!bitmap2.isRecycled) {
                        bitmap2.recycle()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        return intArrayOf(srcWidth, srcHeight)
    }

    fun isLongImage(imagePath: String): Boolean {
        val wh = getWidthHeight(imagePath)
        return (wh[0] > 0
                && wh[1] > 0
                && Math.max(wh[0], wh[1]) / Math.min(wh[0], wh[1]) >= LONG_IMAGE_RATIO)
    }

    /**
     * Drawable convert to bytes[]
     */
    @Synchronized
    fun drawableToByte(drawable: Drawable): ByteArray? {
        return drawableToByte(drawable, Bitmap.CompressFormat.PNG)
    }

    /**
     * Drawable convert to bytes[]
     *
     * @param drawable drawable
     * @param format   bitmap format,like png,jpeg and so son
     * you can check through Bitmap.CompressFormat class
     * @return drawable bytes[]
     */
    fun drawableToByte(drawable: Drawable?, format: Bitmap.CompressFormat): ByteArray? {
        if (drawable != null) {
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight,
                    if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            drawable.draw(canvas)
            val size = bitmap.width * bitmap.height * 4
            // 创建一个字节数组输出流,流的大小为size
            val baos = ByteArrayOutputStream(size)
            // 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            // 将字节数组输出流转化为字节数组byte[]
            return baos.toByteArray()
        }
        return null
    }

    /**
     * bitmap转byte[]
     *
     * @param bitmap  图片
     * @param format  图片格式
     * @param quality 压缩质量
     * @return
     */
    fun bitmapToByteArray(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int): ByteArray {
        val out = ByteArrayOutputStream()
        bitmap.compress(format, quality, out)
        return out.toByteArray()
    }

    @JvmOverloads
    fun bitmapToByteArray(bitmap: Bitmap,
                          format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): ByteArray {
        val out = ByteArrayOutputStream()
        bitmap.compress(format, 100, out)
        return out.toByteArray()
    }

    /**
     * 将图片字节流转换成bitmap
     *
     * @param data bytes
     * @return bitmap
     */
    fun byteArrayToBitmap(data: ByteArray): Bitmap? {
        return if (data.size == 0) {
            null
        } else BitmapFactory.decodeByteArray(data, 0, data.size)
    }


    /**
     * a drawable convert to bitmap
     *
     * @param drawable drawable
     * @return bitmap
     */
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap
                .createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight,
                        if (drawable.opacity != PixelFormat.OPAQUE)
                            Bitmap.Config.ARGB_8888
                        else
                            Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth,
                drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * 圆角图片
     *
     * @param bitmap bitmap
     * @return round bitmap
     */
    fun getRoundedCornerBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        val roundPx = 12f

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return round bitmap
     */
    fun toRoundBitmap(bitmap: Bitmap): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        val roundPx: Float
        val left: Float
        val top: Float
        val right: Float
        val bottom: Float
        val dst_left: Float
        val dst_top: Float
        val dst_right: Float
        val dst_bottom: Float
        if (width <= height) {
            roundPx = (width / 2).toFloat()

            left = 0f
            top = 0f
            right = width.toFloat()
            bottom = width.toFloat()

            height = width

            dst_left = 0f
            dst_top = 0f
            dst_right = width.toFloat()
            dst_bottom = width.toFloat()
        } else {
            roundPx = (height / 2).toFloat()

            val clip = ((width - height) / 2).toFloat()

            left = clip
            right = width - clip
            top = 0f
            bottom = height.toFloat()
            width = height

            dst_left = 0f
            dst_top = 0f
            dst_right = height.toFloat()
            dst_bottom = height.toFloat()
        }

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        val src = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        val dst = Rect(dst_left.toInt(), dst_top.toInt(), dst_right.toInt(), dst_bottom.toInt())
        val rectF = RectF(dst)

        paint.isAntiAlias = true// 设置画笔无锯齿

        canvas.drawARGB(0, 0, 0, 0) // 填充整个Canvas

        // 以下有两种方法画圆,drawRounRect和drawCircle
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        // canvas.drawCircle(roundPx, roundPx, roundPx, paint);

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint) // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
        return output
    }

    /**
     * 计算图片的缩放比例
     *
     * @param options   参数
     * @param reqWidth  目标缩放宽度
     * @param reqHeight 目标缩放高度
     * @return 缩放比例 inSampleSize
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        //得到原图的宽搞
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1//初始为1，不缩放
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            //取缩放比例较小的那个，保证不失真的严重
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

    /**
     * 计算按指定宽高度缩放后的bitmap的宽高度
     *
     * @param options   参数
     * @param reqWidth  目标缩放宽度
     * @param reqHeight 目标缩放高度
     * @return [“宽度”，“高度”]
     */
    private fun calculateSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): IntArray {
        //得到原图的宽高
        var height = options.outHeight
        var width = options.outWidth
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = height.toFloat() / reqHeight.toFloat()
            val widthRatio = width.toFloat() / reqWidth.toFloat()
            if (heightRatio > widthRatio) {
                width = reqWidth
                height = (height / widthRatio).toInt()
            } else {
                width = (width / heightRatio).toInt()
                height = reqHeight
            }
        }
        return intArrayOf(width, height)
    }

    /**
     * 获取按比例压缩的缩略图
     *
     * @param filePath 原图地址
     * @return 缩略图bitmap
     */
    fun getThumbnailBitmap(filePath: String): Bitmap? {
        //第一步大小压缩
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        //给定一个指定的分辨率，计算压缩比率
        options.inSampleSize = calculateInSampleSize(options, 320, 320)
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeFile(filePath, options)
        //第二步 质量压缩
        return getCompressBitmap(bitmap)
    }

    /**
     * 获取按比例压缩的预览图
     *
     * @param filePath 原图地址
     * @return 预览图bitmap
     */
    fun getPreviewBitmap(filePath: String): Bitmap? {
        //第一步大小压缩
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        //给定一个指定的分辨率，计算压缩比率
        options.inSampleSize = calculateInSampleSize(options, 720, 1280)
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeFile(filePath, options)
        //第二步 质量压缩
        return getCompressBitmap(bitmap)
    }

    /**
     * 获取按指定宽高压缩的缩略图
     *
     * @param filePath 原图地址
     * @return 缩略图bitmap
     */
    fun getThumbnailBitmap2(filePath: String): Bitmap? {
        //第一步大小压缩
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        //按512 * 512的来
        //开始计算压缩后的宽高度
        val result = calculateSize(options, 512, 512)
        options.inJustDecodeBounds = false
        var bitmap = BitmapFactory.decodeFile(filePath, options)
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, result[0], result[1])
        //第二步 质量压缩
        return getCompressBitmap(bitmap)
    }

    /**
     * 获取按指定宽高压缩的预览图
     *
     * @param filePath 原图地址
     * @return 预览图bitmap
     */
    fun getPreviewBitmap2(filePath: String): Bitmap? {
        //第一步大小压缩
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        //按720 * 1280的来
        //开始计算压缩后的宽高度
        val result = calculateSize(options, 720, 1280)
        //给定一个指定的分辨率，计算压缩比率
        options.inJustDecodeBounds = false
        var bitmap = BitmapFactory.decodeFile(filePath, options)
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, result[0], result[1])
        //第二步 质量压缩
        return getCompressBitmap(bitmap)
    }

    /**
     * 获取按指定宽高压缩的预览图
     *
     * @param angle    旋转角度
     * @param filePath 原图地址
     * @return 预览图byte[]
     */
    fun getCompressPhoto(angle: Int, filePath: String): ByteArray? {
        //第一步大小压缩
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        //按720 * 1280的来
        //开始计算压缩后的宽高度
        val result = calculateSize(options, 720, 1280)
        //给定一个指定的分辨率，计算压缩比率
        options.inJustDecodeBounds = false
        var bitmap: Bitmap? = BitmapFactory.decodeFile(filePath, options)
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, result[0], result[1])
        if (bitmap == null) {
            return null
        }

        if (angle == 0) {
            //第二步 质量压缩
            val bytes = getCompressBytes(bitmap)
            bitmap.recycle()
            return bytes
        }

        Log.d("angle:$angle")

        //旋转图片 动作
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        // 创建新的图片
        val resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.width, bitmap.height, matrix, true)
        //第二步 质量压缩
        val bytes = getCompressBytes(resizedBitmap)
        bitmap.recycle()
        resizedBitmap.recycle()

        return bytes
    }

    /**
     * 质量压缩
     *
     * @param bitmap 需要压缩的bitmap
     * @return 压缩后的字节流
     */
    fun getCompressBytes(bitmap: Bitmap): ByteArray {
        val bos = ByteArrayOutputStream()
        //设置压缩质量，1-100，100表示不压缩
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos)
        return bos.toByteArray()
    }

    /**
     * 质量压缩
     *
     * @param bitmap 需要压缩的bitmap
     * @return 压缩后的字节流
     */
    fun getCompressBitmap(bitmap: Bitmap): Bitmap? {
        val bos = ByteArrayOutputStream()
        //设置压缩质量，1-100，100表示不压缩
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos)
        return byteArrayToBitmap(bos.toByteArray())
    }

    /**
     * 保存bitmap 字节流到sdcard
     *
     * @param desPath 目标图片文件地址
     * @param bytes   bitmap字节流
     * @return false，保存失败
     */
    fun saveBitmap(desPath: String, bytes: ByteArray): Boolean {
        val bitmap = byteArrayToBitmap(bytes)
        return saveBitmap(desPath, bitmap)
    }

    /**
     * 保存bitmap到sdcard
     *
     * @param desPath 目标图片文件地址
     * @param bitmap  bitmap
     * @return false，保存失败
     */
    fun saveBitmap(desPath: String, bitmap: Bitmap?): Boolean {
        val file = File(desPath)
        try {
            val ret = file.createNewFile()
            if (!ret) {
                return false
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

        val fos: FileOutputStream
        try {
            fos = FileOutputStream(file)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

        return true
    }

    /**
     * 缩放Bitmap
     */
    fun scaleBitmap(bitmap: Bitmap?, scale: Float): Bitmap? {
        var resizeBitmap: Bitmap? = null
        val scaleMatrix = Matrix()
        if (bitmap != null && !bitmap.isRecycled) {
            scaleMatrix.postScale(scale, scale)
            try {
                resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, scaleMatrix, false)
            } catch (e: Exception) {
                Log.e(e.toString())
                return bitmap
            }

        }
        return resizeBitmap
    }


}
