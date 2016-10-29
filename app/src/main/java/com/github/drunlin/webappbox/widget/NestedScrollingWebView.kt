package com.github.drunlin.webappbox.widget

import android.content.Context
import android.os.Build
import android.support.v4.view.NestedScrollingChild
import android.support.v4.view.NestedScrollingChildHelper
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView

class NestedScrollingWebView(context: Context, attar: AttributeSet) :
        WebView(context, attar), NestedScrollingChild {

    private val nestedScrollingHelper by lazy { NestedScrollingChildHelper(this) }

    private var nestedScrollY = 0
    private var onNestedScrolling = false

    init {
        isNestedScrollingEnabled = true
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)

        onNestedScrolling = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> startNestedScroll(event)
            MotionEvent.ACTION_MOVE -> if (onNestedScrolling && nestedScroll(event)) return true
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> stopNestedScroll()
        }
        return super.onTouchEvent(event)
    }

    private fun startNestedScroll(event: MotionEvent) {
        nestedScrollY = event.y.toInt()
        onNestedScrolling = false
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
    }

    private fun nestedScroll(event: MotionEvent): Boolean {
        val y = event.y.toInt()
        val dy = nestedScrollY - y
        nestedScrollY = y
        return dispatchNestedPreScroll(0, dy, null, null)
                || !canScrollVertically(dy) && dispatchNestedScroll(0, 0, 0, dy, null)
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            super.setNestedScrollingEnabled(enabled)
        else
            nestedScrollingHelper.isNestedScrollingEnabled = true
    }

    override fun isNestedScrollingEnabled(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return super.isNestedScrollingEnabled()
        else
            return nestedScrollingHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return super.startNestedScroll(axes)
        else
            return nestedScrollingHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            super.stopNestedScroll()
        else
            nestedScrollingHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return super.hasNestedScrollingParent()
        else
            return nestedScrollingHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?,
                                         offsetInWindow: IntArray?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
        else
            return nestedScrollingHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
                                      dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed,
                    dyUnconsumed, offsetInWindow)
        else
            return nestedScrollingHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                    dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return super.dispatchNestedPreFling(velocityX, velocityY)
        else
            return nestedScrollingHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return super.dispatchNestedFling(velocityX, velocityY, consumed)
        else
            return nestedScrollingHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            nestedScrollingHelper.onDetachedFromWindow()
    }
}
