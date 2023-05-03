package com.chocobi.groot.view.search.model

data class PlantSearchResponse(
    val plants: Array<PlantMetaData>,
    val msg: String
)

data class PlantMetaData(
    val plantId: Int?,
    val krName: String?,
    val img: String?
)