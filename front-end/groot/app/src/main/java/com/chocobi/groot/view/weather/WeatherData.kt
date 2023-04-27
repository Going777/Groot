package com.chocobi.groot.view.weather

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chocobi.groot.R
import com.google.gson.annotations.SerializedName

data class LayoutParams(
    val thermometerText: TextView,
    val humidityText: TextView,
    val weatherBgView: ImageView,
    val weatherIcon: ImageView
)

class WeatherResponse() {
    @SerializedName("weather")
    var weather = ArrayList<Weather>()

    @SerializedName("main")
    var main: Main? = null
}

class Weather {
    @SerializedName("id")
    var id: Int = 0
}

class Main {
    @SerializedName("temp")
    var temp: Float = 0.toFloat()

    @SerializedName("humidity")
    var humidity: Float = 0.toFloat()
}

