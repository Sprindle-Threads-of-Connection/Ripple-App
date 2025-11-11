package com.example.androidprojectmain.Models

import com.example.androidprojectmain.RipplePost

data class RippleRequest(
    val user_id: Int,
    val content: String,
    val media_url: String? = null
)

data class RippleResponse(
    val success: Boolean,
    val message: String,
    val ripples: List<RipplePost>
)

data class Ripple(
    val post_id: Int,
    val user_id: Int,
    val content: String,
    val media_url: String?,
    val created_at: String,
    val likes_count: Int? = null,
    val comments_count: Int? = null
)

data class LikeRequest(
    val user_id: Int,
    val post_id: Int
)

data class CommentRequest(
    val user_id: Int,
    val post_id: Int,
    val content: String
)

data class Comment(
    val comment_id: Int,
    val user_id: Int,
    val content: String,
    val created_at: String
)

data class PreSignedUrlResponse(
    val url: String
)