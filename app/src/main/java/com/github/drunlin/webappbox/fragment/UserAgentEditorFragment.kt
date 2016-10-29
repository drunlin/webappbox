package com.github.drunlin.webappbox.fragment

import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.github.drunlin.webappbox.BR
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.app
import com.github.drunlin.webappbox.common.positiveButton
import com.github.drunlin.webappbox.common.string
import com.github.drunlin.webappbox.data.UserAgent
import com.github.drunlin.webappbox.model.UserAgentManager
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.fragment_user_agnet_editor.*
import kotlinx.android.synthetic.main.text_input.view.*
import rx.Observable
import javax.inject.Inject

class UserAgentEditorFragment(id: Long?) : EditorDialogFragment<UserAgent>(id) {
    @Inject lateinit var userAgentManager: UserAgentManager

    override val data by lazy { id?.let { userAgentManager.getUserAgent(it) } }

    override val titleResId = id?.let { R.string.edit_user_agent } ?: R.string.add_user_agent
    override val layoutResId = R.layout.fragment_user_agnet_editor

    constructor() : this(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app.component.inject(this)
    }

    override fun onDialogCreated(dialog: AlertDialog, savedInstanceState: Bundle?) {
        super.onDialogCreated(dialog, savedInstanceState)

        val nameObserver = RxTextView.textChanges(nameInput.edit)
                .map { it.trim().toString() }
                .map { onNameChange(it) }

        val uaObserver = RxTextView.textChanges(uaInput.edit)
                .map { it.trim().toString() }
                .map { onUserAgentChange(it) }

        Observable.combineLatest(nameObserver, uaObserver, { a, b -> a && b })
                .subscribe { dialog.positiveButton.isEnabled = it }
    }

    private fun onUserAgentChange(userAgent: String): Boolean {
        if (userAgent.isEmpty()) {
            uaInput.layout.isErrorEnabled = false
        } else if (userAgent != data?.value && userAgentManager.isValueExited(userAgent)) {
            uaInput.layout.error = getString(R.string.exited_ua)
        } else {
            uaInput.layout.isErrorEnabled = false
            return true
        }
        return false
    }

    private fun onNameChange(name: String): Boolean {
        if (name.isEmpty()) {
            nameInput.layout.isErrorEnabled = false
        } else if (name != data?.name && userAgentManager.isNameExited(name)) {
            nameInput.layout.error = getString(R.string.exited_name)
        } else {
            nameInput.layout.isErrorEnabled = false
            return true
        }
        return false
    }

    override fun onCommit() {
        id?.run { userAgentManager.update(this, nameInput.edit.string, uaInput.edit.string) }
                ?: userAgentManager.insert(nameInput.edit.string, uaInput.edit.string)
    }

    override fun onBindData(binding: ViewDataBinding, data: UserAgent) {
        binding.setVariable(BR.userAgent, data)
    }
}
