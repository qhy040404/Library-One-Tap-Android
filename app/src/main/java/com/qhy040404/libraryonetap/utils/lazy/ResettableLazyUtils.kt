package com.qhy040404.libraryonetap.utils.lazy

object ResettableLazyUtils {
    fun <T> resettableLazy(
        manager: ResettableLazyManager,
        init: () -> T,
    ): ResettableLazy<T> = ResettableLazy(manager, init)

    fun resettableManager(): ResettableLazyManager = ResettableLazyManager()
}
