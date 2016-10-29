package com.github.drunlin.webappbox.widget

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.widget.FrameLayout
import com.github.drunlin.webappbox.common.Callback

class ContentLayout(context: Context, attar: AttributeSet?) : FrameLayout(context, attar) {
    companion object {
        val LOG_TAG = ContentLayout::class.java.name!!
    }

    val onStatusBarHeightChange = Callback<(Int) -> Unit>()

    var statusBarHeight = 0
        private set

    init {
        fitsSystemWindows = true
    }

    constructor(context: Context) : this(context, null)

    private fun onSystemInsetTopChange(height: Int) {
        statusBarHeight = height
        onStatusBarHeightChange.invoke { it(height) }
    }

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun fitSystemWindows(insets: Rect): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
            if (rootView.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN != 0) {
                onSystemInsetTopChange(0)
                insets.bottom = 0
            } else {
                onSystemInsetTopChange(insets.top)
            }
            insets.top = 0
            insets.left = 0
            insets.right = 0
        }
        return super.fitSystemWindows(insets)
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        Log.d(LOG_TAG, "onApplyWindowInsets(insets = $insets)")

        var bottom = insets.systemWindowInsetBottom
        if (rootView.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN != 0) {
            onSystemInsetTopChange(0)
            bottom = 0
        } else {
            onSystemInsetTopChange(insets.systemWindowInsetTop)
        }
        return super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0, bottom))
    }
}
