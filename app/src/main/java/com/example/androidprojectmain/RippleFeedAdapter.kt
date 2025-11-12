package com.example.androidprojectmain

import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.androidprojectmain.Models.Ripple
import java.text.SimpleDateFormat
import java.util.*

class RippleFeedAdapter(
    private val onLikeClick: ((Ripple) -> Unit)? = null,
    private val onCommentClick: ((Ripple) -> Unit)? = null,
    private val onRippleClick: ((Ripple) -> Unit)? = null
) : ListAdapter<Ripple, RippleFeedAdapter.RippleViewHolder>(RippleDiffCallback()) {

    private val TAG = "RippleFeedAdapter"

    inner class RippleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.textUsername)
        private val timestampTextView: TextView = itemView.findViewById(R.id.textTimestamp)
        private val contentTextView: TextView = itemView.findViewById(R.id.textContent)
        private val mediaImageView: ImageView = itemView.findViewById(R.id.imageMedia)
        private val likeCountTextView: TextView = itemView.findViewById(R.id.textLikeCount)
        private val commentCountTextView: TextView = itemView.findViewById(R.id.textCommentCount)
        private val buttonLike: ImageView = itemView.findViewById(R.id.buttonLike)
        private val buttonComment: ImageView = itemView.findViewById(R.id.buttonComment)

        fun bind(ripple: Ripple) {
            Log.d(TAG, "Binding ripple: ${ripple.post_id}")

            usernameTextView.text = "User ${ripple.user_id}"
            contentTextView.text = ripple.content
            timestampTextView.text = formatRelativeTime(ripple.created_at)
            likeCountTextView.text = "${ripple.likes_count ?: 0}"
            commentCountTextView.text = "${ripple.comments_count ?: 0}"

            // Load image from S3 with proper error handling
            if (!ripple.media_url.isNullOrEmpty()) {
                Log.d(TAG, "📸 Loading image from S3: ${ripple.media_url}")
                mediaImageView.visibility = View.VISIBLE

                Glide.with(itemView.context)
                    .load(ripple.media_url)
//                    .placeholder(R.drawable.placeholder_image)
//                    .error(R.drawable.error_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(mediaImageView)

                Log.d(TAG, "Image loading started")
            } else {
                Log.d(TAG, "No image for this ripple")
                mediaImageView.visibility = View.GONE
                Glide.with(itemView.context).clear(mediaImageView)
            }

            // Set click listeners
            itemView.setOnClickListener {
                Log.d(TAG, "Ripple clicked: ${ripple.post_id}")
                onRippleClick?.invoke(ripple)
            }

            buttonLike.setOnClickListener {
                Log.d(TAG, "Like button clicked for ripple: ${ripple.post_id}")
                onLikeClick?.invoke(ripple)
            }

            buttonComment.setOnClickListener {
                Log.d(TAG, "Comment button clicked for ripple: ${ripple.post_id}")
                onCommentClick?.invoke(ripple)
            }
        }

        private fun formatRelativeTime(timestamp: String): String {
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val date = sdf.parse(timestamp)

                if (date != null) {
                    DateUtils.getRelativeTimeSpanString(
                        date.time,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE
                    ).toString()
                } else {
                    "Recently"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing timestamp: ${e.message}")
                "Recently"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RippleViewHolder {
        Log.d(TAG, "Creating view holder")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_ripple, parent, false)
        return RippleViewHolder(view)
    }

    override fun onBindViewHolder(holder: RippleViewHolder, position: Int) {
        Log.d(TAG, "Binding view holder at position: $position")
        holder.bind(getItem(position))
    }
}

class RippleDiffCallback : DiffUtil.ItemCallback<Ripple>() {

    override fun areItemsTheSame(oldItem: Ripple, newItem: Ripple): Boolean {
        return oldItem.post_id == newItem.post_id
    }

    override fun areContentsTheSame(oldItem: Ripple, newItem: Ripple): Boolean {
        return oldItem == newItem
    }
}
