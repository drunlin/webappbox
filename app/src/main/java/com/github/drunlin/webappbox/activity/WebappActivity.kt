package com.github.drunlin.webappbox.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.EXTRA_UUID
import com.github.drunlin.webappbox.common.app
import com.github.drunlin.webappbox.common.setClass
import com.github.drunlin.webappbox.fragment.WebappContext
import com.github.drunlin.webappbox.fragment.WebappFragment
import com.github.drunlin.webappbox.model.WebappManager
import javax.inject.Inject

open class WebappActivity : FragmentActivity(), WebappContext {
    companion object {
        internal val Intent.uuid: String get() = data.getQueryParameter(EXTRA_UUID)

        internal fun intent(uuid: String) = Intent()
                .setClass(WebappActivity::class.java)
                .setData(Uri.parse("?$EXTRA_UUID=$uuid"))

        fun start(uuid: String) = LauncherActivity.start(uuid)
    }

    @Inject lateinit var webappManager: WebappManager

    override val component by lazy { app.webappComponent(id!!) }

    private val id by lazy { webappManager.getWebappId(intent.uuid) }

    override fun onCreate(savedInstanceState: Bundle?) {
        app.component.inject(this)
        super.onCreate(savedInstanceState)

        if (intent.flags and Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS != 0)
            finish()
        else
            create(savedInstanceState)
    }

    private fun create(savedInstanceState: Bundle?) {
        if (id != null) {
            savedInstanceState ?: setContentFragment(WebappFragment())
        } else {
            Toast.makeText(this, R.string.app_not_found, Toast.LENGTH_SHORT).show()
            finishAndRemoveTaskCompat()
        }
    }

    private fun finishAndRemoveTaskCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask()
        } else {
            val intent = Intent()
                    .setComponent(intent.component)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            startActivity(intent)
            finish()
        }
    }
}
