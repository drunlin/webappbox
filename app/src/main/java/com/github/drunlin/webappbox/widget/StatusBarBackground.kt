package com.github.drunlin.webappbox.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewParent

class StatusBarBackground(context: Context, attar: AttributeSet) : View(context, attar) {
    private lateinit var contentLayout: ContentLayout

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (isInEditMode) return

        contentLayout = findContentLayout()
        contentLayout.onStatusBarHeightChange.add(this) { setHeight(it) }
        setHeight(contentLayout.statusBarHeight)
    }

    tailrec fun findContentLayout(parent: ViewParent = getParent()): ContentLayout
            = parent as? ContentLayout ?: findContentLayout(parent.parent)

    private fun setHeight(height: Int) {
        layoutParams.height = height
        requestLayout()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        contentLayout.onStatusBarHeightChange.remove(this)
    }
}
