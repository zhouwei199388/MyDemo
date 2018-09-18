package zw.com.mydemo.bitmapCache

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import com.harsom.delemu.utils.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by ZouWei on 2018/9/14.
 */
class NetCacheUtiles(localCacheUtils: LocalCacheUtils, memoryCacheUtils: MemoryCacheUtils) {
    private var mLocalCacheUtils: LocalCacheUtils = localCacheUtils
    private var mMemoryCacheUtils: MemoryCacheUtils = memoryCacheUtils

    fun getBitmapFromNet(ivPic: ImageView, url: String) {
        BitmapTask().execute(ivPic, url)
    }


    fun downLoadBitmap(url: String): Bitmap? {
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            conn.requestMethod = "GET"
            val responseCode = conn.responseCode

            if (responseCode == 200) {
                val option = BitmapFactory.Options()
                option.inSampleSize = 2
                option.inPreferredConfig = Bitmap.Config.ARGB_4444
                return BitmapFactory.decodeStream(conn.inputStream)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            conn.disconnect()
        }
        return null
    }

    inner class BitmapTask : AsyncTask<Any, Void, Bitmap>() {

        private lateinit var ivPic: ImageView
        private lateinit var url: String


        override fun doInBackground(vararg params: Any?): Bitmap? {

            ivPic = params[0] as ImageView
            url = params[1] as String
            return downLoadBitmap(url)
        }


        override fun onPostExecute(result: Bitmap?) {
            if (result != null) {
                ivPic.setImageBitmap(result)
                mLocalCacheUtils.setBitmapToLocal(url, result)
                mMemoryCacheUtils.setBitmapFromMemory(url, result)
            } else {
                Log.d("返回图片为空！")
            }
        }

    }


}