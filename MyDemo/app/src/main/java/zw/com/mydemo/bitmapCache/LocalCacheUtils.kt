package zw.com.mydemo.bitmapCache

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by ZouWei on 2018/9/14.
 */
class LocalCacheUtils {

    private val CACHE_PATH = "${Environment.getExternalStorageDirectory().absolutePath}/BitmapCache"

    fun getBitmapFromLocal(url: String): Bitmap? {
        val file = File(CACHE_PATH, url)
        return if (file.exists()) {
            BitmapFactory.decodeFile(file.path)
        } else {
            null
        }
    }


    fun setBitmapToLocal(url: String, bitmap: Bitmap) {
        try {
            val file = File(CACHE_PATH, url)
            if (!file.exists()) {
                file.mkdirs()
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}