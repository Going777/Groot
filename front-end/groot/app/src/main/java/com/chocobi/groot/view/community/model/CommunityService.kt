package com.chocobi.groot.view.community.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CommunityService {
    @GET("/api/articles/filter")
    fun requestRegionFilter(
        @Query("region") region1: String? = null,
        @Query("region") region2: String? = null,
        @Query("region") region3: String? = null,
        @Query("page") pageInput: Int,
        @Query("size") sizeInput: Int,
    ): Call<CommunityArticleListResponse>
}