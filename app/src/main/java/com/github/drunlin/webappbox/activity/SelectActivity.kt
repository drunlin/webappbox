package com.github.drunlin.webappbox.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.data.Shortcut
import com.github.drunlin.webappbox.fragment.OnShortcutCreated
import kotlinx.android.synthetic.main.toolbar.*

class SelectActivity : TranslucentStatusActivity(), OnShortcutCreated {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_select)

        setSupportActionBar(toolbar)
    }

    @Suppress("DEPRECATION")
    override fun onShortcutCreated(itemView: View, shortcut: Shortcut) {
        itemView.setOnClickListener {
            val intent = Intent()
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, WebappActivity.start(shortcut.uuid))
                .putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcut.name)
                .putExtra(Intent.EXTRA_SHORTCUT_ICON, shortcut.icon)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
