package com.github.drunlin.webappbox.widget

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.util.AttributeSet

class AppEditText(context: Context, attar: AttributeSet) : TextInputEditText(context, attar) {
    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)

        setSelection(length())
    }
}
