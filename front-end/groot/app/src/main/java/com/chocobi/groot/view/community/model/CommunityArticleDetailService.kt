package com.chocobi.groot.view.community

import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.view.community.model.BookmarkResponse
import com.chocobi.groot.view.community.model.CommunityArticleDetailResponse
import com.chocobi.groot.view.community.model.CommunityShareItemResponse
import retrofit2.Call
import retrofit2.http.Body
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

interface CommunityShareItemService {

    @GET("/api/articles/share/{articleId}") // 요청 url
    fun requestCommunityShareItem(
//        input 정의
        @Path("articleId") articleIdInput:Int,
    ) : Call<CommunityShareItemResponse> // output 정의
}

interface CommunityShareStatusService {
    @PUT("/api/articles/shareStatus")
    fun requestCommunityShareStatus(
        @Body params: ShareStatusRequest,
    ): Call<BasicResponse>
}
class ShareStatusRequest internal constructor(
    val articleId: Int,
    val userPK: Int,
)