package com.qhy040404.libraryonetap.utils.tools

import com.qhy040404.libraryonetap.utils.extensions.DoubleExtensions.to2Decimal

object GradesUtils {
    fun calculateWeightedAverage(
        grades: MutableList<String>,
        credits: MutableList<Double>,
    ): Double {
        var totalWeightedSum = 0.0
        val totalCredits = credits.sum()
        for (i in 0 until grades.size) {
            totalWeightedSum += grades[i].toDouble() * credits[i]
        }
        return (totalWeightedSum / totalCredits).to2Decimal()
    }

    fun calculateAverageGP(gp: MutableList<Double>, credits: MutableList<Double>): Double {
        var totalWeightedGP = 0.0
        val totalCredits = credits.sum()
        for (i in 0 until gp.size) {
            totalWeightedGP += gp[i] * credits[i]
        }
        return (totalWeightedGP / totalCredits).to2Decimal()
    }
}
