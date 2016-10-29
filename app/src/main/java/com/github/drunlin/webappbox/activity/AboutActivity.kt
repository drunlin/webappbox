package com.github.drunlin.webappbox.activity

import android.os.Bundle
import com.github.drunlin.webappbox.fragment.AboutFragment

class AboutActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState ?: setContentFragment(AboutFragment())
    }
}
