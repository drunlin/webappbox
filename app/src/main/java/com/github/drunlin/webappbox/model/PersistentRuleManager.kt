package com.github.drunlin.webappbox.model

import com.github.drunlin.webappbox.common.asyncCall
import com.github.drunlin.webappbox.common.runOnIoThread
import com.github.drunlin.webappbox.data.Rule
import javax.inject.Inject

class PersistentRuleManager(private val id: Long) : RuleManager() {
    @Inject lateinit var databaseManager: DatabaseManager

    override val data by lazy { databaseManager.getRules(id) }

    override fun insert(value: Rule) {
        super.insert(value)

        asyncCall({ databaseManager.insert(id, value) }) { value.id = it }
    }

    override fun remove(ids: Set<Long>) {
        super.remove(ids)

        runOnIoThread { databaseManager.deleteRules(ids) }
    }

    override fun swap(from: Int, to: Int) {
        super.swap(from, to)

        runOnIoThread { databaseManager.swapRules(rules[from].id, rules[to].id) }
    }

    override fun update(index: Int, value: Rule) {
        super.update(index, value)

        runOnIoThread { databaseManager.update(value) }
    }
}
