package com.qhy040404.libraryonetap.utils.tools

import android.content.Context
import com.absinthe.libraries.utils.extensions.getStringArray
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.annotation.Parentheses
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
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
        ctx: Context,
        grades: List<Grade>,
    ): String {
        return when (GlobalValues.gpOption) {
            Constants.GPA_DLUT -> {
                GPAUtils.calculateGPAByDlut(grades)
                    .toString() + getCurrentGPAAlgorithm(ctx).addParentheses(Parentheses.SMALL)
            }
            Constants.GPA_STANDARD5 -> {
                GPAUtils.calculateGPAByStandard5(grades)
                    .toString() + getCurrentGPAAlgorithm(ctx).addParentheses(Parentheses.SMALL)
            }
            Constants.GPA_STANDARD4 -> {
                GPAUtils.calculateGPAByStandard4(grades)
                    .toString() + getCurrentGPAAlgorithm(ctx).addParentheses(Parentheses.SMALL)
            }
            Constants.GPA_PEKING4 -> {
                GPAUtils.calculateGPAByPeking4(grades)
                    .toString() + getCurrentGPAAlgorithm(ctx).addParentheses(Parentheses.SMALL)
            }
            else -> {
                GPAUtils.calculateGPAByDlut(grades)
                    .toString() + getCurrentGPAAlgorithm(ctx).addParentheses(Parentheses.SMALL)
            }
        }
    }

    private fun getCurrentGPAAlgorithm(ctx: Context): String =
        ctx.getStringArray(R.array.gp)[ctx.getStringArray(R.array.gp_values)
            .indexOf(GlobalValues.gpOption)]
}
