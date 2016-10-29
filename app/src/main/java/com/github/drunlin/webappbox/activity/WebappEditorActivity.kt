package com.github.drunlin.webappbox.activity

import android.content.Intent
import android.os.Bundle
import com.github.drunlin.webappbox.common.*
import com.github.drunlin.webappbox.fragment.WebappContext
import com.github.drunlin.webappbox.fragment.WebappEditorFragment
import com.github.drunlin.webappbox.module.WebappModule.Flag.EDIT
import com.github.drunlin.webappbox.module.WebappModule.Flag.NEW

class WebappEditorActivity : FragmentActivity(), WebappContext {
    companion object {
        fun new() = Intent(ACTION_NEW)
                .setClass(WebappEditorActivity::class.java)
                .putExtra(EXTRA_ID, generateId())!!

        fun edit(id: Long) = Intent(ACTION_EDIT)
                .setClass(WebappEditorActivity::class.java)
                .putExtra(EXTRA_ID, id)!!
    }

    override val component by lazy { app.webappComponent(id, if (new) NEW else EDIT) }

    private val id by lazy { intent.getLongExtra(EXTRA_ID) }
    private val new by lazy { intent.action == ACTION_NEW }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null)
            setContentFragment(if (new) WebappEditorFragment() else WebappEditorFragment(id))
    }
}
