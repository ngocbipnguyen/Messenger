package com.bachnn.messenger.ui.view.custom

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.widget.LinearLayout
import com.bachnn.messenger.R
import java.util.LinkedList

class EmoticonGroupView @JvmOverloads constructor(
    private val context: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attr, defStyleAttr) {


//    var backgroundGroupView: Int
    var emoticonViewWidth: Int
    var marginStartEnd: Int
    var marginEmotionView: Int

    // todo selectEmoticonView.
    var selectedEmoticonViewWidth: Int
    var marginSelectedEmoticonView: Int


    // todo config part
    private lateinit var emotionViews: MutableList<EmotionView>

    private var selectedEmotion: Int = -1

    private var emoticonConfig: InitEmoticonConfig? = null

//    private lateinit var emoticonConfig: EmoticonConfig

    init {
        attr.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.custom_emoticon_group)
//            backgroundGroupView = typedArray.getInt(
//                R.styleable.custom_emoticon_group_android_background,
//                R.drawable.react_emoticon_background
//            )
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

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
    }


    private fun dpToXp(dp: Float): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    fun configure(config: InitEmoticonConfig) {
        this.emoticonConfig = config
        emotionViews = LinkedList()
        selectedEmotion = -1

        config.emoticons?.forEachIndexed { index, emotion ->
            val emotionView = EmotionView(context)
            emotionView.layoutParams = getDefaultLayoutParams(index)
            emotionView.setEmoticon(emotion)
            this.addView(emotionView)
            this.emotionViews.add(emotionView)
        }

        this.bringToFront()
    }


    private fun getDefaultLayoutParams(index: Int): LinearLayout.LayoutParams {
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            emoticonViewWidth,
            emoticonViewWidth,
            EmoticonConstant.UNSELECTED_WEIGHT
        )
        when (index) {
            0 -> {
                params.setMargins(
                    marginStartEnd + marginEmotionView,
                    marginEmotionView,
                    marginEmotionView,
                    marginEmotionView
                )
            }

            (emoticonConfig?.emoticons?.size?.minus(1)) -> {
                params.setMargins(
                    marginEmotionView,
                    marginEmotionView,
                    marginEmotionView,
                    marginEmotionView + marginStartEnd
                )
            }

            else -> {
                params.setMargins(
                    marginEmotionView,
                    marginEmotionView,
                    marginEmotionView,
                    marginEmotionView
                )
            }
        }
        return params
    }

    private fun isShowed(): Boolean {
        return this.visibility == VISIBLE
    }

    fun onTouchUp(x: Float, y: Float) {
        if (this.isShowed()) {
            if (emoticonConfig?.onEmoticonSelectedListener != null) {
                if (selectedEmotion >= 0 && selectedEmotion < this.emotionViews.size) {
                    if (x.toInt() != -1 && y.toInt() != -1) {
                        if (selectedEmotion != -1) {
                            emoticonConfig?.onEmoticonSelectedListener!!.onEmoticonSelected(
                                this.emoticonConfig?.emoticons?.get(
                                    selectedEmotion
                                )!!
                            )
                        }
                    }
                }
            }
        }
    }

    fun onTouchMove(x: Float, y: Float) {
        val maxX: Int = width
        val minX: Int = x.toInt()

        var index: Int = (((x - minX) / maxX) * emoticonConfig?.emoticons?.size!!.toInt()).toInt()

        if (x < minX || x > maxX + minX) {
            emoticonConfig?.emoticons?.forEachIndexed { index, emoticon ->
                setUnselectedEmoticon(index)
            }
            selectedEmotion = -1
        } else {
            if (index < 0) {
                index = 0
            }
            if (index > emoticonConfig?.emoticons?.size!!.minus(1)) {
                index = emoticonConfig?.emoticons?.size!!
            }
            setSelectedLikeOnIndex(index)
        }
    }

    fun onTouchDown(x: Float, y: Float) {
        onTouchMove(x, y)

        Handler().postDelayed({
            onTouchMove(x, y)
            Handler().postDelayed({ onTouchMove(x, y) }, 50)
        }, 50)
    }

    private fun setSelectedLikeOnIndex(selectedIndex: Int) {
        for (i in 0 until selectedIndex)
            setUnselectedEmoticon(i)
        for (i in selectedIndex + 1 until emoticonConfig?.emoticons?.size!!)
            setUnselectedEmoticon(i)

        setSelectedEmoticon(selectedIndex)

    }

    private fun setSelectedEmoticon(index: Int) {
        if (index >= 0 && index < emotionViews.size) {
            selectedEmotion = index
            val view: EmotionView = emotionViews[selectedEmotion]
            val weight = getWeight(view)
            growView(view, index, weight, EmoticonConstant.SELECTED_WEIGHT, emoticonConfig?.emojiAnimationSpeed!!, true)
        }
    }

    private fun setUnselectedEmoticon(index: Int) {
        if (index >= 0 && index < emotionViews.size) {
            val view: EmotionView = emotionViews[index]
            val weight = getWeight(view)
            growView(view, index, weight, EmoticonConstant.UNSELECTED_WEIGHT, -emoticonConfig?.emojiAnimationSpeed!!, false)
        }
    }

    private fun getWeight(view: View): Float {
        val param: LinearLayout.LayoutParams = view.layoutParams as LinearLayout.LayoutParams
        return param.weight
    }

    private fun growView(
        view: EmotionView,
        index: Int,
        initWeight: Float,
        maxWeight: Float,
        step: Float,
        shouldSelect: Boolean
    ) {
        val a: Animation =
            EmoticonWeightAnimation(
                view, index, initWeight, maxWeight, step, shouldSelect,
                emoticonConfig?.emoticons?.size!!, this
            )
        view.startAnimation(a)
    }

    //todo animation scale up and down.

}