package com.harsom.delemu.utils


import android.text.TextUtils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 *
 * 时间相关常用工具类
 * Created by Yuri on 2016/4/20.
 */
object TimeUtil {

    /**
     * 获取当前时间，并格式化
     *
     * @return 当前时间格式化后的字符
     */
    val time: String
        get() {
            val format = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.CHINA)
            val date = Date(System.currentTimeMillis())
            return format.format(date)
        }

    /**
     * 获取当前时间，并格式化
     *
     * @return 当前时间格式化后的字符
     */
    val date: String
        get() {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            val date = Date(System.currentTimeMillis())
            return format.format(date)
        }

    /**
     * 获取当前时间，并格式化
     *
     * @return 当前时间格式化后的字符
     */
    fun getDate(time: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val date = Date(time)
        return format.format(date)
    }

    /**
     * 按指定格式返回时间
     *
     * @return 当前时间格式化后的字符
     */
    fun getDate(time: Long, formats: String): String {
        val format = SimpleDateFormat(formats, Locale.CHINA)
        val date = Date(time)
        return format.format(date)
    }

    fun getTime(birthday: String): Long {
        if (TextUtils.isEmpty(birthday)) {
            return 0L
        }

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        var time = 0L
        try {
            val date = format.parse(birthday)
            time = date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return time
    }

    fun getBabyAge(birthday: String): String? {
        return getBabyAge(getTime(birthday))
    }

    /**
     * 获取宝宝的岁数 格式：2岁8个月
     * @param birthdayTime 宝宝的出生日期
     */
    fun getBabyAge(birthdayTime: Long): String? {
        val birthday = Calendar.getInstance()
        birthday.timeInMillis = birthdayTime

        val now = Calendar.getInstance()

        var day = now.get(Calendar.DAY_OF_MONTH) - birthday.get(Calendar.DAY_OF_MONTH)
        var month = now.get(Calendar.MONTH) - birthday.get(Calendar.MONTH)
        var year = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR)

        //按照减法原理，先day相减，不够向month借；然后month相减，不够向year借；最后year相减。
        if (day < 0) {
            month -= 1
            now.add(Calendar.MONTH, -1)//得到上一个月，用来得到上个月的天数。
            day += now.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        if (month < 0) {
            month = (month + 12) % 12
            year--
        }

        val result: String
        if (year < 0) {
            return null
        }

        if (year == 0 && month == 0 && day == 0) {
            return "0天"
        }

        val monthStr = if (month > 0) month.toString() + "个月" else ""
        val dayStr = if (day > 0) day.toString() + "天" else ""

        if (year > 0) {
            result = year.toString() + "岁" + month + "个月"
        } else {
            result = monthStr + dayStr
        }

        return result
    }

    /**
     * 判断两个时间是否为同一天
     * @param timeA time in mills
     * @param timeB time in mills
     * @return true 两个时间为同一天；false：非同一天
     */
    fun isSameDay(timeA: Long, timeB: Long): Boolean {
        val calDateA = Calendar.getInstance()
        calDateA.timeInMillis = timeA
        val calDateB = Calendar.getInstance()
        calDateB.timeInMillis = timeB
        return (calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB
                .get(Calendar.DAY_OF_MONTH))
    }

    fun isSameYear(timeA: Long, timeB: Long): Boolean {
        val calDateA = Calendar.getInstance()
        calDateA.timeInMillis = timeA
        val calDateB = Calendar.getInstance()
        calDateB.timeInMillis = timeB
        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
    }

    /**
     * 判断指定时间是否就是今天的日期
     * @param time time in mills
     * @return true toady，else not
     */
    fun isToady(time: Long): Boolean {
        return time > 0 && isSameDay(time, System.currentTimeMillis())
    }

    /**
     * 返回指定时间的月份
     */
    fun getMonth(time: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        return calendar.get(Calendar.MONTH) + 1
    }
}
