package com.github.drunlin.webappbox.fragment

import android.app.ActivityManager
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.add
import com.github.drunlin.webappbox.common.showDialog
import com.github.drunlin.webappbox.common.string
import com.github.drunlin.webappbox.model.WebappModel
import kotlinx.android.synthetic.main.fragment_preview.*
import javax.inject.Inject

class PreviewFragment() : AppBarFragment() {
    @Inject lateinit var webappModel: WebappModel

    override val menuResId = R.menu.fragment_priview
    override val viewResId = R.layout.fragment_preview

    private val webapp by lazy { webappModel.webapp }

    private val webappFragment by lazy {
        childFragmentManager.findFragmentById(R.id.webapp) as WebappFragment?
                ?: childFragmentManager.add(R.id.webapp, WebappFragment())
    }

    init {
        retainInstance = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as WebappContext).component.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webappFragment.onUrlChange = { urlEdit.setText(it) }

        urlEdit.setText(webapp.url)
        urlEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                webappFragment.loadUrl(urlEdit.string)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_restart -> webappFragment.loadUrl(webapp.url)
            R.id.menu_new_rule -> activity.replaceContentFragment(RuleEditorFragment())
            R.id.menu_rules -> activity.replaceContentFragment(RulesFragment())
            R.id.menu_new_pattern -> showDialog(PatternEditorFragment())
            R.id.menu_patterns -> activity.replaceContentFragment(PatternsFragment())
            R.id.menu_set_url -> webappModel.setUrl(urlEdit.string)
            R.id.menu_exit -> activity.onBackPressed()
        }
        return true
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (hidden) restoreSystemUi()
    }

    override fun onDestroy() {
        super.onDestroy()

        restoreSystemUi()
    }

    private fun restoreSystemUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            activity.setTaskDescription(ActivityManager.TaskDescription())

        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        else
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}
