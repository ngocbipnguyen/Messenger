package com.bachnn.messenger.ui.view

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import java.util.LinkedList

class EmoticonLikeView constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var emoticonBackgroundView: ImageView? = null

    private var emoticonCellContainer: LinearLayout? = null

    private lateinit var emoticonCellViews: MutableList<EmoticonCellView>

    private var selectedEmoticon: Int = 0

    private lateinit var emoticonConfig: EmoticonConfig

    init {
        this.setBackgroundColor(Color.TRANSPARENT)
    }

    public fun configure(config: EmoticonConfig) {
        this.emoticonConfig = config
        emoticonCellViews = LinkedList()
        selectedEmoticon = -1


        //set background.
        if (config.backgroundImageResource != 0) {
            emoticonBackgroundView = ImageView(context)

            emoticonBackgroundView?.setImageResource(config.backgroundImageResource)
            emoticonBackgroundView?.scaleType = ImageView.ScaleType.FIT_XY

            // set width and height of view.
            val params: RelativeLayout.LayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, config.backgroundViewHeight)
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            params.setMargins(config.emojiViewMarginLeft, 0, config.emojiViewMarginRight, config.backgroundViewMarginBottom)
            emoticonBackgroundView?.layoutParams = params

            emoticonBackgroundView?.visibility = View.INVISIBLE
            this.addView(emoticonBackgroundView)
        }

        emoticonCellContainer = LinearLayout(context)

        val params: RelativeLayout.LayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, config.emojiImagesContainerHeight)
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        emoticonCellContainer?.layoutParams = params
        emoticonCellContainer?.visibility = View.INVISIBLE
        this.addView(emoticonCellContainer)


        config.emojis?.forEachIndexed { index, emoticon ->
            val cellView: EmoticonCellView = config.cellViewFactory?.newInstance(context)!!
            cellView.layoutParams = getDefaultLayoutParams(index)
            cellView.onWeightAnimated(0f)
            cellView.setEmoticon(emoticon)
            this.emoticonCellContainer?.addView(cellView)
            this.emoticonCellViews.add(cellView)
        }

        this.bringToFront()

    }


    private fun getDefaultLayoutParams(index: Int): LinearLayout.LayoutParams {
        val height: Int = LinearLayout.LayoutParams.MATCH_PARENT
        val weight: Float = emoticonConfig.unselectedEmojiWeight.toFloat()
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(0, height, weight)
        val leftMargin = emoticonConfig.unselectedEmojiMarginLeft
        val topMargin = emoticonConfig.unselectedEmojiMarginTop
        val rightMargin = emoticonConfig.unselectedEmojiMarginRight
        val bottomMargin = emoticonConfig.unselectedEmojiMarginBottom
        if (index == 0) {
            params.setMargins(
                emoticonConfig.emojiViewMarginLeft + leftMargin,
                topMargin,
                rightMargin,
                bottomMargin
            )
        } else if (index == (emoticonConfig.emojis?.size?.minus(1))) {
            params.setMargins(
                leftMargin,
                topMargin,
                rightMargin,
                bottomMargin + emoticonConfig.emojiViewMarginRight
            )
        } else {
            params.setMargins(
                leftMargin,
                topMargin,
                rightMargin,
                bottomMargin
            )
        }
        return params
    }


    fun show() {
        if (this.emoticonCellContainer?.visibility == View.VISIBLE)  {
            return
        }

        if (emoticonConfig.emojiViewOpenedAnimation != null) {
            this.startAnimation(emoticonConfig.emojiViewOpenedAnimation)
        }

        if (this.emoticonBackgroundView != null) {
            this.emoticonBackgroundView?.visibility = View.VISIBLE
        }

        this.emoticonCellContainer?.visibility = View.VISIBLE

    }


    fun hide() {
        if (this.emoticonCellContainer?.visibility != View.VISIBLE) {
            return
        }
        if (emoticonConfig.emojiViewClosedAnimation != null) {
            this.startAnimation(emoticonConfig.emojiViewClosedAnimation)
            emoticonConfig.emojiViewClosedAnimation!!.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                }

                override fun onAnimationRepeat(animation: Animation?) {
                    if (emoticonBackgroundView != null) {
                      emoticonBackgroundView?.visibility = View.INVISIBLE
                      emoticonCellContainer?.visibility = View.INVISIBLE
                    }
                    emoticonCellViews.forEachIndexed { index, emoticonCellView ->
                        emoticonCellView.layoutParams = getDefaultLayoutParams(index)
                        emoticonCellView.onWeightAnimated(0f)
                    }
                }
            })
        } else {
            if (this.emoticonBackgroundView != null) {
                this.emoticonBackgroundView?.visibility = View.INVISIBLE
            }
            this.emoticonCellContainer?.visibility = View.INVISIBLE
        }
    }

    private fun isShowed(): Boolean {
        return this.emoticonCellContainer?.visibility == VISIBLE
    }


    fun onTouchUp(x: Float, y: Float) {
        if (this.isShowed()) {
            if (emoticonConfig.onEmoticonSelectedListener != null) {
                if (selectedEmoticon >= 0 && selectedEmoticon < this.emoticonCellViews.size) {
                    if (x.toInt() != -1 && y.toInt() != -1) {
                        if (selectedEmoticon != -1) {
                            emoticonConfig.onEmoticonSelectedListener!!.onEmoticonSelected(
                                this.emoticonConfig.emojis?.get(
                                    selectedEmoticon
                                )!!
                            )
                        }
                    }
                }
            }
        }
    }


    fun onTouchMove(x: Float, y: Float) {
        val maxX : Int = width.toInt()
        val minX : Int = x.toInt()

        var index : Int = (((x-minX)/ maxX)* emoticonConfig.emojis?.size!!.toInt()).toInt()

        if(x < minX || x> maxX + minX) {
            emoticonConfig.emojis?.forEachIndexed { index, emoticon ->
                setUnselectedEmoticon(index)
            }
            selectedEmoticon = -1
        } else {
            if (index < 0) {
                index =0
            }
            if (index > emoticonConfig.emojis?.size!!) {
                index = emoticonConfig.emojis?.size!!
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
        for (i in selectedIndex + 1 until emoticonConfig.emojis?.size!!)
            setUnselectedEmoticon(i)

        setSelectedEmoticon(selectedIndex)

    }

    private fun setSelectedEmoticon(index: Int) {
        if (index >= 0 && index < emoticonCellViews.size) {
            selectedEmoticon = index
            val view: EmoticonCellView = emoticonCellViews[selectedEmoticon]
            val weight = getWeight(view)
            growView(view, index, weight, 4f, emoticonConfig.emojiAnimationSpeed, true)
        }
    }


    private fun setUnselectedEmoticon(index: Int) {
        if (index >= 0 && index < emoticonCellViews.size) {
            val view: EmoticonCellView = emoticonCellViews[index]
            val weight = getWeight(view)
            growView(view, index, weight, 1f, -emoticonConfig.emojiAnimationSpeed, false)
        }
    }


    private fun getWeight(view: View): Float {
        val param: LinearLayout.LayoutParams = view.layoutParams as LinearLayout.LayoutParams
        return param.weight
    }


    private fun growView(
        view: EmoticonCellView,
        index: Int,
        initWeight: Float,
        maxWeight: Float,
        step: Float,
        shouldSelect: Boolean
    ) {
        val a: Animation =
            ViewWeightAnimation(view, index, initWeight, maxWeight, step, shouldSelect, emoticonConfig)
        view.startAnimation(a)
    }

}