package com.github.drunlin.webappbox.model

import com.github.drunlin.webappbox.common.Callback
import com.github.drunlin.webappbox.data.Unique
import java.util.*

abstract class DataManager<T : Unique> : ObservableModel() {
    val onRemove: Callback<() -> Unit> = Callback()

    open val data: MutableList<T> = LinkedList()

    open protected fun insert(value: T) {
        data.add(value)
        onInsert.invoke { it(data.lastIndex) }
    }

    open protected fun update(index: Int, value: T) {
        onUpdate.invoke { it(index) }
    }

    open fun remove(ids: Set<Long>) {
        data.removeAll { ids.contains(it.id) }
        onRemove.invoke { it() }
    }
}
