package com.harsom.delemu.crop

import android.content.Context
import android.util.AttributeSet
import com.harsom.delemu.R

/**
 * Created by ZouWei on 2018/8/13.
 */

class CropConfig {
    companion object {
        val DEFAULT_VIEWPORT_RATIO = 0f
        val DEFAULT_MAXIMUM_SCALE = 10f
        val DEFAULT_MINIMUM_SCALE = 0f
        val DEFAULT_IMAGE_QUALITY = 100
        val DEFAULT_VIEWPORT_OVERLAY_PADDING = 0
        val DEFAULT_VIEWPORT_OVERLAY_COLOR = -0x38000000 // Black with 200 alpha


        fun from(context: Context, attrs: AttributeSet?): CropConfig {
            val cropConfig = CropConfig()
            if (attrs == null) {
                return cropConfig
            }
            val attributes = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.CropView)
            cropConfig.viewportRatio = attributes.getFloat(R.styleable.CropView_ViewportRatio, CropConfig.DEFAULT_VIEWPORT_RATIO)
            cropConfig.maxScale = attributes.getFloat(R.styleable.CropView_MaxScale, CropConfig.DEFAULT_MAXIMUM_SCALE)
            cropConfig.minScale = attributes.getFloat(R.styleable.CropView_MinScale, CropConfig.DEFAULT_MINIMUM_SCALE)
            cropConfig.viewportOverlayColor = attributes.getColor(R.styleable.CropView_ViewportOverlayColor, CropConfig.DEFAULT_VIEWPORT_OVERLAY_COLOR)
            cropConfig.viewportOverlayPadding = attributes.getDimensionPixelSize(R.styleable.CropView_ViewportOverlayPadding, CropConfig.DEFAULT_VIEWPORT_OVERLAY_PADDING)
            attributes.recycle()
            return cropConfig
        }
    }

    var viewportRatio = DEFAULT_VIEWPORT_RATIO
    var maxScale = DEFAULT_MAXIMUM_SCALE
    var minScale = DEFAULT_MINIMUM_SCALE
    var viewportOverlayPadding = DEFAULT_VIEWPORT_OVERLAY_PADDING
    var viewportOverlayColor = DEFAULT_VIEWPORT_OVERLAY_COLOR
}