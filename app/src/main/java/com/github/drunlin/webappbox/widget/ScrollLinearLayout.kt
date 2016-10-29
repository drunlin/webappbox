package com.github.drunlin.webappbox.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.ScrollView

class ScrollLinearLayout(context: Context, attar: AttributeSet) : ScrollView(context, attar) {
    private var inflating = true
    private var addingView = false

    private val container: LinearLayout

    init {
        container = LinearLayout(context)
        container.orientation = LinearLayout.VERTICAL
        super.addView(container, -1, ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
    }

    override fun getChildCount(): Int {
        return if (addingView) 0 else if (inflating) container.childCount else super.getChildCount()
    }

    override fun getChildAt(index: Int): View? {
        return if (inflating) container.getChildAt(index) else super.getChildAt(index)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        inflating = false

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun addView(child: View) {
        addingView = true

        super.addView(child)
    }

    override fun addView(child: View, index: Int) {
        addingView = true

        super.addView(child, index)
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        addingView = true

        super.addView(child, params)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        container.addView(child, index, LinearLayout.LayoutParams(params as MarginLayoutParams))

        addingView = false
    }
}
