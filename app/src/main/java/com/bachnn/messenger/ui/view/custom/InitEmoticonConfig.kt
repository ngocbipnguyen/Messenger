package com.bachnn.messenger.ui.view.custom

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.bachnn.messenger.R
import java.util.LinkedList

class InitEmoticonConfig(val context: Context) {

//    var target: IActivityWithEmoji? = null

    var triggerView: View? = null

    var emoticons: MutableList<Emoticon>? = null

    val emoticonViewOpenedAnimation: Animation
    val emoticonViewClosedAnimation: Animation

    var onEmoticonSelectedListener: OnEmoticonSelectedListener? = null
    var emoticonGroupView: EmoticonGroupView? = null

    var emojiAnimationSpeed: Float = 0f

//    var cellViewFactory: IEmoticonCellViewFactory? = null

    init {

        emoticonViewOpenedAnimation =
            AnimationUtils.loadAnimation(context, R.anim.emoticon_default_in_animation)
        emoticonViewClosedAnimation =
            AnimationUtils.loadAnimation(context, R.anim.emoticon_default_out_animation)
        emojiAnimationSpeed = 0.2f
//        cellViewFactory = IEmoticonCellViewFactory { context -> EmoticonCellView(context) }

    }

    companion object {
        fun with(context: Context): InitEmoticonConfig {
            return InitEmoticonConfig(context)
        }
    }



    fun on(triggerView: View): InitEmoticonConfig {
        this.triggerView = triggerView
        return this
    }

    fun setEmoticons(emoticons: List<Emoticon>): InitEmoticonConfig {
        if (this.emoticons == null) {
            this.emoticons = emoticons.toMutableList()
        }
        return this
    }

    fun addEmoticon(emoticon: Emoticon): InitEmoticonConfig {
        if (this.emoticons == null) {
            this.emoticons = LinkedList()
        }
        this.emoticons?.add(emoticon)
        return this
    }


    fun setOnEmojiSelectedListener(emoticonSelectedListener: OnEmoticonSelectedListener): InitEmoticonConfig {
        this.onEmoticonSelectedListener = emoticonSelectedListener
        return this
    }



    fun open(emoticonLikeView: EmoticonGroupView): InitEmoticonConfig {
        this.emoticonGroupView = emoticonLikeView
        return this
    }


    fun setup() {
      if (triggerView == null)
            throw EmoticonException("Trigger view not set. Do it with EmojiConfig.with(target).on(triggerView)")
        else if (emoticons == null)
            throw EmoticonException("Emojis not set")
        else if (emoticons!!.size <= 1)
            throw EmoticonException("Please add more emojis")
        else if (emoticonGroupView == null)
            throw EmoticonException("EmoticonLikeView not set. Use open method.")
        else {
//            target.configureEmojiLike(this)
            emoticonGroupView?.configure(this)
        }
    }


}