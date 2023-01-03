package com.qhy040404.libraryonetap.utils.tools

import com.qhy040404.libraryonetap.data.tools.Grade
import com.qhy040404.libraryonetap.utils.extensions.DoubleExtensions.to2Decimal

object GPAUtils {
    fun calculateGPAByDlut(grades: List<Grade>): Double {
        var totalWeightedGP = 0.0
        val totalCredits = grades.sumOf { it.credit }
        grades.forEach {
            totalWeightedGP += it.gp * it.credit
        }
        return (totalWeightedGP / totalCredits).to2Decimal()
    }

    fun calculateGPAByStandard5(grades: List<Grade>): Double {
        var totalWeightedGP = 0.0
        var totalCredits = grades.sumOf { it.credit }
        grades.forEach {
            runCatching {
                totalWeightedGP += getGpByStandard5(it.grade.toInt()) * it.credit
            }.onFailure { _ ->
                totalCredits -= it.credit
            }
        }
        return (totalWeightedGP / totalCredits).to2Decimal()
    }

    fun calculateGPAByStandard4(grades: List<Grade>): Double {
        var totalWeightedGP = 0.0
        var totalCredits = grades.sumOf { it.credit }
        grades.forEach {
            runCatching {
                totalWeightedGP += getGpByStandard4(it.grade.toInt()) * it.credit
            }.onFailure { _ ->
                totalCredits -= it.credit
            }
        }
        return (totalWeightedGP / totalCredits).to2Decimal()
    }

    fun calculateGPAByPeking4(grades: List<Grade>): Double {
        var totalWeightedGP = 0.0
        var totalCredits = grades.sumOf { it.credit }
        grades.forEach {
            runCatching {
                totalWeightedGP += getGpByPeking4(it.grade.toInt()) * it.credit
            }.onFailure { _ ->
                totalCredits -= it.credit
            }
        }
        return (totalWeightedGP / totalCredits).to2Decimal()
    }

    private fun getGpByStandard5(score: Int): Double {
        return when (score) {
            in 95..100 -> 5.0
            in 90 until 95 -> 4.5
            in 85 until 90 -> 4.0
            in 80 until 85 -> 3.5
            in 75 until 80 -> 3.0
            in 70 until 75 -> 2.5
            in 65 until 70 -> 2.0
            in 60 until 65 -> 1.0
            else -> 0.0
        }
    }

    private fun getGpByStandard4(score: Int): Double {
        return when (score) {
            in 90..100 -> 4.0
            in 80 until 90 -> 3.0
            in 70 until 80 -> 2.0
            in 60 until 70 -> 1.0
            else -> 0.0
        }
    }

    private fun getGpByPeking4(score: Int): Double {
        return when (score) {
            in 90..100 -> 4.0
            in 85 until 90 -> 3.7
            in 82 until 85 -> 3.3
            in 78 until 82 -> 3.0
            in 75 until 78 -> 2.7
            in 72 until 75 -> 2.3
            in 68 until 72 -> 2.0
            in 64 until 68 -> 1.5
            in 60 until 64 -> 1.0
            else -> 0.0
        }
    }
}
