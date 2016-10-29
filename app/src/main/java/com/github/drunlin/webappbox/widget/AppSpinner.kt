package com.github.drunlin.webappbox.widget

import android.content.Context
import android.support.v7.widget.AppCompatSpinner
import android.util.AttributeSet
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.widget.adapter.SpinnerAdapter

class AppSpinner(context: Context, attar: AttributeSet) : AppCompatSpinner(context, attar) {
    private var _values: Array<String>?
    var values: Array<String>
        set(value) { _values = value }
        get() = _values!!

    var value: String
        set(value) { setSelection(values.indexOf(value)) }
        get() = values[selectedItemPosition]

    init {
        val array = context.obtainStyledAttributes(attar, R.styleable.AppSpinner)

        val title = array.getString(R.styleable.AppSpinner_title) ?: ""
        val entries = array.getTextArray(R.styleable.AppSpinner_entries)
        entries?.run { adapter = SpinnerAdapter(context, title, toList()) }

        _values = array.getTextArray(R.styleable.AppSpinner_values)?.map { "$it" }?.toTypedArray()

        array.recycle()
    }
}
