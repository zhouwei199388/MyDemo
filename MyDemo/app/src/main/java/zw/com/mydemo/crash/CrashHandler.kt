package zw.com.mydemo.crash

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.os.Process
import android.text.TextUtils
import com.harsom.delemu.utils.ToastUtil
import java.io.*
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ZouWei on 2018/9/10.
 *
 * 全局异常捕获
 */
class CrashHandler : Thread.UncaughtExceptionHandler {
    private lateinit var mDefaultHanlder: Thread.UncaughtExceptionHandler
    private lateinit var mContext: Context
    private var mMessages = HashMap<String, String>()

    companion object {
        fun getInstance() = Handler.INSTANCE
    }

    private object Handler {
        val INSTANCE = CrashHandler()
    }

    fun init(context: Context) {
        mContext = context

        mDefaultHanlder = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread?, e: Throwable?) {

        if (!handleException(e)) {
            mDefaultHanlder.uncaughtException(t, e)
        } else {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            Process.killProcess(Process.myPid())
            System.exit(1)
        }
    }

    /**
     * 是否认为捕获异常
     */
    private fun handleException(e: Throwable?): Boolean {
        if (e == null) {
            return false
        }
        object : Thread() {
            override fun run() {
                Looper.prepare()
                ToastUtil.showToast(mContext, "捕获到异常")
                Looper.loop()
            }
        }.start()
        collectErrorMessages()
        saveErrorMessages(e)
        return true
    }

    /**
     * 收集错误信息
     */
    private fun collectErrorMessages() {
        val pm: PackageManager = mContext.packageManager
        try {
            val pi: PackageInfo = pm.getPackageInfo(mContext.packageName, PackageManager.GET_ACTIVITIES)
            val versionName: String = if (TextUtils.isEmpty(pi.versionName)) "" else pi.versionName
            val versionCode = pi.versionCode
            mMessages["versionName"] = versionName
            mMessages["versionCode"] = versionCode.toString()
            val fields = Build::class.java.fields
            if (fields != null && fields.isNotEmpty()) {
                for (field: Field in fields) {
                    field.isAccessible = true
                    try {
                        mMessages[field.name] = field.get(null).toString()
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    }
                }
            }

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    /**
     * 保存错误信息
     */
    private fun saveErrorMessages(e: Throwable) {
        val sb = StringBuilder()
        for (entry: Map.Entry<String, String> in mMessages.entries) {
            val key = entry.key
            val value = entry.value
            sb.append(key).append("=").append(value).append("\n")
        }
        val writer = StringWriter()
        val pw = PrintWriter(writer)

        e.printStackTrace(pw)
        var cause = e.cause
        while (cause != null) {
            cause.printStackTrace()
            cause = e.cause
        }
        pw.close()
        val result = writer.toString()
        sb.append(result)
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA).format(Date())
        val fileName = "crash-$time-${System.currentTimeMillis()}.log"

        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val path = "${Environment.getExternalStorageDirectory().path}/crash/"
            val dir = File(path)
            if (!dir.exists())
                dir.mkdirs()
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream("$path$fileName")
                fos.write(sb.toString().toByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (fos != null) {
                    try {
                        fos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }


}