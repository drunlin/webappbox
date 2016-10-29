package com.github.drunlin.webappbox.widget

import android.content.Context
import android.util.AttributeSet
import com.github.drunlin.webappbox.BR
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.isValidUrl
import com.github.drunlin.webappbox.common.string
import com.github.drunlin.webappbox.data.URLPattern
import com.github.drunlin.webappbox.databinding.WidgetPatternBinding
import com.jakewharton.rxbinding.widget.RxCompoundButton
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.text_input.view.*
import kotlinx.android.synthetic.main.widget_pattern.view.*
import rx.Observable
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class PatternEditor(context: Context, attar: AttributeSet) : ViewStateLayout(context, attar) {
    var isExisted: ((String, Boolean) -> Boolean)? = null
    var onStateChange: ((Boolean) -> Unit)? = null

    val value: String get() = textInput.edit.string
    val regex: Boolean get() = checkbox.isChecked

    var pattern: URLPattern? = null
        set(value) {
            field = value
            binding.setVariable(BR.pattern, value)
            binding.executePendingBindings()
        }

    private lateinit var binding: WidgetPatternBinding

    init {
        inflate(context, R.layout.widget_pattern, this)

        if (!isInEditMode) init()
    }

    private fun init() {
        binding = WidgetPatternBinding.bind(getChildAt(0))

        val editObservable = RxTextView.textChanges(textInput.edit).map { it.toString().trim() }
        val checkboxObservable = RxCompoundButton.checkedChanges(checkbox)
        Observable.combineLatest(editObservable, checkboxObservable, { a, b -> arrayOf(a, b) })
                .skip(1)
                .map { onChange(it[0] as String, it[1] as Boolean) }
                .subscribe { onStateChange?.invoke(it) }
    }

    fun requestValidate() {
        onStateChange?.invoke(onChange(textInput.edit.string, checkbox.isChecked))
    }

    private fun onChange(value: String, regex: Boolean): Boolean {
        if (value.isEmpty()) {
            layout.isErrorEnabled = false
            return false
        }

        if (regex) {
            try {
                Pattern.compile(value)
            } catch (e: PatternSyntaxException) {
                textInput.layout.error = context.getString(R.string.invalid_regex)
                return false
            }
        } else if (!value.isValidUrl()) {
            textInput.layout.error = context.getString(R.string.invalid_url)
            return false
        }

        if ((pattern?.pattern != value || pattern?.regex != regex)
                && isExisted?.invoke(value, regex) ?: false) {
            textInput.layout.error = context.getString(R.string.exited_pattern)
            return false
        }

        textInput.layout.isErrorEnabled = false
        return true
    }
}
