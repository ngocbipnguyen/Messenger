package com.bachnn.messenger.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bachnn.messenger.R
import com.bachnn.messenger.data.model.Media
import com.bumptech.glide.Glide

class MediaAdapter(private val medias: List<Media>, onClickMedia: (Media) -> Unit) : RecyclerView.Adapter<MediaAdapter.ViewHolder>() {


    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val imageView: ImageView
        val videoView: ImageView
        val iconView: ImageView

        init {
            imageView = view.findViewById(R.id.image_media_item)
            videoView = view.findViewById(R.id.video_media_item)
            iconView = view.findViewById(R.id.icon_media)
        }

        fun bind(media: Media) {
            if (media.mimeType.contains("image")) {
                imageView.visibility = View.VISIBLE
                Glide.with(view).load(media.uri).into(imageView)
            } else {
                videoView.visibility = View.VISIBLE
                iconView.visibility = View.VISIBLE
                Glide.with(view).load(media.uri).into(videoView)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = medias.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(medias[position])
    }
}