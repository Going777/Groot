package com.chocobi.groot.view.community

import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.view.community.model.CommunityArticleDetailResponse
import com.chocobi.groot.view.community.model.CommunityArticleListResponse
import com.chocobi.groot.view.community.model.CommunityCommentResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CommunityCommentService {

    @GET("/api/comments/list/{articleId}") // 요청 url
    fun requestCommunityComment(
//        input 정의
        @Path("articleId") articleIdInput:Int,
        ) : Call<CommunityCommentResponse> // output 정의
}

interface CommunityCommentPostService {
    @POST("api/comments")
    fun requestCommentPost(
        @Body params: CommentPostRequest
    ) : Call<CommunityCommentResponse>

    @DELETE("api/comments/{commentId}/{userPK}")
    fun requestCommentDelete(
        @Path("commentId") commentId:Int,
        @Path("userPK") userPK:Int,
    ) : Call<CommunityCommentResponse>
}

class CommentPostRequest internal constructor(
    val articleId: Int,
    val content: String,
)