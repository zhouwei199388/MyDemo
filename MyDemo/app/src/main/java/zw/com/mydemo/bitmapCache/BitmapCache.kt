package zw.com.mydemo.bitmapCache

import android.graphics.Bitmap
import android.widget.ImageView
import zw.com.mydemo.R

/**
 * Created by ZouWei on 2018/9/14.
 */
class BitmapCache {

    private val mLocalCacheUtils: LocalCacheUtils = LocalCacheUtils()
    private val mMemoryCacheUtils: MemoryCacheUtils = MemoryCacheUtils()
    private val mNetCacheUtiles: NetCacheUtiles = NetCacheUtiles(mLocalCacheUtils, mMemoryCacheUtils)


    fun disPlay(imageView: ImageView, url: String) {
        imageView.setImageResource(R.mipmap.ic_launcher)
        var bitmap: Bitmap? = mMemoryCacheUtils.getBitmapFromMemory(url)

        if (bitmap !== null) {
            imageView.setImageBitmap(bitmap)
            return
        }

        bitmap = mLocalCacheUtils.getBitmapFromLocal(url)
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
            mMemoryCacheUtils.setBitmapFromMemory(url, bitmap)
            return
        }
        mNetCacheUtiles.getBitmapFromNet(imageView, url)
    }

}