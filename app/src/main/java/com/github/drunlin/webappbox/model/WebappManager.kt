package com.github.drunlin.webappbox.model

import android.content.Context
import android.content.Intent
import android.os.Build
import android.webkit.*
import com.github.drunlin.webappbox.activity.WebappActivity
import com.github.drunlin.webappbox.common.*
import com.github.drunlin.webappbox.data.Shortcut
import com.github.drunlin.webappbox.module.WebappComponent
import java.util.*
import javax.inject.Inject

class WebappManager : ObservableModel() {
    @Inject lateinit var context: Context
    @Inject lateinit var databaseManager: DatabaseManager

    val onRemove: Callback<(Int) -> Unit> = Callback()

    val shortcuts by lazy { databaseManager.getShortcuts() }

    fun insert(component: WebappComponent, addShortcut: Boolean) {
        val uuid = UUID.randomUUID().toString().replace("-", "")
        val webapp = component.webappModel.webapp
        asyncCall({
            val id = databaseManager.insert(uuid, webapp)
            component.patternManager.patterns.forEach { databaseManager.insert(id, it) }
            component.ruleManager.rules.forEach { databaseManager.insert(id, it) }
            return@asyncCall id
        }) {
            val shortcut = Shortcut(it, uuid, webapp.icon, webapp.name)
            shortcuts.add(shortcut)
            onInsert.invoke { it(shortcuts.lastIndex) }
            if (addShortcut) installShortcut(shortcut)
        }
    }

    fun update(component: WebappComponent, addShortcut: Boolean) {
        val webapp = component.webappModel.webapp
        val (index, shortcut) = shortcuts.findIndexedValue { it.id == webapp.id }
        shortcut.icon = webapp.icon
        shortcut.name = webapp.name
        onUpdate.invoke { it(index) }
        if (addShortcut) installShortcut(shortcut)

        runOnIoThread { databaseManager.update(webapp) }
    }

    fun delete(id: Long) {
        val (index, shortcut) = shortcuts.findIndexedValue { it.id == id }
        shortcuts.removeAt(index)
        onRemove.invoke { it(index) }
        uninstallShortcut(shortcut)

        runOnIoThread { databaseManager.deleteWebapp(id) }
    }

    fun isExisted(url: String) = databaseManager.isWebappExisted(url)

    fun getWebappId(uuid: String) = databaseManager.getWebappId(uuid)

    fun filter(name: String): List<Shortcut> {
        return if (name.isEmpty()) shortcuts else shortcuts.filter { it.name.contains(name) }
    }

    fun installShortcut(id: Long) {
        installShortcut(shortcuts.find { it.id == id }!!)
    }

    fun installShortcut(shortcut: Shortcut) {
        val intent = Intent("com.android.launcher.action.INSTALL_SHORTCUT")
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, WebappActivity.start(shortcut.uuid))
                .putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcut.name)
                .putExtra(Intent.EXTRA_SHORTCUT_ICON, shortcut.icon)
                .putExtra(EXTRA_SHORTCUT_DUPLICATE, true)
        context.sendBroadcast(intent)
    }

    fun uninstallShortcut(shortcut: Shortcut) {
        val intent = Intent("com.android.launcher.action.UNINSTALL_SHORTCUT")
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, WebappActivity.start(shortcut.uuid))
                .putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcut.name)
                .putExtra(EXTRA_SHORTCUT_DUPLICATE, true)
        context.sendBroadcast(intent)
    }

    @Suppress("DEPRECATION")
    fun clearData() {
        clearCache()

        val db = WebViewDatabase.getInstance(context)
        db.clearFormData()
        db.clearHttpAuthUsernamePassword()

        WebStorage.getInstance().deleteAllData()

        val cm = CookieManager.getInstance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cm.removeAllCookies { runOnIoThread { cm.flush() } }
        } else {
            val sm = CookieSyncManager.createInstance(context)
            sm.startSync()
            cm.removeAllCookie()
            sm.stopSync()
            sm.sync()
        }
    }

    fun clearCache() {
        WebView(context).clearCache(true)
    }
}
