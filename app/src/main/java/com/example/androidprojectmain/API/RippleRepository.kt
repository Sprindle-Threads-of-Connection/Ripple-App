package com.example.androidprojectmain.API

import android.util.Log
import com.bumptech.glide.request.Request
import com.example.androidprojectmain.Models.Comment
import com.example.androidprojectmain.Models.CommentRequest
import com.example.androidprojectmain.Models.LikeRequest
import com.example.androidprojectmain.Models.Ripple
import com.example.androidprojectmain.Models.RippleRequest
import com.example.androidprojectmain.Models.RippleResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class RippleRepository {
    private val apiService = RetrofitClient.instance
    private val TAG = "RippleRepository"

    suspend fun createRippleWithImage(
        userId: Int,
        content: String,
        imageFile: File?
    ): Result<RippleResponse> {
        return try {
            var permanentS3Url: String? = null

            // Step 1 & 2 & 3: If image exists, upload it and get permanent URL
            if (imageFile != null && imageFile.exists()) {
                Log.d(TAG, "📤 Starting Image Upload Flow")
                Log.d(TAG, "Requesting presigned URL for: ${imageFile.name}")

                val urlResult = getPresignedUploadUrl(imageFile.name)

                if (urlResult.isFailure) {
                    Log.e(TAG, "Failed to get presigned URL")
                    return Result.failure(urlResult.exceptionOrNull()!!)
                }

                val presignedUploadUrl = urlResult.getOrNull()!!
                Log.d(TAG, "URL: $presignedUploadUrl")

                // STEP 2: Upload image to S3 bucket using presigned URL
                Log.d(TAG, "\nUploading image to AWS S3 bucket...")
                val uploadResult = uploadImageToS3(presignedUploadUrl, imageFile)

                if (uploadResult.isFailure) {
                    Log.e(TAG, "Failed to upload image to S3")
                    return Result.failure(uploadResult.exceptionOrNull()!!)
                }

                Log.d(TAG, "✅ Image successfully uploaded to S3 bucket!")

                permanentS3Url = extractPermanentUrl(presignedUploadUrl)
                Log.d(TAG, "Permanent URL: $permanentS3Url")
            }

            // STEP 4: Create ripple with permanent S3 URL
            Log.d(TAG, "\nSTEP 4: Creating ripple in database...")

            val rippleResult = createRipple(userId, content, permanentS3Url)

            if (rippleResult.isSuccess) {
                Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                Log.d(TAG, "✅ RIPPLE CREATED SUCCESSFULLY!")
                Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            }

            rippleResult

        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception in createRippleWithImage: ${e.message}", e)
            Result.failure(e)
        }
    }

    private fun extractPermanentUrl(presignedUrl: String): String {
        val permanentUrl = presignedUrl.split("?")[0]
        Log.d(TAG, "Extracted permanent URL from presigned URL")
        return permanentUrl
    }

    private suspend fun uploadImageToS3(presignedUrl: String, imageFile: File): Result<Unit> {
        Log.d(TAG, "⬆️ Uploading to S3 bucket...")
        Log.d(TAG, "File: ${imageFile.name}")
        Log.d(TAG, "Size: ${imageFile.length()} bytes")

        return try {
            val client = OkHttpClient()

            // Determine content type based on file extension
            val contentType = when (imageFile.extension.lowercase()) {
                "jpg", "jpeg" -> "image/jpeg"
                "png" -> "image/png"
                "gif" -> "image/gif"
                "webp" -> "image/webp"
                else -> "application/octet-stream"
            }

            Log.d(TAG, "Content-Type: $contentType")

            val requestBody = imageFile.asRequestBody(contentType.toMediaTypeOrNull())

            // PUT request to upload to S3
            val request = okhttp3.Request.Builder()
                .url(presignedUrl)
                .put(requestBody)
                .addHeader("Content-Type", contentType)
                .build()

            Log.d(TAG, "Sending PUT request to S3...")
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                Log.d(TAG, "Image is now stored in AWS S3 bucket")
                Result.success(Unit)
            } else {
                Log.e(TAG, "S3 Upload Failed!")
                Log.e(TAG, "Response Message: ${response.message}")
                Result.failure(Exception("S3 upload failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during S3 upload: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun getPresignedUploadUrl(filename: String): Result<String> {
        Log.d(TAG, "🔗 Requesting presigned upload URL from backend...")
        Log.d(TAG, "Endpoint: /api/s3/presigned-url?filename=$filename")

        return try {
            val response = apiService.getPreSignedUrl(filename)

            if (response.isSuccessful && response.body() != null) {
                val presignedUrl = response.body()!!.url
                Log.d(TAG, "✅ Presigned URL received from backend")
                Log.d(TAG, "This URL is temporary and will be used for uploading only")
                Result.success(presignedUrl)
            } else {
                Log.e(TAG, "❌ Failed to get presigned URL")
                Log.e(TAG, "Response Code: ${response.code()}")
                Result.failure(Exception("Failed to get presigned URL: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception while getting presigned URL: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun createRipple(userId: Int, content: String, mediaUrl: String?): Result<RippleResponse> {
        Log.d(TAG, "📝 Sending create ripple request to backend...")
        Log.d(TAG, "Endpoint: POST /api/ripple/")
        Log.d(TAG, "Body: {user_id: $userId, content: '$content', media_url: '$mediaUrl'}")

        return try {
            val response = apiService.createRipple(
                RippleRequest(userId, content, mediaUrl)
            )

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Ripple created in database!")
                Log.d(TAG, "Response: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                Log.e(TAG, "Failed to create ripple")
                Log.e(TAG, "Response Code: ${response.code()}")
                Result.failure(Exception("Failed to create ripple: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while creating ripple: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun createTextOnlyRipple(userId: Int, content: String): Result<RippleResponse> {
        Log.d(TAG, "Creating text-only ripple (no image)")
        return createRipple(userId, content, null)
    }

    suspend fun getAllRipples(): Result<List<Ripple>> {
        Log.d(TAG, "Fetching all ripples from backend...")

        return try {
            val response = apiService.getAllRipples()

            if (response.isSuccessful && response.body() != null) {
                val ripples = response.body()!!
                Log.d(TAG, "✅ Fetched ${ripples.size} ripples from backend")

                ripples.forEachIndexed { index, ripple ->
                    Log.d(TAG, "\n📄 Ripple #${index + 1}")
                    Log.d(TAG, "  Post ID: ${ripple.post_id}")
                    Log.d(TAG, "  User ID: ${ripple.user_id}")
                    Log.d(TAG, "  Content: ${ripple.content}")
                    Log.d(TAG, "  Media URL: ${ripple.media_url ?: "No image"}")

                    if (ripple.media_url != null) {
                        Log.d(TAG, "  🖼️ This ripple has an image stored in S3")
                        Log.d(TAG, "  The image will be loaded from: ${ripple.media_url}")
                    }
                }
                Result.success(ripples)
            } else {
                Log.e(TAG, "Failed to fetch ripples - Code: ${response.code()}")
                Result.failure(Exception("Failed to fetch ripples: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while fetching ripples: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun likeRipple(userId: Int, postId: Int): Result<String> {
        Log.d(TAG, "Liking ripple - UserId: $userId, PostId: $postId")
        return try {
            val response = apiService.likeRipple(LikeRequest(userId, postId))
            if (response.isSuccessful) {
                Log.d(TAG, "Ripple liked successfully")
                Result.success("Liked successfully")
            } else {
                Log.e(TAG, "Failed to like - Code: ${response.code()}")
                Result.failure(Exception("Failed to like: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while liking: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun commentOnRipple(userId: Int, postId: Int, content: String): Result<String> {
        Log.d(TAG, "Adding comment - UserId: $userId, PostId: $postId")
        return try {
            val response = apiService.commentOnRipple(CommentRequest(userId, postId, content))
            if (response.isSuccessful) {
                Result.success("Comment added")
            } else {
                Result.failure(Exception("Failed to comment: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while commenting: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getComments(postId: Int): Result<List<Comment>> {
        Log.d(TAG, "Fetching comments for post: $postId")
        return try {
            val response = apiService.getComments(postId)
            if (response.isSuccessful && response.body() != null) {
                val comments = response.body()!!
                Log.d(TAG, "Fetched ${comments.size} comments")
                Result.success(comments)
            } else {
                Log.e(TAG, "❌ Failed to fetch comments - Code: ${response.code()}")
                Result.failure(Exception("Failed to fetch comments: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception while fetching comments: ${e.message}", e)
            Result.failure(e)
        }
    }
}