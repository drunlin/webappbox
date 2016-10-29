package com.github.drunlin.webappbox.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import com.github.drunlin.webappbox.common.STATE_COLOR
import com.github.drunlin.webappbox.common.STATE_SUPER

class ColorView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint: Paint

    init {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.BLACK
    }

    var color: Int
        get() = paint.color
        set(color) {
            paint.color = color
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        val radius = (measuredWidth / 2).toFloat()
        canvas.drawCircle(radius, radius, radius, paint)
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(STATE_SUPER, super.onSaveInstanceState())
        bundle.putInt(STATE_COLOR, color)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val bundle = state as Bundle
        super.onRestoreInstanceState(bundle.getParcelable(STATE_SUPER))
        color = state.getInt(STATE_COLOR)
    }
}
