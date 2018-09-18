package zw.com.mydemo.animation

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.harsom.delemu.utils.Log
import zw.com.mydemo.R
import zw.com.mydemo.animation.evaluator.ObjectEvaluator
import kotlin.math.log

/**
 * Created by ZouWei on 2018/9/12.
 */
class ProgressView : View {

    private val mBgPaint = Paint()
    private val mProgressPaint = Paint()

    private var mCurrentPro: ArcProgress? = null

    constructor(context: Context) : super(context) {
        iniPaint()
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        iniPaint()
    }

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr) {
        iniPaint()
    }

    private fun iniPaint() {
        mBgPaint.color = ContextCompat.getColor(context, R.color.colorAccent)
        mBgPaint.isAntiAlias = true
        mBgPaint.strokeWidth = 20f
        mBgPaint.style = Paint.Style.STROKE

        mProgressPaint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        mProgressPaint.isAntiAlias = true
        mProgressPaint.strokeWidth = 20f
        mProgressPaint.style = Paint.Style.STROKE


    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        canvas.drawCircle(500f, 500f, 50f, mBgPaint)
        if (mCurrentPro == null) {
            val startPro = ArcProgress()
            val endPro = ArcProgress(260f)
            val animator = ValueAnimator.ofObject(ObjectEvaluator(), startPro, endPro)
            animator.addUpdateListener { animation ->
                mCurrentPro = animation.animatedValue as ArcProgress
                Log.d("currentPro = ${mCurrentPro!!.angle}")
                invalidate()
            }
            animator.duration = 5000
            animator.start()
        } else {
            canvas.drawArc(450f, 450f, 550f, 550f, 0f, mCurrentPro!!.angle, false, mProgressPaint)
        }

    }

}