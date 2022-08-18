package com.qhy040404.libraryonetap.utils.lazy

import kotlin.reflect.KProperty

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
