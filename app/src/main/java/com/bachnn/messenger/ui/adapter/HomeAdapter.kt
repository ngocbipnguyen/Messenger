package com.bachnn.messenger.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bachnn.messenger.R
import com.bachnn.messenger.data.model.User

class HomeAdapter(private val users: List<User>,val onClickUser:(User) -> Unit): RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    class ViewHolder(view: View, val onClickUser:(User) -> Unit) : RecyclerView.ViewHolder(view) {
        private val personImage: ImageView
        private val username: TextView
        private val email: TextView
        private val viewGroup : ConstraintLayout
        lateinit var user: User

        init {
            viewGroup = view.findViewById(R.id.home_item_id)
            personImage = view.findViewById(R.id.person_user)
            username = view.findViewById(R.id.name_edit)
            email = view.findViewById(R.id.email_user)

            viewGroup.setOnClickListener {
                onClickUser(user)
            }
        }

        fun bind(user: User) {
            this.user = user
//            personImage.setImageURI(user)
            username.text = user.name
            email.text = user.email

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_item, parent, false)
        return ViewHolder(view, onClickUser)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }


}