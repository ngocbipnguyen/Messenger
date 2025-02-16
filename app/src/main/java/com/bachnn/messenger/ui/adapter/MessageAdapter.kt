package com.bachnn.messenger.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bachnn.messenger.R
import com.bachnn.messenger.constants.Constants
import com.bachnn.messenger.data.model.Message
import com.bachnn.messenger.data.model.User
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(val messages: List<Message>, val user: User, val emoticonLongClick: (View, Int) -> Unit) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    abstract class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        abstract fun bind(message: Message)
    }

    //right message.

    class RightHolder(view: View) : ViewHolder(view) {

        private val messageText : TextView
        private val messageImage: ImageView
        val sendingText: TextView

        init {
            messageText = view.findViewById(R.id.right_message_text)
            messageImage = view.findViewById(R.id.right_message_image)
            sendingText = view.findViewById(R.id.sending)
        }

        override fun bind(message: Message) {
            if (message.type == Constants.TYPE_TEXT) {
                messageText.text = message.content
                messageText.visibility = View.VISIBLE
                messageImage.visibility = View.GONE
            } else {
                Glide.with(view).load(message.content).into(messageImage)
                messageText.visibility = View.GONE
                messageImage.visibility = View.VISIBLE
            }
        }

    }


    class LeftHolder(view: View,private val user: User, val emoticonLongClick: (View, Int) -> Unit) : ViewHolder(view) {

        private val circleImage : CircleImageView
        private val messageText: TextView
        private val messageImage: ImageView
        val leftView: FrameLayout
        val emoticonFrame: FrameLayout
        val emoticonImage: ImageView


        init {
            circleImage = view.findViewById(R.id.circle_image)
            messageText = view.findViewById(R.id.left_message_text)
            messageImage = view.findViewById(R.id.left_message_image)
            leftView = view.findViewById(R.id.left_view)
            emoticonFrame = view.findViewById(R.id.emoticon_frame)
            emoticonImage = view.findViewById(R.id.emoticon_icon)
        }
        override fun bind(message: Message) {
            Glide.with(view).load(user.photoUrl).into(circleImage)
            if (message.type == Constants.TYPE_TEXT) {
                messageText.text = message.content
                messageText.visibility = View.VISIBLE
//                messageImage.visibility = View.GONE
            } else {
                Glide.with(view).load(message.content).into(messageImage)
//                messageText.visibility = View.GONE
                messageImage.visibility = View.VISIBLE
            }

            val drawableInt = typeToDrawable(message.emoticonType)
            if (drawableInt >= 0) {
                emoticonImage.setImageResource(drawableInt)
                emoticonImage.visibility = View.VISIBLE
                emoticonFrame.visibility = View.VISIBLE
            }
        }

        fun typeToDrawable(type: String): Int {
            return if (type == Constants.EMOTICON_LIKE) {
                R.drawable.like
            } else if (type == Constants.EMOTICON_HAHA) {
                R.drawable.haha
            } else if (type == Constants.EMOTICON_KISS) {
                R.drawable.kiss
            } else if (type == Constants.EMOTICON_P) {
                R.drawable.p
            } else if (type == Constants.EMOTICON_SAD) {
                R.drawable.sad
            } else {
                -1
            }
        }

    }


    override fun getItemViewType(position: Int): Int {
        return if (messages[position].idFrom == user.uid) {
            return Constants.LEFT_MESSAGE
        } else {
            return Constants.RIGHT_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == Constants.RIGHT_MESSAGE) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.right_message_item, parent, false)
            RightHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.left_message_item, parent, false)
            LeftHolder(view, user, emoticonLongClick)
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (messages[position].idFrom == user.uid) {
            val leftHolder : LeftHolder = holder as LeftHolder
            leftHolder.bind(messages[position])
            leftHolder.leftView.setOnLongClickListener {
                emoticonLongClick(it.rootView, position)
                true
            }

        } else {
            val rightHolder : RightHolder = holder as RightHolder
            rightHolder.bind(messages[position])
        }
    }
}