package zw.com.mydemo

import android.app.Application
import zw.com.mydemo.crash.CrashHandler

/**
 * Created by ZouWei on 2018/9/10.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
//        CrashHandler.getInstance().init(this)
    }
}