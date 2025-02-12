package com.bachnn.messenger.ui.view.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.bachnn.messenger.R

class EmoticonGroupView(
    private val context: Context,
    private val attr: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : LinearLayout(context, attr, defStyleAttr) {


    private var backgroundGroupView: Int
    private var emoticonViewWidth: Int
    private var marginStartEnd: Int
    private var marginEmotionView: Int

    // todo selectEmoticonView.
    private var selectedEmoticonViewWidth: Int
    private var marginSelectedEmoticonView: Int


    // todo config part
    private var emotionViews : List<EmotionView>? = null


    init {
        attr.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.custom_emoticon_group)
            backgroundGroupView = typedArray.getInt(
                R.styleable.custom_emoticon_group_android_background,
                R.drawable.react_emoticon_background
            )
            emoticonViewWidth = dpToXp(
                typedArray.getDimension(
                    R.styleable.custom_emoticon_group_margin_emoticon_view,
                    40f
                )
            )
            marginStartEnd = dpToXp(
                typedArray.getDimension(
                    R.styleable.custom_emoticon_group_margin_start_end,
                    6f
                )
            )
            marginEmotionView = dpToXp(
                typedArray.getDimension(
                    R.styleable.custom_emoticon_group_margin_emoticon_view,
                    4f
                )
            )


            selectedEmoticonViewWidth = dpToXp(
                typedArray.getDimension(
                    R.styleable.custom_emoticon_group_selected_emoticon_view_width,
                    60f
                )
            )
            marginSelectedEmoticonView = dpToXp(
                typedArray.getDimension(
                    R.styleable.custom_emoticon_group_margin_selected_emoticon_view,
                    8f
                )
            )
        }
    }


    private fun dpToXp(dp: Float): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    //todo animation scale up and down.

}