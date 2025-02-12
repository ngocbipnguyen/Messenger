package com.bachnn.messenger.ui.view.custom

import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import com.bachnn.messenger.ui.view.IntervalConverter

data class EmoticonWeightAnimation(
    val view: EmotionView, val index: Int,
    val initialWeight: Float, val targetWeight: Float,
    val step: Float,
    val shouldSelect: Boolean,
    val emoticonSize: Int,
    val emoticonGroupView: EmoticonGroupView
): Animation() {
    private var currentWeight: Float = initialWeight

    fun animate() {
        applyTransformation(0f, null)
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        currentWeight += step
        if (if (step > 0) currentWeight < targetWeight else currentWeight > targetWeight) {
            val height =
                if (shouldSelect) emoticonGroupView.selectedEmoticonViewWidth else emoticonGroupView.emoticonViewWidth
            val weight = currentWeight
            val params = LinearLayout.LayoutParams(0, height, weight)
            val marginView: Int =
                if (shouldSelect) emoticonGroupView.marginSelectedEmoticonView else emoticonGroupView.marginEmotionView
            when (index) {
                0 -> params.setMargins(
                    emoticonGroupView.marginStartEnd + marginView,
                    marginView,
                    marginView,
                    marginView
                )
                emoticonSize - 1 -> params.setMargins(
                    marginView,
                    marginView,
                    marginView + emoticonGroupView.marginStartEnd,
                    marginView
                )
                else -> params.setMargins(marginView, marginView, marginView, marginView)
            }
            view.layoutParams = params
//            if (shouldSelect && step > 0) {
//                val normalizedCurrentWeight: Float = IntervalConverter
//                    .convertNumber(currentWeight)
//                    .fromInterval(0f, targetWeight)
//                    .toInterval(initialWeight, targetWeight)
//                val animationPercent = normalizedCurrentWeight / targetWeight
////                view.onWeightAnimated(animationPercent)
//            } //else view.onWeightAnimated(0f)
        } else {
            cancel()
        }
    }

    override fun willChangeBounds(): Boolean {
        return true
    }

}