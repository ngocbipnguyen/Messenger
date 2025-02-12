package com.bachnn.messenger.ui.view

import android.os.Handler
import android.view.MotionEvent
import android.view.View
import com.bachnn.messenger.ui.view.custom.EmoticonConstant
import com.bachnn.messenger.ui.view.custom.EmoticonGroupView
import com.bachnn.messenger.ui.view.custom.InitEmoticonConfig

class EmoticonTriggerManager {

    private var triggerView: View? = null

    private var emoticonGroupView: EmoticonGroupView? = null

    private var touchDownDelay: Int = 0

    private var touchUpDelay: Int = 0

    private var triggerViewTouched: Boolean = false

    private var shouldSendEventsToEmoticonView: Boolean = false

    private var downEvent: MotionEvent? = null

    private var shouldWaitForClosing: Boolean = false

    private var upEvent: MotionEvent? = null

    fun configure(emoticonConfig: InitEmoticonConfig) {
        this.triggerView = emoticonConfig.triggerView
        this.touchDownDelay = EmoticonConstant.TOUCH_DOWN_DELAY
        this.touchUpDelay =  EmoticonConstant.TOUCH_UP_DELAY
        this.emoticonGroupView = emoticonConfig.emoticonGroupView
    }

    private fun intersectView(view: View, rx: Int, ry: Int): Boolean {
        val l = IntArray(2)
        view.getLocationInWindow(l)
        val x = l[0]
        val y = l[1]
        val w = view.width
        val h = view.height
        return !(rx < x || rx > x + w || ry < y || ry > y + h)
    }

    fun onTouch(event: MotionEvent): Boolean {
        val x: Float = event.rawX
        val y: Float = event.rawY
        val action: Int = event.action

        if (shouldWaitForClosing) {
            return true
        }

        if (action == MotionEvent.ACTION_DOWN) {

            if (intersectView(triggerView!!, x.toInt(), y.toInt())) {
                triggerViewTouched = true
                shouldSendEventsToEmoticonView = false
                downEvent = event

                Handler().postDelayed({
                    if (triggerViewTouched) {
                        //if the user has touched and the touch is still down
                        shouldSendEventsToEmoticonView = true
                        emoticonGroupView?.onTouchDown(downEvent!!.rawX, downEvent!!.rawY)
//                        emoticonGroupView?.show()
                    }
                }, touchDownDelay.toLong())

                return false
            }

        } else if (action == MotionEvent.ACTION_MOVE) {

            if (triggerViewTouched) {
                if (shouldSendEventsToEmoticonView) {
                    emoticonGroupView?.onTouchMove(x, y)
                    return false
                }
            }

        } else if (action == MotionEvent.ACTION_UP) {
            triggerViewTouched = false
            shouldSendEventsToEmoticonView = false
            shouldWaitForClosing = false
            upEvent = event


            Handler().postDelayed({
                shouldWaitForClosing = false
                emoticonGroupView?.onTouchUp(x,y)
//                emoticonGroupView?.hide()
            }, touchUpDelay.toLong())

            return false
        }

        return true
    }

    fun getEmoticonLikeView(): EmoticonGroupView {
        return emoticonGroupView!!
    }

}