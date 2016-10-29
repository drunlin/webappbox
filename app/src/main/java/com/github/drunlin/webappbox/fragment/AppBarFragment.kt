package com.github.drunlin.webappbox.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.activity.FragmentActivity
import kotlinx.android.synthetic.main.fragment_toolbar.*
import kotlinx.android.synthetic.main.fragment_toolbar.view.*
import kotlinx.android.synthetic.main.toolbar.*

abstract class AppBarFragment : Fragment(), Toolbar.OnMenuItemClickListener {
    open protected val titleResId: Int? = null
    open protected val menuResId: Int? = null
    open protected val viewResId = R.layout.fragment_toolbar
    open protected val contentViewResId: Int? = null

    protected val activity: FragmentActivity get() = getActivity() as FragmentActivity
    protected val contentView: View get() = container.getChildAt(0)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(viewResId, container, false)
        contentViewResId?.run { inflater.inflate(this, view.container) }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setBackgroundResource(android.R.color.background_light)

        titleResId?.run { toolbar.setTitle(this) }
        if (menuResId != null) {
            toolbar.inflateMenu(menuResId!!)
            toolbar.setOnMenuItemClickListener(this)
        }
    }

    override fun onMenuItemClick(item: MenuItem) = false
}
