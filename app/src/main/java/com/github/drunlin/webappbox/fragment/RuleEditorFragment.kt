package com.github.drunlin.webappbox.fragment

import android.databinding.ViewDataBinding
import android.os.Build
import android.os.Bundle
import android.view.View
import com.github.drunlin.webappbox.BR
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.BUNDLE_UA
import com.github.drunlin.webappbox.common.showDialog
import com.github.drunlin.webappbox.data.LaunchMode
import com.github.drunlin.webappbox.data.Orientation
import com.github.drunlin.webappbox.data.Rule
import com.github.drunlin.webappbox.data.UserAgent
import com.github.drunlin.webappbox.databinding.FragmentRuleEditorBinding
import com.github.drunlin.webappbox.model.RuleManager
import com.github.drunlin.webappbox.model.UserAgentManager
import kotlinx.android.synthetic.main.checkable_item.view.*
import kotlinx.android.synthetic.main.fragment_rule_editor.*
import javax.inject.Inject

class RuleEditorFragment(id: Long?) : EditorFragment<Rule>(id),
        ColorPickerFragment.OnColorSelectedListener, UserAgentsFragment.OnChangeListener {

    @Inject lateinit var ruleManager: RuleManager
    @Inject lateinit var userAgentManager: UserAgentManager

    override val data by lazy { id?.let { ruleManager.getRule(it) } }

    override val titleResId = id?.let { R.string.edit_rule } ?: R.string.add_rule
    override val contentViewResId = R.layout.fragment_rule_editor

    private val userAgents by lazy { userAgentManager.userAgents }

    private var userAgent: UserAgent? = null

    constructor() : this(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as WebappContext).component.inject(this)

        val ua = savedInstanceState?.run { userAgentManager.getUserAgent(getLong(BUNDLE_UA)) }
                ?: data?.userAgent
        userAgent = if (ua in userAgents) ua else null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        userAgent?.run { outState.putLong(BUNDLE_UA, id) }
    }

    override fun onBindData(binding: ViewDataBinding, data: Rule) {
        binding.setVariable(BR.rule, data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editor.onStateChange = { confirmMenu?.isEnabled = it }
        editor.isExisted = { v, b -> ruleManager.isExited(v, b) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            colorItem.setOnClickListener { showDialog(ColorPickerFragment(colorView.color)) }
        else
            colorItem.visibility = View.GONE

        updateUserAgentSummary()
        userAgentItem.setOnClickListener {
            activity.replaceContentFragment(UserAgentsFragment(userAgent?.id, this))
        }
    }

    private fun updateUserAgentSummary() {
        (binding as FragmentRuleEditorBinding).userAgentItem!!.summary = userAgent?.name
    }

    override fun onConfigureView(data: Rule) {
        launchModeSpinner.value = data.launchMode.name

        orientationSpinner.value = data.orientation.name
    }

    override fun onColorSelected(color: Int) {
        colorView.color = color
    }

    override fun onUserAgentChange(userAgent: UserAgent?) {
        this.userAgent = userAgent

        updateUserAgentSummary()
    }

    override fun onCommit() {
        val pattern = editor.value
        val regex = editor.regex
        val color = colorView.color
        val lm = LaunchMode.valueOf(launchModeSpinner.value)
        val so = Orientation.valueOf(orientationSpinner.value)
        val ua = userAgent ?: data?.userAgent ?: userAgents.getOrNull(0)
        val fs = fullScreenItem.switcher.isChecked
        val js = enableJavascriptItem.switcher.isChecked

        id?.run { ruleManager.update(this, pattern, regex, color, lm, so, fs, ua, js) }
                ?: ruleManager.insert(pattern, regex, color, lm, so, fs, ua, js)
    }
}
