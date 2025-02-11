package com.bachnn.messenger.ui.view

import android.view.MotionEvent
import java.util.LinkedList

class EmoticonLikeTouchDetector {

    private var emoticonTriggerManagers: MutableList<EmoticonTriggerManager>? = null

    private fun getEmoticonTriggerManagers(): MutableList<EmoticonTriggerManager> {
        if (emoticonTriggerManagers == null) emoticonTriggerManagers = LinkedList()
        return emoticonTriggerManagers as MutableList<EmoticonTriggerManager>
    }

    fun configure(emoticonConfig: EmoticonConfig) {
        val emoticonTriggerManager = EmoticonTriggerManager()
        emoticonTriggerManager.configure(emoticonConfig)
        getEmoticonTriggerManagers().add(emoticonTriggerManager)
    }

    fun dispatchTouchEvent(event: MotionEvent): Boolean {
        getEmoticonTriggerManagers().forEach { it ->
            if (it.getEmoticonLikeView().isShown || event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
                val shouldCallSuper = it.onTouch(event)
                if (!shouldCallSuper && event.action == MotionEvent.ACTION_MOVE) {
                    return false
                }
            }
        }

        return true
    }

}