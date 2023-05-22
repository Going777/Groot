package com.chocobi.groot.view.pot.model

import com.chocobi.groot.data.BasicResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NotiService {
    @GET("/api/notifications/list")
    fun getNotiList(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<NotiResponse>

    @PUT("/api/notifications/readCheck/{notificationId}")
    fun requestReadCheck(
        @Path("notificationId") notificationId: Int
    ): Call<BasicResponse>

}