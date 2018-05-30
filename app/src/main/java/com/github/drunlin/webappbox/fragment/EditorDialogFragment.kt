package com.github.drunlin.webappbox.fragment

import android.app.Dialog
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.ARGUMENT_ID

abstract class EditorDialogFragment<T>(id: Long?) : CustomViewDialogFragment() {
    abstract protected val data: T?
    abstract protected val titleResId: Int

    protected val id by lazy { arguments?.getLong(ARGUMENT_ID) }

    init {
        id?.run { arguments = Bundle().apply { putLong(ARGUMENT_ID, id) } }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
                .setTitle(titleResId)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(id?.let { R.string.ok } ?: R.string.add) { d, i -> onCommit() }
                .create()
                .apply { setOnShowListener { onDialogCreated(this, savedInstanceState) } }
    }

    protected abstract fun onCommit()

    open protected fun onDialogCreated(dialog: AlertDialog, savedInstanceState: Bundle?) {
        dialog.setOnShowListener(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = DataBindingUtil.bind<ViewDataBinding>(view)!!
        savedInstanceState ?: data?.run { onBindData(binding, this) }
        binding.executePendingBindings()
    }

    abstract protected fun onBindData(binding: ViewDataBinding, data: T)
}
