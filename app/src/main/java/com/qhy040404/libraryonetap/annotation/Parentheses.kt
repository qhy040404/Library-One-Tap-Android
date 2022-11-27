package com.qhy040404.libraryonetap.annotation

import androidx.annotation.IntDef

@IntDef(Parentheses.SMALL, Parentheses.MEDIUM, Parentheses.LARGE)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Parentheses {
    companion object {
        const val SMALL = 0
        const val MEDIUM = 1
        const val LARGE = 2
    }
}
