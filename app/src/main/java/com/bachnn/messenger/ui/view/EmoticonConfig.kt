package com.bachnn.messenger.ui.view

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.bachnn.messenger.R
import java.util.LinkedList

/*
* xml:
* <EmoticonLikeView
* >
*
* kotlin
* EmoticonConfig to config arg (EmoticonCellView)
* create a ground view in ground view create number of cellView contain icon and text\
*
* handle touch and animation for cellView
* pass MotionEvent from EmoticonLikeTouchDetector -> EmoticonTriggerManager -> groundView handle show groundView and
*  calculate selectView and unselectView and scale Up and Down animation.
*
* */
class EmoticonConfig(val context: Context) {

    //??
    var triggerView: View? = null

    var touchDownDelay: Int = 0

    var touchUpDelay: Int = 0

    var emojiImagesContainerHeight: Int = 0

    var backgroundViewHeight: Int = 0

    //todo : width and height of emoticonCellView selected
    var selectedEmojiHeight: Int = 0

    var selectedEmojiWeight: Int = 0

    //todo: margin of this view

    var emojiViewMarginLeft: Int = 0

    var emojiViewMarginRight: Int = 0

    //todo:  margin of this view selected

    var selectedEmojiMarginBottom: Int = 0

    var selectedEmojiMarginTop: Int = 0

    var selectedEmojiMarginLeft: Int = 0

    var selectedEmojiMarginRight: Int = 0

    //todo: margin of this view unselected

    var unselectedEmojiMarginBottom: Int = 0

    var unselectedEmojiMarginTop: Int = 0

    var unselectedEmojiMarginLeft: Int = 0

    var unselectedEmojiMarginRight: Int = 0

    //todo: width of  this view unselected
    var unselectedEmojiWeight: Int = 0

    var emojiViewOpenedAnimation: Animation? = null

    var emojiViewClosedAnimation: Animation? = null

    // number of emoticonCellView
    var emojis: MutableList<Emoticon>? = null


    var onEmoticonSelectedListener: OnEmoticonSelectedListener? = null

    var emoticonLikeView: EmoticonLikeView? = null

    var backgroundImageResource: Int = 0

    var backgroundViewMarginBottom: Int = 0

    var emojiAnimationSpeed: Float = 0f

    var cellViewFactory: IEmoticonCellViewFactory? = null


    init {
        touchUpDelay = 500
        touchDownDelay = 100
        backgroundImageResource = 0
        selectedEmojiHeight = dpToPx(85)
        selectedEmojiWeight = 4
        unselectedEmojiWeight = 1
        emojiViewMarginLeft = dpToPx(15)
        emojiViewMarginRight = dpToPx(15)
        selectedEmojiMarginBottom = dpToPx(15)
        selectedEmojiMarginTop = 0
        selectedEmojiMarginLeft = dpToPx(15)
        selectedEmojiMarginRight = dpToPx(15)

        unselectedEmojiMarginBottom = 0
        unselectedEmojiMarginTop = dpToPx(30)
        unselectedEmojiMarginLeft = dpToPx(15)
        unselectedEmojiMarginRight = dpToPx(15)

        emojiImagesContainerHeight = dpToPx(130)
        backgroundViewHeight = dpToPx(50)
        backgroundViewMarginBottom = dpToPx(10)
        emojiAnimationSpeed = 0.2f


        emojiViewOpenedAnimation =
            AnimationUtils.loadAnimation(context, R.anim.emoticon_default_in_animation)
        emojiViewClosedAnimation =
            AnimationUtils.loadAnimation(context, R.anim.emoticon_default_out_animation)
        backgroundImageResource = R.drawable.emoticon_default_background_drawable


        cellViewFactory = IEmoticonCellViewFactory { context -> EmoticonCellView(context) }

    }


    private fun dpToPx(i: Int): Int {
        val resources: Resources = context.resources
        val px: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i.toFloat(), resources.displayMetrics)

