package com.qhy040404.libraryonetap.utils.extensions

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.QRUtils

@Suppress("unused")
object ViewExtensions {
    /**
     * Get dp length according to density
     */
    val Number.dp: Int get() = (toInt() * Resources.getSystem().displayMetrics.density).toInt()

    fun ViewPager2.setCurrentItem(
        item: Int,
        duration: Long,
        interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
        pagePxWidth: Int = width,
    ) {
        val pxToDrag: Int = pagePxWidth * (item - currentItem)
        val animator = ValueAnimator.ofInt(0, pxToDrag)
        var previousValue = 0
        animator.addUpdateListener { valueAnimator ->
            val currentValue = valueAnimator.animatedValue as Int
            val currentPxToDrag = (currentValue - previousValue).toFloat()
            fakeDragBy(-currentPxToDrag)
            previousValue = currentValue
        }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                beginFakeDrag()
            }

            override fun onAnimationEnd(animation: Animator) {
                endFakeDrag()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        animator.interpolator = interpolator
        animator.duration = duration
        animator.start()
    }

    /**
     * Auto detect night mode to generate a nicer QR code for ImageView
     *
     * @param ctx Context
     * @param origByteArray Original QR code byte array
     */
    fun ImageView.mLoad(ctx: Context, origByteArray: ByteArray) {
        val qrCode = BitmapFactory.decodeByteArray(origByteArray, 0, origByteArray.size)
        load(
            if (AppUtils.currentIsNightMode(ctx)) {
                QRUtils.createWhiteBorderBitmap(qrCode, 2)
            } else {
                qrCode
            }
        )
    }
}
