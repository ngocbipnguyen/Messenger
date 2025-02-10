package com.bachnn.messenger.ui.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import java.util.LinkedList

class EmoticonLikeView constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private lateinit var emoticonBackgroundView: ImageView

    private lateinit var emoticonCellContainer: LinearLayout

    private lateinit var emoticonCellViews: List<EmoticonCellView>

    private var selectedEmoticon: Int = 0

    private lateinit var emoticonConfig: EmoticonConfig

    init {
        this.setBackgroundColor(Color.TRANSPARENT)
    }

    public fun configure(config: EmoticonConfig) {
        this.emoticonConfig = config
        emoticonCellViews = LinkedList()
        selectedEmoticon = -1



    }


}