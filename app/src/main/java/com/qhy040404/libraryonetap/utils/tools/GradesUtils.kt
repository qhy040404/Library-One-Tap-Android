package com.qhy040404.libraryonetap.utils.tools

import android.content.Context
import com.absinthe.libraries.utils.extensions.getStringArray
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.enums.GPAAlgorithm
import com.qhy040404.libraryonetap.constant.enums.Parentheses
import com.qhy040404.libraryonetap.data.tools.Grade
import com.qhy040404.libraryonetap.utils.extensions.DoubleExtensions.to2Decimal
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.addParentheses

object GradesUtils {
    fun calculateWeightedAverage(
        grades: List<Grade>,
    ): Double {
        var totalWeightedSum = 0.0
        var totalCredits = grades.sumOf { it.credit }
        grades.forEach {
            runCatching {
                totalWeightedSum += it.grade.toDouble() * it.credit
            }.onFailure { _ ->
                totalCredits -= it.credit
            }
        }
        return (totalWeightedSum / totalCredits).to2Decimal()
    }

    fun calculateAverageGP(
        context: Context,
        grades: List<Grade>,
    ): String {
        return when (GlobalValues.gpOption) {
            Constants.GPA_DLUT -> {
                GPAUtils.calculateGPA(grades, GPAAlgorithm.DLUT)
                    .toString() + getCurrentGPAAlgorithm(context).addParentheses(Parentheses.SMALL)
            }
            Constants.GPA_STANDARD5 -> {
                GPAUtils.calculateGPA(grades, GPAAlgorithm.STD5)
                    .toString() + getCurrentGPAAlgorithm(context).addParentheses(Parentheses.SMALL)
            }
            Constants.GPA_STANDARD4 -> {
                GPAUtils.calculateGPA(grades, GPAAlgorithm.STD4)
                    .toString() + getCurrentGPAAlgorithm(context).addParentheses(Parentheses.SMALL)
            }
            Constants.GPA_PEKING4 -> {
                GPAUtils.calculateGPA(grades, GPAAlgorithm.PK4)
                    .toString() + getCurrentGPAAlgorithm(context).addParentheses(Parentheses.SMALL)
            }
            else -> {
                GPAUtils.calculateGPA(grades, GPAAlgorithm.DLUT)
                    .toString() + getCurrentGPAAlgorithm(context).addParentheses(Parentheses.SMALL)
            }
        }
    }

    private fun getCurrentGPAAlgorithm(context: Context): String =
        context.getStringArray(R.array.gp)[context.getStringArray(R.array.gp_values)
            .indexOf(GlobalValues.gpOption)]
}
