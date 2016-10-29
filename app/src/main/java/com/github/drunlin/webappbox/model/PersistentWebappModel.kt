package com.github.drunlin.webappbox.model

import com.github.drunlin.webappbox.common.runOnIoThread
import com.github.drunlin.webappbox.data.Policy
import javax.inject.Inject

class PersistentWebappModel(id: Long) : WebappModel(id) {
    @Inject lateinit var databaseManager: DatabaseManager
    @Inject lateinit var webappManager: WebappManager

    override val webapp by lazy { databaseManager.getWebapp(id) }
    override val originalUrl by lazy { webapp.url }

    override fun setLocationPolicy(policy: Policy) {
        super.setLocationPolicy(policy)

        runOnIoThread { databaseManager.updateLocationPolicy(id, policy) }
    }
}
