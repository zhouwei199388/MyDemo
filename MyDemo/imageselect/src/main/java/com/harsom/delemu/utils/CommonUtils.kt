package com.harsom.delemu.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Build
import android.support.v4.content.ContextCompat

import java.util.UUID
import java.util.regex.Pattern

/**
 * 基础工具类
 */
object CommonUtils {

    /**
     * 获取设备型号
     *
     * @return 设备型号字符串
     */
    val phoneModel: String
        get() = Build.MODEL


    /**
     * 验证码是否合法的校验
     *
     * @param number
     * @return true 合法； false 不合法
     */
    private val CODE_REG = "^\\d{4}$"

    /**
     * 验证手机号码是否合法的正则表达式
     */
    private val PHONE_REG = "^1[3|4578][0-9]\\d{8}$"

    /**
     * 正则匹配用户密码6-24位数字和字母组合
     */
    private val PASSWORD_REG = "^[0-9a-zA-Z_]{6,24}$"

    /**
     * 正则匹配用户名只有汉字或字母
     */
    private val USERNAME_REG = "^([A-Za-z]|[\\u4E00-\\u9FA5])+$"

    private val NUMBER_REG = "^\\d\\d*$"
    /**
     * 获取应用版本号
     *
     * @param context
     * @return 当前版本号
     * @throws NameNotFoundException
     */
    fun getAppVersion(context: Context): String {
        var version = ""
        try {
            val packageManager = context.packageManager
            val packInfo = packageManager.getPackageInfo(
                    context.packageName, 0)
            version = packInfo.versionName
        } catch (e: Exception) {
            Log.e("get app version error...")
        }

        return version
    }

    /**
     * 生成一个32位的UUID
     */
    fun generateUUID(): String {
        val s = UUID.randomUUID().toString()
        //去掉“-”符号
        return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24)
    }

    /**
     * 获取Manifest中配置的键值对
     *
     * @param context
     * @param key     键
     * @return 键对应的值
     */
    fun getMetaData(context: Context, key: String): String {
        try {
            val ai = context.packageManager
                    .getApplicationInfo(context.packageName,
                            PackageManager.GET_META_DATA)
            val value = ai.metaData.get(key)
            if (value != null) {
                return value.toString()
            }
        } catch (e: Exception) {
            return ""
        }

        return ""
    }

    /**
     * 获取当前版本渠道ID
     *
     * @param context
     * @return 当前版本渠道ID
     */
    fun getCurrentChannel(context: Context): String {
        return getMetaData(context, "CHANAL")
    }

    fun isValideCode(number: String): Boolean {
        val p = Pattern.compile(CODE_REG)
        val m = p.matcher(number)
        return m.find()
    }

    /**
     * 验证手机号是否合法
     *
     * @param number 需要验证的手机号码
     * @return true 手机号码合法；false手机号码不合法
     */
    fun isValidPhoneNumber(number: CharSequence): Boolean {
        val p = Pattern.compile(PHONE_REG)
        val m = p.matcher(number)
        return m.find()
    }

    fun isValidPassword(password: String): Boolean {
        val p = Pattern.compile(PASSWORD_REG)
        val m = p.matcher(password)
        return m.find()
    }

    fun isValidUserName(userName: String): Boolean {
        val p = Pattern.compile(USERNAME_REG)
        val m = p.matcher(userName)
        return m.find()
    }

    /**
     * 正则匹配是否是纯数字
     */
    fun isNumber(text: String): Boolean {
        val p = Pattern.compile(NUMBER_REG)
        val m = p.matcher(text)
        return m.find()
    }

    /**
     * 隐藏手机号码中间四位
     */
    fun getHidePhoneNumber(phone: String): String {
        return phone.replace("(\\d{3})\\d{4}(\\d{4})".toRegex(), "$1****$2")
    }

    /**
     * 确保作为方法参数的对象引用非空
     *
     * @param reference an object reference
     * @return the non-null reference that was validated
     * @throws NullPointerException if `reference` is null
     */
    fun <T> checkNotNull(reference: T?): T {
        if (reference == null) {
            throw NullPointerException()
        }
        return reference
    }


    /**
     * 确保作为方法参数的对象引用非空
     * @param reference an object reference
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     * string using [String.valueOf]
     * @return the non-null reference that was validated
     * @throws NullPointerException if `reference` is null
     */
    fun <T> checkNotNull(reference: T?, errorMessage: Any?): T {
        if (reference == null) {
            throw NullPointerException(errorMessage.toString())
        }
        return reference
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param expression a boolean expression
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     * string using [String.valueOf]
     * @throws IllegalArgumentException if `expression` is false
     */
    fun checkArgument(expression: Boolean, errorMessage: Any?) {
        if (!expression) {
            throw IllegalArgumentException(errorMessage.toString())
        }
    }


    /**
     * 修改状态栏颜色
     * @param activity activity
     * @param colorResId 颜色资源ID
     */
    fun setStatusBarColor(activity: Activity?, colorResId: Int) {
        if (activity == null) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.statusBarColor = ContextCompat.getColor(activity, colorResId)
        }
    }

}
