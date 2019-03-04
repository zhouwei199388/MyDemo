package zw.com.mydemo.animation.evaluator

import android.animation.TypeEvaluator
import com.harsom.delemu.utils.Log
import zw.com.mydemo.animation.ArcProgress

/**
 * Created by ZouWei on 2018/9/12.
 *
 * 估值器
 */
class ObjectEvaluator : TypeEvaluator<Any> {
    override fun evaluate(fraction: Float, startValue: Any?, endValue: Any?): Any? {
        val startPro = startValue as ArcProgress
        val endPro = endValue as ArcProgress
        Log.d("fraction=$fraction  startValue=${startPro.angle}  endValue=${endPro.angle}")
        val pro: Int = (startPro.angle + fraction * endPro.angle).toInt()
        return ArcProgress(pro.toFloat())
    }
}