package com.chocobi.groot.view.sensor.model

data class SensorResponse(
    val msg:String,
    val env: Env
)

data class Env(
    val minLux:Int,
    val maxLux: Int
)