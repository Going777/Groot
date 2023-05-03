package com.chocobi.groot.view.search.model

data class PlantSearchResponse(
    val plants: Array<PlantMetaData>,
    val msg: String
)
data class PlantDetailResponse(
    val plant: PlantDetailData,
    val msg: String
)

data class PlantMetaData(
    val plantId: Int?,
    val krName: String?,
    val img: String?
)

data class PlantDetailData(
    val plantId: Int,
    val krName: String,
    val sciName: String,
    val waterCycle: String,
    val minHumidity: Int,
    val maxHumidity: Int,
    val minGrwTemp: Int,
    val maxGrwTemp: Int,
    val description: String,
    val place: String,
    val mgmtLevel: String,
    val mgmtDemand: String,
    val mgmtTip: String,
    val insectInfo: String,
    val img: String
)