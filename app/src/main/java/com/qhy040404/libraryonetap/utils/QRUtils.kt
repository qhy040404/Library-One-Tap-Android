package com.qhy040404.libraryonetap.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

object QRUtils {
    fun toGrayscale(bmpOriginal: Bitmap): Bitmap {
        val width = bmpOriginal.width
        val height = bmpOriginal.height
        var color: Int
        var r: Int
        var g: Int
        var b: Int
        var a: Int

        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val oldPx = IntArray(width * height)
        val newPx = IntArray(width * height)
        bmpOriginal.getPixels(oldPx, 0, width, 0, 0, width, height)

        for (j in 0 until width * height) {
            color = oldPx[j]
            r = Color.red(color)
            g = Color.green(color)
            b = Color.blue(color)
            a = Color.alpha(color)
            var gray = (r.toFloat() * 0.3 + g.toFloat() * 0.59 + b.toFloat() * 0.11).toInt()
            gray = if (gray < 128) {
                0
            } else {
                255
            }
            newPx[j] = Color.argb(a, gray, gray, gray)
        }

        bmp.setPixels(newPx, 0, width, 0, 0, width, height)
        return createWhiteBorderBitmap(bmp)
    }

    fun createWhiteBorderBitmap(bmp: Bitmap, width: Int = 10, radius: Float = 5F): Bitmap {
        val outBmp = Bitmap.createBitmap(
            bmp.width + width * 2,
            bmp.height + width * 2,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(outBmp)

        val rectF = RectF(0F, 0F, outBmp.width.toFloat(), outBmp.height.toFloat())

        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.WHITE

        canvas.drawRoundRect(rectF, radius, radius, paint)
        canvas.drawBitmap(bmp, width.toFloat(), width.toFloat(), paint)

        return outBmp
    }
}
