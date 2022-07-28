package com.qhy040404.libraryonetap.utils.extensions

object MutableListExtensions {
    fun MutableList<Int>.sum(): Int {
        var sum = 0
        for (element in this) {
            sum += element
        }
        return sum
    }

    fun MutableList<Double>.sum(): Double {
        var sum = 0.0
        for (element in this) {
            sum += element
        }
        return sum
    }
}