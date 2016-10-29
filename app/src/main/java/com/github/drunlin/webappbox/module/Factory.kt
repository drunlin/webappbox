package com.github.drunlin.webappbox.module

import java.lang.ref.WeakReference
import java.util.*

class Factory<in K, V> {
    private val map = HashMap<K, WeakReference<V>>()

    fun get(key: K, generator: () -> V): V {
        map.filter { it.value.isEnqueued }.forEach { map.remove(it.key) }
        return map[key]?.get() ?: generator().apply { map[key] = WeakReference(this) }
    }
}
