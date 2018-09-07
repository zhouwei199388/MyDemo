package com.harsom.delemu.crop

import android.graphics.Matrix
import android.graphics.Rect
import android.view.MotionEvent

/**
 * Created by ZouWei on 2018/8/13.
 */
class TouchManager {
    private var maxNumberOfTouchPoints: Int = 0
    private var mCropConfig: CropConfig

    private var points: Array<TouchPoint?>
    private var previousPoints: Array<TouchPoint?>

    private var minimumScale: Float = 0.toFloat()
    private var maximumScale: Float = 0f
    private var imageBounds: Rect? = null
    var aspectRatio: Float = 0.toFloat()
    private var viewportWidth: Int = 0
    private var viewportHeight: Int = 0
    private var bitmapWidth: Int = 0
    private var bitmapHeight: Int = 0

    private var verticalLimit: Int = 0
    private var horizontalLimit: Int = 0

    private var scale = -1.0f
    private val position = TouchPoint()


    constructor(maxNumberOfTouchPoints: Int, cropConfig: CropConfig) {
        this.maxNumberOfTouchPoints = maxNumberOfTouchPoints
        this.mCropConfig = cropConfig

        points = Array(maxNumberOfTouchPoints, { null })
        previousPoints = Array(maxNumberOfTouchPoints, { null })
        minimumScale = cropConfig.minScale
        maximumScale = cropConfig.maxScale
    }

    fun onEvent(event: MotionEvent) {
        val index = event.actionIndex
        if (index >= maxNumberOfTouchPoints) {
            return  // We don't care about this pointer, ignore it.
        }
        if (isUpAction(event.actionMasked)) {
            previousPoints[index] = null
            points[index] = null
        } else {
            updateCurrentAndPreviousPoints(event)
        }

        handleDragGesture()
        handlePinchGesture()
        if (isUpAction(event.actionMasked)) {
            ensureInsideViewport()
        }
    }


    fun getViewportWidth(): Int {
        return viewportWidth
    }

    fun getViewportHeight(): Int {
        return viewportHeight
    }

    fun applyPositioningAndScale(matrix: Matrix) {
        matrix.postTranslate(-bitmapWidth / 2.0f, -bitmapHeight / 2.0f)
        matrix.postScale(scale, scale)
        matrix.postTranslate(position.getX(), position.getY())
    }

    fun resetFor(bitmapWidth: Int, bitmapHeight: Int, availableWidth: Int, availableHeight: Int) {
        aspectRatio = mCropConfig.viewportRatio
        imageBounds = Rect(0, 0, availableWidth / 2, availableHeight / 2)
        setViewport(bitmapWidth, bitmapHeight, availableWidth, availableHeight)

        this.bitmapWidth = bitmapWidth
        this.bitmapHeight = bitmapHeight
        if (bitmapWidth > 0 && bitmapHeight > 0) {
            setMinimumScale()
            setLimits()
            ensureInsideViewport()
        }
    }

    private fun handlePinchGesture() {
        if (getDownCount() != 2) {
            return
        }
        updateScale()
        setLimits()
    }

    private fun handleDragGesture() {
        if (getDownCount() != 1) {
            return
        }
        position.add(moveDelta(0))
    }


    private fun setViewport(bitmapWidth: Int, bitmapHeight: Int, availableWidth: Int, availableHeight: Int) {
        val imageAspect = bitmapWidth.toFloat() / bitmapHeight
        val viewAspect = availableWidth.toFloat() / availableHeight

        var ratio = mCropConfig.viewportRatio
        if (java.lang.Float.compare(0f, ratio) == 0) {
            // viewport ratio of 0 means match native ratio of bitmap
            ratio = imageAspect
        }

        if (ratio > viewAspect) {
            // viewport is wider than view
            viewportWidth = availableWidth - mCropConfig.viewportOverlayPadding * 2
            viewportHeight = (viewportWidth * (1 / ratio)).toInt()
        } else {
            // viewport is taller than view
            viewportHeight = availableHeight - mCropConfig.viewportOverlayPadding * 2
            viewportWidth = (viewportHeight * ratio).toInt()
        }
        viewportHeight = if (viewportHeight > 512) 512 else viewportHeight
        viewportWidth = if (viewportWidth > 512) 512 else viewportWidth
    }

    private fun setLimits() {
        horizontalLimit = computeLimit((bitmapWidth * scale).toInt(), viewportWidth)
        verticalLimit = computeLimit((bitmapHeight * scale).toInt(), viewportHeight)
    }

