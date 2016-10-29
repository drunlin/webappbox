package com.github.drunlin.webappbox.model

import javax.inject.Inject

class StoredWebappModel(id: Long) : WebappModel(id) {
    @Inject lateinit var databaseManager: DatabaseManager

    override val webapp by lazy { databaseManager.getWebapp(id) }
    override val originalUrl by lazy { webapp.url }
}
