package com.github.drunlin.webappbox.model

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import com.github.drunlin.webappbox.common.*
import com.github.drunlin.webappbox.data.*
import com.github.drunlin.webappbox.module.Injectable
import javax.inject.Inject

class PreferenceModel : Injectable, SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        val PREFERENCE_NAME = "settings"
    }

    @Inject lateinit var context: Context
    @Inject lateinit var userAgentManager: UserAgentManager

    var onChange: Callback<(String) -> Unit> = Callback()

    private val preferences by lazy {
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    lateinit private var userAgent: UserAgent

    private var _defaultRule: Rule? = null
    val defaultRule: Rule
        get() {
            if (_defaultRule == null) {
                val launchMode = LaunchMode.valueOf(
                        preferences.getString(PREF_LAUNCH_MODE, LaunchMode.STANDARD.name))
                val orientation = Orientation.valueOf(
                        preferences.getString(PREF_ORIENTATION, Orientation.NORMAL.name))
                _defaultRule = Rule(-1, URLPattern(), preferences.getInt(PREF_COLOR, Color.BLACK),
                        launchMode, orientation, preferences.getBoolean(PREF_FULL_SCREEN, false),
                        userAgent, preferences.getBoolean(PREF_ENABLE_JS, true))
            }
            return _defaultRule!!
        }

    override fun init() {
        userAgent = userAgentManager.getUserAgent(preferences.getLong(PREF_USER_AGENT, -1))

        preferences.registerOnSharedPreferenceChangeListener(this)

        userAgentManager.onRemove.add(this) {
            if (userAgent !in userAgentManager.userAgents) setUserAgent(null)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        _defaultRule = null
        onChange.invoke { it(key) }
    }

    fun setUserAgent(value: UserAgent?) {
        userAgent = value ?: userAgentManager.defaultUserAgent
        preferences.edit().putLong(PREF_USER_AGENT, value?.id ?: -1).apply()
    }
}
