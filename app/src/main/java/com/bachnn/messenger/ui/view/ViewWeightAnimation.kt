package com.bachnn.messenger.ui.view

import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout

data class ViewWeightAnimation(
    val view: EmoticonCellView, val index: Int,
    val initialWeight: Float, val targetWeight: Float,
    val step: Float,
    val shouldSelect: Boolean,
    val emoticonConfig: EmoticonConfig
): Animation() {
    private var currentWeight: Float = initialWeight

    fun animate() {
        applyTransformation(0f, null)
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        currentWeight += step
        if (if (step > 0) currentWeight < targetWeight else currentWeight > targetWeight) {
            val height =
                if (shouldSelect) emoticonConfig.selectedEmojiHeight else LinearLayout.LayoutParams.MATCH_PARENT
            val weight = currentWeight
            val params = LinearLayout.LayoutParams(0, height, weight)
            val left: Int =
                if (shouldSelect) emoticonConfig.selectedEmojiMarginLeft else emoticonConfig.unselectedEmojiMarginLeft
            val top: Int =
                if (shouldSelect) emoticonConfig.selectedEmojiMarginTop else emoticonConfig.unselectedEmojiMarginTop
            val bottom: Int =
                if (shouldSelect) emoticonConfig.selectedEmojiMarginBottom else emoticonConfig.unselectedEmojiMarginBottom
            val right: Int =
                if (shouldSelect) emoticonConfig.selectedEmojiMarginRight else emoticonConfig.unselectedEmojiMarginRight
            when (index) {
                0 -> params.setMargins(
                    emoticonConfig.emojiViewMarginLeft + left,
                    top,
                    right,
                    bottom
                )
                emoticonConfig.emojis?.size!! - 1 -> params.setMargins(
                    left,
                    top,
                    right + emoticonConfig.emojiViewMarginRight,
                    bottom
                )
                else -> params.setMargins(left, top, right, bottom)
            }
            view.layoutParams = params
            if (shouldSelect && step > 0) {
                val normalizedCurrentWeight: Float = IntervalConverter
                    .convertNumber(currentWeight)
                    .fromInterval(0f, targetWeight)
                    .toInterval(initialWeight, targetWeight)
                val animationPercent = normalizedCurrentWeight / targetWeight
                view.onWeightAnimated(animationPercent)
            } else view.onWeightAnimated(0f)
        } else {
            cancel()
        }
    }


    override fun willChangeBounds(): Boolean {
        return true
    }

}

