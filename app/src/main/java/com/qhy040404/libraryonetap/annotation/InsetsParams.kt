package com.qhy040404.libraryonetap.annotation

import androidx.annotation.IntDef

@IntDef(InsetsParams.LEFT, InsetsParams.RIGHT, InsetsParams.TOP, InsetsParams.BOTTOM)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class InsetsParams {
    companion object {
        const val LEFT = 0
        const val RIGHT = 1
        const val TOP = 2
        const val BOTTOM = 3
    }
}
