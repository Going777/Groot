package com.chocobi.groot.view.pot.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PotService {
    @GET("/api/pots/{potId}")
    fun getPotDetail(
        @Path("potId") potId:Int
    ): Call<PotResponse>
}