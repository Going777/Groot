package com.chocobi.groot.view.community.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CommunityService {
    @GET("/api/articles/search")
    fun requestSearchArticle(
        @Query("category") category: String,
        @Query("region") region1: String? = null,
        @Query("region") region2: String? = null,
        @Query("region") region3: String? = null,
        @Query("keyword") keyword: String? = null,
        @Query("page") pageInput: Int,
        @Query("size") sizeInput: Int,
    ): Call<CommunityArticleListResponse>


    @GET("/api/articles/tag")
    fun requestPopularTags(): Call<PopularTagResponse>
}