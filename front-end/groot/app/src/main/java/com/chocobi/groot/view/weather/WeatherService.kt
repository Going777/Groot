package com.chocobi.groot.view.weather

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET
    fun getWeather(@Query("lat") lat: Float,
                   @Query("lon") lon:Float,
                   @Query("appid") appID: String): Call<JsonElement>
}