package com.chocobi.groot.view.pot.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NotiService {
    @GET("/api/notifications/list")
    fun getNotiList(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<NotiResponse>
}