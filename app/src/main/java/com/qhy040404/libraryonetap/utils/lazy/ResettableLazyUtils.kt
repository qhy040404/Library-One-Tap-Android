package com.qhy040404.libraryonetap.utils.lazy

object ResettableLazyUtils {
    fun <PROPTYPE> resettableLazy(
        manager: ResettableLazyManager,
        init: () -> PROPTYPE,
    ): ResettableLazy<PROPTYPE> {
        return ResettableLazy(manager, init)
    }

    fun resettableManager(): ResettableLazyManager = ResettableLazyManager()
}