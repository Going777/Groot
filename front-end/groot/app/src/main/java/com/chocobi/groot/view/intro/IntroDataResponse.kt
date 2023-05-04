package com.chocobi.groot.view.intro

data class PlantNamesResponse(
    val msg: String,
    val nameList: MutableList<String>
)

data class RegionNameResponse(
    val msg: String,
    val result: String,
    val regions: MutableList<String>
)