package zw.com.mydemo.bitmapCache

import android.graphics.Bitmap
import android.util.LruCache

/**
 * Created by ZouWei on 2018/9/13.
 */
class MemoryCacheUtils {
    private lateinit var mBitmapCache: LruCache<String, Bitmap>

    init {
        val maxMemory: Long = Runtime.getRuntime().maxMemory() / 8
        mBitmapCache = object : LruCache<String, Bitmap>(maxMemory.toInt()) {
            override fun sizeOf(key: String?, value: Bitmap?): Int {
                if (value == null) return 0
                return value.byteCount
            }

            override fun entryRemoved(evicted: Boolean, key: String?, oldValue: Bitmap?, newValue: Bitmap?) {
                super.entryRemoved(evicted, key, oldValue, newValue)
            }
        }
    }

    fun getBitmapFromMemory(url: String): Bitmap? {
        return mBitmapCache.get(url)
    }


    fun setBitmapFromMemory(key: String, value: Bitmap) {
        mBitmapCache.put(key, value)
    }

}