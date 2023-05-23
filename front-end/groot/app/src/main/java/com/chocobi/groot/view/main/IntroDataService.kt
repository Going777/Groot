package com.chocobi.groot.view.main

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface IntroDataService {
    @Headers("accept: application/json")
    @GET("/api/plants/names")
    fun requestPlantNames(): Call<PlantNamesResponse>

    @GET("/api/articles/regions/list")
    fun requestRegionNames(): Call<RegionNameResponse>

    @GET("/api/characters/images")
    fun requestCharacters(): Call<CharacterResponse>
}