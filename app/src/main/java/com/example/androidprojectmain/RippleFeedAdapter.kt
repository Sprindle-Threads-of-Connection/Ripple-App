package com.example.androidprojectmain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RippleFeedAdapter : ListAdapter<RipplePost, RippleFeedAdapter.RippleViewHolder>(RippleDiffCallback()) {

    class RippleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.textUsername)
        private val timestampTextView: TextView = itemView.findViewById(R.id.textTimestamp)
        private val contentTextView: TextView = itemView.findViewById(R.id.textContent)
        private val mediaImageView: ImageView = itemView.findViewById(R.id.imageMedia)
        private val likeCountTextView: TextView = itemView.findViewById(R.id.textLikeCount)
        private val commentCountTextView: TextView = itemView.findViewById(R.id.textCommentCount)

        fun bind(ripple: RipplePost) {
            usernameTextView.text = "User ${ripple.user_id}"
            contentTextView.text = ripple.content
            timestampTextView.text = "some time ago" // Placeholder for timestamp

            likeCountTextView.text = ripple.likes_count?.toString() ?: "0"
            commentCountTextView.text = ripple.comments_count?.toString() ?: "0"

            if (!ripple.media_url.isNullOrEmpty()) {
                mediaImageView.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(ripple.media_url)
                    .into(mediaImageView)
            } else {
                mediaImageView.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RippleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_ripple, parent, false)
        return RippleViewHolder(view)
    }

    override fun onBindViewHolder(holder: RippleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class RippleDiffCallback : DiffUtil.ItemCallback<RipplePost>() {
    override fun areItemsTheSame(oldItem: RipplePost, newItem: RipplePost): Boolean {
        return oldItem.post_id == newItem.post_id
    }

    override fun areContentsTheSame(oldItem: RipplePost, newItem: RipplePost): Boolean {
        return oldItem == newItem
    }
}