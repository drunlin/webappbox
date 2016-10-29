package com.github.drunlin.webappbox.activity

import android.os.Bundle
import com.github.drunlin.webappbox.fragment.SettingsFragment

class SettingsActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState ?: setContentFragment(SettingsFragment())
    }
}