        return px.toInt()
    }

    fun with(context: Context): EmoticonConfig {
        return EmoticonConfig(context)
    }

    fun on(triggerView: View): EmoticonConfig {
        this.triggerView = triggerView
        return this
    }

    fun setEmoticons(emoticons: List<Emoticon>): EmoticonConfig {
        if (this.emojis == null) {
            this.emojis = emoticons.toMutableList()
        }
        return this
    }

    fun addEmoticon(emoticon: Emoticon): EmoticonConfig {
        if (this.emojis == null) {
            this.emojis = LinkedList()
        }
        this.emojis?.add(emoticon)
        return this
    }


    fun setOnEmojiSelectedListener(emoticonSelectedListener: OnEmoticonSelectedListener): EmoticonConfig {
        this.onEmoticonSelectedListener = emoticonSelectedListener
        return this
    }

    fun setTouchDownDelay(touchDownDelay: Int): EmoticonConfig {
        this.touchDownDelay = touchDownDelay
        return this
    }

    fun setTouchUpDelay(touchUpDelay: Int): EmoticonConfig {
        this.touchUpDelay = touchUpDelay
        return this
    }


    fun setEmojiViewMarginLeft(emojiViewMarginLeft: Int): EmoticonConfig {
        this.emojiViewMarginLeft = emojiViewMarginLeft
        return this
    }

    fun setEmojiViewMarginRight(emojiViewMarginRight: Int): EmoticonConfig{
        this.emojiViewMarginRight = emojiViewMarginRight
        return this
    }

    fun setSelectedEmojiMarginBottom(selectedEmojiMarginBottom: Int): EmoticonConfig {
        this.selectedEmojiMarginBottom = selectedEmojiMarginBottom
        return this
    }

    fun setSelectedEmojiMarginTop(selectedEmojiMarginTop: Int): EmoticonConfig {
        this.selectedEmojiMarginTop = selectedEmojiMarginTop
        return this
    }

    fun setSelectedEmojiMarginLeft(selectedEmojiMarginLeft: Int): EmoticonConfig {
        this.selectedEmojiMarginLeft = selectedEmojiMarginLeft
        return this
    }

    fun setSelectedEmojiMarginRight(selectedEmojiMarginRight: Int): EmoticonConfig {
        this.selectedEmojiMarginRight = selectedEmojiMarginRight
        return this
    }

    fun setUnselectedEmojiMarginBottom(unselectedEmojiMarginBottom: Int): EmoticonConfig {
        this.unselectedEmojiMarginBottom = unselectedEmojiMarginBottom
        return this
    }

    fun setUnselectedEmojiMarginTop(unselectedEmojiMarginTop: Int): EmoticonConfig {
        this.unselectedEmojiMarginTop = unselectedEmojiMarginTop
        return this
    }

    fun setUnselectedEmojiMarginLeft(unselectedEmojiMarginLeft: Int): EmoticonConfig {
        this.unselectedEmojiMarginLeft = unselectedEmojiMarginLeft
        return this
    }

    fun setUnselectedEmojiMarginRight(unselectedEmojiMarginRight: Int): EmoticonConfig {
        this.unselectedEmojiMarginRight = unselectedEmojiMarginRight
        return this
    }

    fun setUnselectedEmojiWeight(unselectedEmojiWeight: Int): EmoticonConfig {
        this.unselectedEmojiWeight = unselectedEmojiWeight
        return this
    }

    fun setEmojiImagesContainerHeight(emojiImagesContainerHeight: Int): EmoticonConfig {
        this.emojiImagesContainerHeight = emojiImagesContainerHeight
        return this
    }

    fun setEmojiAnimationSpeed(emojiAnimationSpeed: Float): EmoticonConfig {
        this.emojiAnimationSpeed = emojiAnimationSpeed
        return this
    }

    fun setBackgroundViewHeight(backgroundViewHeight: Int): EmoticonConfig {
        this.backgroundViewHeight = backgroundViewHeight
        return this
    }

    fun setBackgroundImageResource(backgroundImageResource: Int): EmoticonConfig {
        this.backgroundImageResource = backgroundImageResource
        return this
    }

    fun setBackgroundViewMarginBottom(backgroundViewMarginBottom: Int): EmoticonConfig {
        this.backgroundViewMarginBottom = backgroundViewMarginBottom
        return this
    }

    fun setEmojiViewOpenedAnimation(emojiViewOpenedAnimation: Animation?): EmoticonConfig {
        this.emojiViewOpenedAnimation = emojiViewOpenedAnimation
        return this
    }

    fun setEmojiViewClosedAnimation(emojiViewClosedAnimation: Animation?): EmoticonConfig {
        this.emojiViewClosedAnimation = emojiViewClosedAnimation
        return this
    }

    fun setEmojiCellViewFactory(factory: IEmoticonCellViewFactory?): EmoticonConfig {
        this.cellViewFactory = factory
        return this
    }

    fun open(emoticonLikeView: EmoticonLikeView): EmoticonConfig {
        this.emoticonLikeView = emoticonLikeView
        return this
    }


    fun setup() {
        if (context == null)
            throw EmoticonException("Target not set. Set it with EmojiConfig.with(target)")
        else if (triggerView == null)
            throw EmoticonException("Trigger view not set. Do it with EmojiConfig.with(target).on(triggerView)")
        else if (emojis == null)
            throw EmoticonException("Emojis not set")
        else if (emojis!!.size <= 1)
            throw EmoticonException("Please add more emojis")
        else if (emoticonLikeView == null)
            throw EmoticonException("EmoticonLikeView not set. Use open method.")
        else {
            target.configureEmojiLike(this)
            emoticonLikeView?.configure(this)
        }
    }


}