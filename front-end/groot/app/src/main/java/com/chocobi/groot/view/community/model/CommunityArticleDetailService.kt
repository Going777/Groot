package com.chocobi.groot.view.community

import com.chocobi.groot.view.community.model.BookmarkResponse
import com.chocobi.groot.view.community.model.CommunityArticleDetailResponse
import com.chocobi.groot.view.community.model.CommunityArticleListResponse
import com.chocobi.groot.view.login.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CommunityArticleDetailService {

    @GET("/api/articles/{articleId}") // 요청 url
    fun requestCommunityArticleDetail(
//        input 정의
        @Path("articleId") articleIdInput:Int,
        ) : Call<CommunityArticleDetailResponse> // output 정의
}

interface CommunityBookmarkService {
    @PUT("/api/articles/bookmark")
    fun requestCommunityBookmark(
        @Body params: BookmarkRequest,
    ): Call<BookmarkResponse>
}

class BookmarkRequest internal constructor(
    val articleId: Int,
    val userPK: Int,
    val bookmarkStatus: Boolean,
)