package com.qhy040404.libraryonetap.utils.lazy

import kotlin.reflect.KProperty

class ResettableLazy<PROPTYPE>(
    private val manager: ResettableLazyManager,
    val init: () -> PROPTYPE,
) : Resettable {
    @Volatile
    var lazyHolder = makeInitBlock()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): PROPTYPE {
        return lazyHolder.value
    }

    override fun reset() {
        lazyHolder = makeInitBlock()
    }

    private fun makeInitBlock(): Lazy<PROPTYPE> {
        return lazy {
            manager.register(this)
            init()
        }
    }
}