package com.github.drunlin.webappbox.fragment

import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.github.drunlin.webappbox.BR
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.positiveButton
import com.github.drunlin.webappbox.data.URLPattern
import com.github.drunlin.webappbox.model.PatternManager
import kotlinx.android.synthetic.main.fragment_pattern_editor.*
import javax.inject.Inject

class PatternEditorFragment(id: Long?) : EditorDialogFragment<URLPattern>(id) {
    @Inject lateinit var patternManager: PatternManager

    override val data by lazy { id?.let { patternManager.getPattern(it) } }

    override val titleResId by lazy { id?.let { R.string.edit_pattern } ?: R.string.add_pattern }
    override val layoutResId = R.layout.fragment_pattern_editor

    constructor() : this(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (context as WebappContext).component.inject(this)
    }

    override fun onDialogCreated(dialog: AlertDialog, savedInstanceState: Bundle?) {
        super.onDialogCreated(dialog, savedInstanceState)

        editor.onStateChange = { dialog.positiveButton.isEnabled = it }
        editor.isExisted = { v, b -> patternManager.isExited(v, b) }
        editor.requestValidate()
    }

    override fun onCommit() {
        id?.run { patternManager.update(this, editor.value, editor.regex) }
                ?: patternManager.insert(editor.value, editor.regex)
    }

    override fun onBindData(binding: ViewDataBinding, data: URLPattern) {
        binding.setVariable(BR.pattern, data)
    }
}
