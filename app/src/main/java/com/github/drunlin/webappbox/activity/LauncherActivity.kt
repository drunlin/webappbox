package com.github.drunlin.webappbox.activity

import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.activity.WebappActivity.Companion.uuid
import com.github.drunlin.webappbox.common.EXTRA_UUID
import com.github.drunlin.webappbox.common.app
import com.github.drunlin.webappbox.common.setClass
import com.github.drunlin.webappbox.model.WebappManager
import javax.inject.Inject

class LauncherActivity : Activity() {
    companion object {
        fun start(uuid: String) = Intent()
                .setAction(Intent.ACTION_VIEW)
                .setClass(LauncherActivity::class.java)
                .putExtra(EXTRA_UUID, uuid)!!
    }

    @Inject lateinit var webappManager: WebappManager

    private val uuid by lazy { intent.getStringExtra(EXTRA_UUID) }

    private val am by lazy { getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app.component.inject(this)

        if (webappManager.getWebappId(uuid) == null)
            Toast.makeText(this, R.string.app_not_found, Toast.LENGTH_SHORT).show()
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            startWebapp()
        else
            startCompatWebapp()

        finish()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startWebapp() {
        am.appTasks.find { isTargetWebappActivity(it.taskInfo.baseIntent) }?.moveToFront()
                ?: startActivity(WebappActivity.intent(uuid))
    }

    private fun isTargetWebappActivity(intent: Intent)
            = isWebappActivity(getActivityName(intent)) && intent.uuid == uuid

    private fun isWebappActivity(name: String) = name.startsWith(CLASS_NAME_PREFIX)

    private fun getActivityName(intent: Intent) = intent.component.className

    private fun startCompatWebapp() {
        @Suppress("DEPRECATION")
        val tasks = am.getRecentTasks(Int.MAX_VALUE, ActivityManager.RECENT_IGNORE_UNAVAILABLE)
        val task = tasks.find { isTargetWebappActivity(it.baseIntent) }
        val name = if (task != null) {
            if (task.id != -1) {
                am.moveTaskToFront(task.id, 0)
                return
            } else {
                getActivityName(task.baseIntent)
            }
        } else {
            val names = tasks.map { getActivityName(it.baseIntent) }.filter { isWebappActivity(it) }
            if (names.lastIndex < MAX_ACTIVITY_ID) {
                val ids = names.map { it.substringAfter(CLASS_NAME_PREFIX).toInt() }.toSet()
                "$CLASS_NAME_PREFIX${(0..MAX_ACTIVITY_ID).find { !ids.contains(it) }}"
            } else {
                names.last()
            }
        }
        val intent = WebappActivity.intent(uuid)
                .setClassName(this, name)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}
