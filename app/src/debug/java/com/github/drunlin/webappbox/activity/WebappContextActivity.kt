package com.github.drunlin.webappbox.activity

import com.github.drunlin.webappbox.common.app
import com.github.drunlin.webappbox.fragment.WebappContext
import com.github.drunlin.webappbox.module.WebappModule.Flag.NEW

class WebappContextActivity : FragmentActivity(), WebappContext {
    override val component by lazy { app.webappComponent(0, NEW) }
}
