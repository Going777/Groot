package com.chocobi.groot.view.sensor.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface SensorService {
    @GET("/api/plants/{plantId}/env")
    fun getPlantLux(
        @Path("plantId") plantId: Int
    ) : Call<SensorResponse>
}