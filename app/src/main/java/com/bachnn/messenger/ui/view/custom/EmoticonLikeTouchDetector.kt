package com.bachnn.messenger.ui.view.custom

import android.view.MotionEvent

class EmoticonLikeTouchDetector {

    private var emoticonTriggerManagers: EmoticonTriggerManager? = null

    fun configure(emoticonConfig: InitEmoticonConfig) {
        val emoticonTriggerManager = EmoticonTriggerManager()
        emoticonTriggerManager.configure(emoticonConfig)
        emoticonTriggerManagers = emoticonTriggerManager
    }

    fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (emoticonTriggerManagers?.getEmoticonLikeView()?.isShown!! || event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
            val shouldCallSuper = emoticonTriggerManagers?.onTouch(event)
            if (!shouldCallSuper!! && event.action == MotionEvent.ACTION_MOVE) {
                return false
            }
        }

        return true
    }

}