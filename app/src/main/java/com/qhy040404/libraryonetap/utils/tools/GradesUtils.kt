package com.qhy040404.libraryonetap.utils.tools

import android.content.Context
import com.absinthe.libraries.utils.extensions.getStringArray
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.annotation.Parentheses
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.utils.extensions.DoubleExtensions.to2Decimal
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.addParentheses

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

    fun calculateAverageGP(
        ctx: Context,
        gp: MutableList<Double>,
        scores: MutableList<String>,
        credits: MutableList<Double>,
    ): String {
        return when (GlobalValues.gpOption) {
            Constants.GP_DLUT -> {
                GPAUtils.calculateGPAByDlut(gp, credits)
                    .toString() + getCurrentGPAAlgorithm(ctx).addParentheses(Parentheses.SMALL)
            }
            Constants.GP_STANDARD5 -> {
                GPAUtils.calculateGPAByStandard5(scores, credits)
                    .toString() + getCurrentGPAAlgorithm(ctx).addParentheses(Parentheses.SMALL)
            }
            Constants.GP_STANDARD4 -> {
                GPAUtils.calculateGPAByStandard4(scores, credits)
                    .toString() + getCurrentGPAAlgorithm(ctx).addParentheses(Parentheses.SMALL)
            }
            Constants.GP_PEKING4 -> {
                GPAUtils.calculateGPAByPeking4(scores, credits)
                    .toString() + getCurrentGPAAlgorithm(ctx).addParentheses(Parentheses.SMALL)
            }
            else -> {
                GPAUtils.calculateGPAByDlut(gp, credits)
                    .toString() + getCurrentGPAAlgorithm(ctx).addParentheses(Parentheses.SMALL)
            }
        }
    }

    private fun getCurrentGPAAlgorithm(ctx: Context): String =
        ctx.getStringArray(R.array.gp)[ctx.getStringArray(R.array.gp_values)
            .indexOf(GlobalValues.gpOption)]
}
