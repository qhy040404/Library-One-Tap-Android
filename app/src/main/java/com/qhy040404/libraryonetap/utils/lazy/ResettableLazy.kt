package com.qhy040404.libraryonetap.utils.lazy

import java.util.LinkedList
import kotlin.reflect.KProperty

fun <T> resettableLazy(
    manager: ResettableLazyManager,
    init: () -> T,
): ResettableLazy<T> = ResettableLazy(manager, init)

fun resettableManager(): ResettableLazyManager = ResettableLazyManager()

interface Resettable {
    fun reset()
}

class ResettableLazy<T>(
    private val manager: ResettableLazyManager,
    val init: () -> T,
) : Resettable {
    @Volatile
    var lazyHolder = makeInitBlock()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = lazyHolder.value

    override fun reset() {
        lazyHolder = makeInitBlock()
    }

    private fun makeInitBlock(): Lazy<T> {
        return lazy {
            manager.register(this)
            init()
        }
    }
}

class ResettableLazyManager {
    private val managedDelegates = LinkedList<Resettable>()

    fun register(managed: Resettable) {
        synchronized(managedDelegates) {
            managedDelegates.add(managed)
        }
    }

    fun reset() {
        synchronized(managedDelegates) {
            managedDelegates.forEach { it.reset() }
            managedDelegates.clear()
        }
    }
}
