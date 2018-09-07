package com.harsom.delemu.base

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

import com.harsom.delemu.R


class GridDividerDecorator @JvmOverloads constructor(context: Context, private val mDividerSize: Int = 0) : RecyclerView.ItemDecoration() {
    private val mDivider: Drawable = ContextCompat.getDrawable(context, R.drawable.video_bg)!!

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        drawRightDivider(c, parent)
        drawBottomDivider(c, parent)
    }

    private fun drawRightDivider(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {

            val child = parent.getChildAt(i)
            val params = child
                    .layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            val right = left + mDividerSize
            val top = child.top - params.topMargin
            val bottom = child.bottom + params.bottomMargin
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    private fun drawBottomDivider(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {

            val child = parent.getChildAt(i)
            val params = child
                    .layoutParams as RecyclerView.LayoutParams
            val left = child.left - params.leftMargin
            val right = child.right - params.rightMargin + mDividerSize
            val top = child.bottom + params.bottomMargin +
                    Math.round(ViewCompat.getTranslationY(child))
            val bottom = top + mDividerSize
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val position = parent.getChildAdapterPosition(view)
        val spanCount = getSpanCount(parent)
        if (spanCount == -1)
            throw ClassCastException("Can not cast" + parent.layoutManager + "to GridLayoutManager")
        if (isLastCloum(position, spanCount)) {
            //如果是最后一列则不绘制右边的Divider
            outRect.set(0, 0, 0, mDividerSize)
        } else {
            outRect.set(0, 0, mDividerSize, mDividerSize)
        }
    }

    private fun isLastCloum(itemPosition: Int, spanCount: Int): Boolean {
        return (itemPosition + 1) % spanCount == 0

    }


    private fun getSpanCount(parent: RecyclerView): Int {
        val layoutManager = parent.layoutManager
        return (layoutManager as? GridLayoutManager)?.spanCount ?: -1
    }
}