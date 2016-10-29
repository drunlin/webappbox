package com.github.drunlin.webappbox.fragment

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.ARGUMENT_ID
import kotlinx.android.synthetic.main.fragment_preview.*

abstract class EditorFragment<T>(id: Long?) : SecondaryFragment() {
    abstract protected val data: T?

    override val menuResId = R.menu.fragment_editor

    protected val id by lazy { arguments?.getLong(ARGUMENT_ID) }

    lateinit protected var binding: ViewDataBinding

    protected var confirmMenu: MenuItem? = null
        private set

    init {
        id?.run { arguments = Bundle().apply { putLong(ARGUMENT_ID, id) } }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        confirmMenu = toolbar.menu.findItem(R.id.menu_confirm)
        confirmMenu!!.isEnabled = id != null

        view.findFocus() ?: view.focusSearch(View.FOCUS_FORWARD)?.requestFocus()

        binding = DataBindingUtil.bind<ViewDataBinding>(contentView)
        savedInstanceState ?: data?.run { onBindData(binding, this) }
        binding.executePendingBindings()
    }

    abstract protected fun onBindData(binding: ViewDataBinding, data: T)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        savedInstanceState ?: data?.run { onConfigureView(this) }
    }

    abstract protected fun onConfigureView(data: T)

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_confirm) {
            onCommit()
            activity.onBackPressed()
        }
        return true
    }

    abstract protected fun onCommit()
}
