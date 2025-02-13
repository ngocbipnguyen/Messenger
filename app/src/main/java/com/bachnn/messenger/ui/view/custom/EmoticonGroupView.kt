package com.bachnn.messenger.ui.view.custom

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
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
    private var previousSelectedEmotion = -1

    private var emoticonConfig: InitEmoticonConfig? = null

    private var endTouch: Int = -1

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
            val padding = dpToXp(4f)
            emotionView.setPadding(padding,padding,padding,padding)
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
                    marginEmotionView + marginStartEnd,
                    marginEmotionView
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
                            setUnselectedEmoticon(selectedEmotion)
                            emoticonConfig?.onEmoticonSelectedListener!!.onEmoticonSelected(
                                this.emoticonConfig?.emoticons?.get(
                                    selectedEmotion
                                )!!
                            )
                            selectedEmotion = -1
                        }
                    }
                }
            }
        }
    }

    fun onTouchMove(x: Float, y: Float) {
        val maxX: Int = width
        val minX: Int = x.toInt()

        var index: Int = ((x / maxX.toFloat()) * emoticonConfig?.emoticons?.size!!).toInt()

        if (x < 0 || x > maxX) {
//            emoticonConfig?.emoticons?.forEachIndexed { index, emoticon ->
//                setUnselectedEmoticon(index)
//            }
//            selectedEmotion = -1
        } else {
            if (index < 0) {
                index = 0
            }
            if (index > emoticonConfig?.emoticons?.size!!.minus(1)) {
                index = emoticonConfig?.emoticons?.size!!
            }

            if (index != selectedEmotion) {
                previousSelectedEmotion = selectedEmotion
                setSelectedLikeOnIndex(index)
            }
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
        if (previousSelectedEmotion != -1) {
            setUnselectedEmoticon(previousSelectedEmotion)
        }
        setSelectedEmoticon(selectedIndex)

    }

    private fun setSelectedEmoticon(index: Int) {
        if (index >= 0 && index < emotionViews.size) {
            selectedEmotion = index
            val view: EmotionView = emotionViews[selectedEmotion]
            runAnimation(view, true)
        }
    }

    private fun setUnselectedEmoticon(index: Int) {
        if (index >= 0 && index < emotionViews.size) {
            val view: EmotionView = emotionViews[index]
            runAnimation(view, false)
        }
    }


    private fun runAnimation(
        view: EmotionView,
        shouldSelect: Boolean
    ) {
        if(shouldSelect) {
            scaleUpEmoticon(view)
        } else {
            scaleDownEmoticon(view)
        }
    }


    fun scaleUpEmoticon(view : View) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.5f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.5f)

        val animatorSet = AnimatorSet()

        animatorSet.playTogether(scaleX, scaleY)

        animatorSet.duration = 200

        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {

            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }

        })

        animatorSet.start()
    }

    fun scaleDownEmoticon(view : View) {
        val scaleXEnd = ObjectAnimator.ofFloat(view, "scaleX", 1.5f, 1f)
        val scaleYEnd = ObjectAnimator.ofFloat(view, "scaleY", 1.5f, 1f)
        val animatorSetEnd = AnimatorSet()
        animatorSetEnd.playTogether(scaleYEnd, scaleXEnd)
        animatorSetEnd.duration = 150
        animatorSetEnd.start()
        animatorSetEnd.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                if (endTouch == 0) {

                } else if (endTouch == 1) {

                }
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

        })
    }

    //todo animation scale up and down.

}