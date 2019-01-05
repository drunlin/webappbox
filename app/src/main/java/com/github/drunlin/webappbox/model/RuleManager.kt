package com.github.drunlin.webappbox.model

import com.github.drunlin.webappbox.common.Callback
import com.github.drunlin.webappbox.common.findIndexedValue
import com.github.drunlin.webappbox.common.generateId
import com.github.drunlin.webappbox.data.*
import java.util.*
import javax.inject.Inject

open class RuleManager : DataManager<Rule>() {
    @Inject lateinit var settingsModel: PreferenceModel
    @Inject lateinit var userAgentManager: UserAgentManager

    val onMove: Callback<(Int, Int) -> Unit> = Callback()

    val rules by lazy { data }

    fun getRule(url: String) = rules.find { it.pattern.matches(url) } ?: settingsModel.defaultRule

    fun getRule(id: Long) = rules.find { it.id == id }!!

    fun isExited(pattern: String, regex: Boolean)
            = rules.any { it.pattern.pattern == pattern && it.pattern.regex == regex }

    open fun swap(from: Int, to: Int) {
        Collections.swap(rules, from, to)
        onMove.invoke { it(from, to) }
    }

    fun update(id: Long, pattern: String, regex: Boolean, color: Int, textZoom: Int, launchMode: LaunchMode,
               orientation: Orientation, fullScreen: Boolean, userAgent: UserAgent?, enableJS: Boolean) {
        val (index, rule) = rules.findIndexedValue { it.id == id }
        rule.pattern.pattern = pattern
        rule.pattern.regex = regex
        rule.color = color
        rule.textZoom = textZoom
        rule.launchMode = launchMode
        rule.orientation = orientation
        rule.fullScreen = fullScreen
        rule.userAgent = userAgent ?: userAgentManager.defaultUserAgent
        rule.jsEnabled = enableJS
        update(index, rule)
    }

    fun insert(pattern: String, regex: Boolean, color: Int, textZoom: Int, launchMode: LaunchMode,
               orientation: Orientation, fullScreen: Boolean,
               userAgent: UserAgent?, enableJS: Boolean) {
        val rule = Rule(generateId(), URLPattern(pattern, regex), color, textZoom, launchMode, orientation,
                fullScreen, userAgent ?: userAgentManager.defaultUserAgent, enableJS)
        insert(rule)
    }
}
