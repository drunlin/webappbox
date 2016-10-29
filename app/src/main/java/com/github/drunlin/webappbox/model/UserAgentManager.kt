package com.github.drunlin.webappbox.model

import com.github.drunlin.webappbox.common.asyncCall
import com.github.drunlin.webappbox.common.findIndexedValue
import com.github.drunlin.webappbox.common.generateId
import com.github.drunlin.webappbox.common.runOnIoThread
import com.github.drunlin.webappbox.data.UserAgent
import javax.inject.Inject

class UserAgentManager : DataManager<UserAgent>() {
    @Inject lateinit var databaseManager: DatabaseManager

    override val data by lazy { databaseManager.getUserAgents() }

    val userAgents by lazy { data }
    val defaultUserAgent by lazy { UserAgent(0, "", "") }

    fun insert(name: String, value: String) {
        insert(UserAgent(generateId(), name, value))
    }

    override fun insert(value: UserAgent) {
        super.insert(value)

        asyncCall({ databaseManager.insert(value) }) { value.id = it }
    }

    fun update(id: Long, name: String, value: String) {
        val (index, userAgent) = data.findIndexedValue { it.id == id }
        userAgent.name = name
        userAgent.value = value
        update(index, userAgent)
    }

    override fun update(index: Int, value: UserAgent) {
        super.update(index, value)

        runOnIoThread { databaseManager.update(value) }
    }

    override fun remove(ids: Set<Long>) {
        super.remove(ids)

        runOnIoThread { databaseManager.deleteUserAgents(ids) }
    }

    fun isNameExited(name: String) = data.any { it.name == name }

    fun isValueExited(value: String) = data.any { it.value == value }

    fun getUserAgent(id: Long) = data.find { it.id == id } ?: defaultUserAgent
}
