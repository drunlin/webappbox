package com.github.drunlin.webappbox.activity

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.app
import com.github.drunlin.webappbox.data.Shortcut
import com.github.drunlin.webappbox.fragment.OnShortcutCreated
import com.github.drunlin.webappbox.model.WebappManager
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

class MainActivity : TranslucentStatusActivity(), OnShortcutCreated {
    @Inject lateinit var webappManager: WebappManager

    private var selectionId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app.component.inject(this)

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.apps)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new -> startActivity(WebappEditorActivity.new())
            R.id.menu_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.menu_about -> startActivity(Intent(this, AboutActivity::class.java))
        }
        return true
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        info: ContextMenu.ContextMenuInfo?
    ) {
        menuInflater.inflate(R.menu.item_webapp, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_edit -> startActivity(WebappEditorActivity.edit(selectionId))
            R.id.menu_delete -> webappManager.delete(selectionId)
            R.id.menu_add_shortcut -> webappManager.installShortcut(selectionId)
        }
        return true
    }

    override fun onShortcutCreated(itemView: View, shortcut: Shortcut) {
        itemView.setOnClickListener { startActivity(WebappActivity.start(shortcut.uuid)) }
        itemView.setOnCreateContextMenuListener(this@MainActivity)
        itemView.setOnLongClickListener {
            selectionId = shortcut.id
            itemView.showContextMenu()
            return@setOnLongClickListener true
        }
    }
}
