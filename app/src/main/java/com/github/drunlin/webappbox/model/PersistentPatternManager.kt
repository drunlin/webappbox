package com.github.drunlin.webappbox.model

import com.github.drunlin.webappbox.common.asyncCall
import com.github.drunlin.webappbox.common.runOnIoThread
import com.github.drunlin.webappbox.data.URLPattern
import javax.inject.Inject

class PersistentPatternManager(private val id: Long) : PatternManager() {
    @Inject lateinit var databaseManager: DatabaseManager

    override val data by lazy { databaseManager.getPatterns(id) }

    override fun insert(value: URLPattern) {
        super.insert(value)

        asyncCall({ databaseManager.insert(id, value) }) { value.id = it }
    }

    override fun update(index: Int, value: URLPattern) {
        super.update(index, value)

        runOnIoThread { databaseManager.update(value) }
    }

    override fun remove(ids: Set<Long>) {
        super.remove(ids)

        runOnIoThread { databaseManager.deletePatterns(ids) }
    }
}
