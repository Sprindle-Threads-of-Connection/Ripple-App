package com.example.androidprojectmain

/**
 * A data class representing a single "ripple" post.
 * This defines the structure for our dummy data.
 */
data class RipplePost(
    val post_id: Int,
    val user_id: Int,
    val content: String,
    val media_url: String?, // Can be null if there's no image
    val created_at: String,
    val likes_count: Int?,
    val comments_count: Int?
)