    private fun ensureInsideViewport() {
        if (imageBounds == null) {
            return
        }

        var newY = position.getY()
        val bottom = imageBounds!!.bottom


        if (bottom - newY >= verticalLimit) {
            newY = (bottom - verticalLimit).toFloat()
        } else if (newY - bottom >= verticalLimit) {
            newY = (bottom + verticalLimit).toFloat()
        }

        var newX = position.getX()
        val right = imageBounds!!.right
        if (newX <= right - horizontalLimit) {
            newX = (right - horizontalLimit).toFloat()
        } else if (newX > right + horizontalLimit) {
            newX = (right + horizontalLimit).toFloat()
        }

        position[newX] = newY
    }

    private fun setMinimumScale() {
        val fw = viewportWidth.toFloat() / bitmapWidth
        val fh = viewportHeight.toFloat() / bitmapHeight
        minimumScale = Math.max(fw, fh)
        scale = Math.max(scale, minimumScale)
    }


    private fun getDownCount(): Int {
        var count = 0
        for (point in points) {
            if (point != null) {
                count++
            }
        }
        return count
    }


    private fun previousVector(indexA: Int, indexB: Int): TouchPoint {
        return if (previousPoints[indexA] == null || previousPoints[indexB] == null)
            vector(points[indexA]!!, points[indexB]!!)
        else
            vector(previousPoints[indexA]!!, previousPoints[indexB]!!)
    }

    private fun updateScale() {
        val current = vector(points[0]!!, points[1]!!)
        val previous = previousVector(0, 1)
        val currentDistance = current.getLength()
        val previousDistance = previous.getLength()

        var newScale = scale
        if (previousDistance != 0f) {
            newScale *= currentDistance / previousDistance
        }
        newScale = if (newScale < minimumScale) minimumScale else newScale
        newScale = if (newScale > maximumScale) maximumScale else newScale

        scale = newScale
    }

    private fun moveDelta(index: Int): TouchPoint {
        return if (isPressed(index)) {
            val previous = if (previousPoints[index] != null) previousPoints[index] else points[index]
            subtract(points[index]!!, previous!!)
        } else {
            TouchPoint()
        }
    }

    private fun isPressed(index: Int): Boolean {
        return points[index] != null
    }

    private fun updateCurrentAndPreviousPoints(event: MotionEvent) {
        for (i in 0 until maxNumberOfTouchPoints) {
            if (i < event.pointerCount) {
                val eventX = event.getX(i)
                val eventY = event.getY(i)

                if (points[i] == null) {
                    points[i] = TouchPoint(eventX, eventY)
                    previousPoints[i] = null
                } else {
                    if (previousPoints[i] == null) {
                        previousPoints[i] = TouchPoint()
                    }
                    previousPoints[i]!!.copy(points[i]!!)
                    points[i]!![eventX] = eventY
                }
            } else {
                previousPoints[i] = null
                points[i] = null
            }
        }
    }


    class TouchPoint {
        private var x: Float = 0.toFloat()
        private var y: Float = 0.toFloat()

        constructor() {}
        constructor(x: Float, y: Float) {
            this.x = x
            this.y = y
        }

        fun getX(): Float {
            return x
        }

        fun getY(): Float {
            return y
        }

        fun getLength(): Float {
            return Math.sqrt((x * x + y * y).toDouble()).toFloat()
        }

        fun copy(other: TouchPoint): TouchPoint {
            x = other.getX()
            y = other.getY()
            return this
        }

        operator fun set(x: Float, y: Float): TouchPoint {
            this.x = x
            this.y = y
            return this
        }

        fun add(value: TouchPoint): TouchPoint {
            this.x += value.getX()
            this.y += value.getY()
            return this
        }


        override fun toString(): String {
            return String.format("(%.4f, %.4f)", x, y)
        }
    }

    companion object {
        fun subtract(lhs: TouchPoint, rhs: TouchPoint): TouchPoint {
            return TouchPoint(lhs.getX() - rhs.getX(), lhs.getY() - rhs.getY())
        }

        fun isUpAction(actionMasked: Int): Boolean {
            return actionMasked == MotionEvent.ACTION_POINTER_UP || actionMasked == MotionEvent.ACTION_UP
        }

        fun vector(a: TouchPoint, b: TouchPoint): TouchPoint {
            return subtract(b, a)
        }

        fun computeLimit(bitmapSize: Int, viewportSize: Int): Int {
            return (bitmapSize - viewportSize) / 2
        }
    }
}