package com.qhy040404.libraryonetap.utils.lazy

import kotlin.reflect.KProperty

class ResettableLazy<PROPTYPE>(val manager: ResettableLazyManager,val init: () -> PROPTYPE):Resettable {
    @Volatile var lazyHolder = makeInitBlock()

    operator fun getValue(thisRef: Any?, property:KProperty<*>):PROPTYPE {
        return lazyHolder.value
    }

    override fun reset() {
        lazyHolder=makeInitBlock()
    }

    fun makeInitBlock():Lazy<PROPTYPE> {
        return lazy {
            manager.register(this)
            init()
        }
    }
}