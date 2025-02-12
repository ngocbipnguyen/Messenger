package com.bachnn.messenger.ui.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import com.bachnn.messenger.R
import com.bachnn.messenger.ui.view.Emoticon


/*
* Animation when select there are two way.
* 1. one run AnimationSet.
* 2. update size after run animation.
* */
class EmotionView constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        // todo add file xml in to frameLayout.
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.emoticon_cell_with_image, this, true)
    }

    // set value in imageView
    fun setEmoticon(emoticon: Emoticon) {
        val emoticonIcon: ImageView = findViewById(R.id.imageView)
        emoticonIcon.setImageResource(emoticon.drawable)
    }

}