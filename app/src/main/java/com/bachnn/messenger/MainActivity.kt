package com.bachnn.messenger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.bachnn.messenger.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import ro.andreidobrescu.emojilike.EmojiConfig
import ro.andreidobrescu.emojilike.EmojiLikeTouchDetector
import ro.andreidobrescu.emojilike.IActivityWithEmoji


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), IActivityWithEmoji {

    private lateinit var binding: ActivityMainBinding
    lateinit var emojiLikeTouchDetector: EmojiLikeTouchDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        emojiLikeTouchDetector = EmojiLikeTouchDetector()

    }


    //override these 2 methods if your activity doesn't extend ActivityWithEmoji
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val shouldCallSuper = emojiLikeTouchDetector!!.dispatchTouchEvent(event)
        if (shouldCallSuper) return super.dispatchTouchEvent(event)
        return false
    }

    override fun configureEmojiLike(p0: EmojiConfig?) {
        emojiLikeTouchDetector.configure(p0);
    }
}