package com.beside153.peopleinside.service.community

import com.beside153.peopleinside.model.common.CreateContentRequest
import com.beside153.peopleinside.model.community.comment.CommunityCommentModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CommunityCommentService {
    @GET("/api/community/post/{postId}/comment")
    suspend fun getCommunityCommentList(
        @Path("postId") postId: Long,
        @Query("page") page: Int
    ): List<CommunityCommentModel>

    @POST("/api/community/post/{postId}/comment")
    suspend fun postCommunityComment(
        @Path("postId") postId: Long,
        @Body content: CreateContentRequest
    ): CommunityCommentModel
}
