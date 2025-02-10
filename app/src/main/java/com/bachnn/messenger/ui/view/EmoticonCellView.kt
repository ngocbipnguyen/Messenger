package com.bachnn.messenger.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import com.bachnn.messenger.R

class EmoticonCellView constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {

    }

    fun getLayoutId(): Int {
        return R.layout.emoticon_cell_with_image
    }

    fun setEmoticon(emoticon: Emoticon) {
        val imageView: ImageView = findViewById(R.id.imageView)
        imageView.setImageResource(emoticon.drawable)
    }


    fun onWeightAnimated(animationPercent: Float) {

    }

}