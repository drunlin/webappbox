package com.github.drunlin.webappbox.common

import java.util.*

class Callback<T: Function<*>> {
    private val functions: HashMap<Any, HashSet<T>> = HashMap()

    fun add(tag: Any, block: T) {
        val set = functions[tag] ?: HashSet<T>().apply { functions[tag] = this }
        set.add(block)
    }

    fun remove(tag: Any) {
        functions.remove(tag)
    }

    fun invoke(block: (T) -> Unit) {
        functions.forEach { it.value.forEach { block(it) } }
    }
}
