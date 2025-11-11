package com.example.androidprojectmain.API

import com.example.androidprojectmain.Models.Comment
import com.example.androidprojectmain.Models.CommentRequest
import com.example.androidprojectmain.Models.LikeRequest
import com.example.androidprojectmain.Models.PreSignedUrlResponse
import com.example.androidprojectmain.Models.Ripple
import com.example.androidprojectmain.Models.RippleRequest
import com.example.androidprojectmain.Models.RippleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RippleApiService {

    @GET("api/s3/presigned-url")
    suspend fun getPreSignedUrl(
        @Query("filename") filename: String
    ): Response<PreSignedUrlResponse>

    @POST("api/ripple")
    suspend fun createRipple(
        @Body rippleRequest: RippleRequest
    ) : Response<RippleResponse>

    @GET("api/ripple")
    suspend fun getAllRipples() : Response<List<Ripple>>

    @GET("api/ripple")
    suspend fun getUserRipple(
        @Query("user_id") userId: Int
    ) : Response<List<Ripple>>

    @POST("api/like/")
    suspend fun likeRipple(
        @Body likeRequest: LikeRequest
    ): Response<Map<String, String>>

    @POST("api/ripple/comment/")
    suspend fun commentOnRipple(
        @Body commentRequest: CommentRequest
    ): Response<Map<String, String>>

    @GET("api/getAllComments")
    suspend fun getComments(
        @Query("post_id") postId: Int
    ): Response<List<Comment>>
}