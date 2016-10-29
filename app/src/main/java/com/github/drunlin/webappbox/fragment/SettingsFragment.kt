package com.github.drunlin.webappbox.fragment

import android.os.Bundle
import android.view.View
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.add

class SettingsFragment : SecondaryFragment() {
    override val titleResId = R.string.settings

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState ?: childFragmentManager.add(R.id.container, PreferencesFragment())
    }
}
