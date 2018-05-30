package com.github.drunlin.webappbox.fragment

import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.activity.FragmentActivity
import com.github.drunlin.webappbox.common.*
import com.github.drunlin.webappbox.data.UserAgent
import com.github.drunlin.webappbox.model.PreferenceModel
import com.github.drunlin.webappbox.model.WebappManager
import com.thebluealliance.spectrum.SpectrumPreferenceCompat
import javax.inject.Inject

class PreferencesFragment : PreferenceFragmentCompat(), UserAgentsFragment.OnChangeListener {
    @Inject lateinit var preferenceModel: PreferenceModel
    @Inject lateinit var webappManager: WebappManager

    private val userAgent: UserAgent get() = preferenceModel.defaultRule.userAgent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app.component.inject(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = PreferenceModel.PREFERENCE_NAME
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            findPreference(PREF_COLOR).isVisible = false

        updateUserAgentSummary()
    }

    private fun updateUserAgentSummary() {
        findPreference(PREF_USER_AGENT).summary = userAgent.name
    }

    override fun onUserAgentChange(userAgent: UserAgent?) {
        preferenceModel.setUserAgent(userAgent)

        updateUserAgentSummary()
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (!SpectrumPreferenceCompat.onDisplayPreferenceDialog(preference, this))
            super.onDisplayPreferenceDialog(preference)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            PREF_USER_AGENT -> startUserAgentsFragment()
            PREF_CLEAR_DATA -> clearData()
            PREF_CLEAR_CACHE -> clearCache()
            else -> return super.onPreferenceTreeClick(preference)
        }
        return true
    }

    private fun startUserAgentsFragment() {
        (activity as FragmentActivity).replaceContentFragment(UserAgentsFragment(userAgent.id, this))
    }

    private fun clearData() {
        webappManager.clearData()

        Snackbar.make(view!!, R.string.data_cleared, Snackbar.LENGTH_SHORT).show()
    }

    private fun clearCache() {
        webappManager.clearCache()

        Snackbar.make(view!!, R.string.cache_cleared, Snackbar.LENGTH_SHORT).show()
    }
}
