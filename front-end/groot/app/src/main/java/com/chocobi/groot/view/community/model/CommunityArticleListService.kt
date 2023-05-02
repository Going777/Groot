package com.chocobi.groot.view.community

import com.chocobi.groot.view.community.model.CommunityArticleListResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CommunityArticleListService {

    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    @GET("/api/articles/category/{category}") // 요청 url
    fun requestCommunityArticleList(
//        input 정의
        @Path("category") categoryInput:String,
        @Query("page") pageInput:Int,
        @Query("size") sizeInput:Int,

        ) : Call<CommunityArticleListResponse> // output 정의
}