package com.chocobi.groot.view.weather

import android.util.Log
import com.google.gson.annotations.SerializedName
import org.json.JSONException
import org.json.JSONObject

//data class WeatherData(
//
//)


//class WeatherData{
//
//    lateinit var tempString: String
//    lateinit var icon: String
//    lateinit var weatherType: String
//    private var weatherId: Int = 0
//    private var tempInt: Int =0
//
//    val TAG: String = "로그"
//    fun fromJson(jsonObject: JSONObject?): WeatherData? {
//        Log.d(TAG, "WeatherData, $jsonObject",)
//        try{
//            var weatherData = WeatherData()
//            weatherData.weatherId = jsonObject?.getJSONArray("weather")?.getJSONObject(0)?.getInt("id")!!
//            weatherData.weatherType = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main")
//            weatherData.icon = updateWeatherIcon(weatherData.weatherId)
//            val roundedTemp: Int = (jsonObject.getJSONObject("main").getDouble("temp")-273.15).toInt()
//            weatherData.tempString = roundedTemp.toString()
//            weatherData.tempInt = roundedTemp
//            return weatherData
//        }catch (e: JSONException){
//            e.printStackTrace()
//            return null
//        }
//    }
//
    private fun weatherCondition(condition: Int): String {
        if (condition in 200..299) {
            return "thunderstorm"
        } else if (condition in 300..499) {
            return "rain"
        } else if (condition in 500..599) {
            return "rain"
        } else if (condition in 600..700) {
            return "snow"
        } else if (condition in 701..771) {
            return "cloudy"
        } else if (condition in 772..799) {
            return "cloudy"
        } else if (condition == 800) {
            return "sun"
        } else if (condition in 801..804) {
            return "cloudy"
        } else if (condition in 900..902) {
            return "thunderstorm"
        }
        if (condition == 903) {
            return "snow"
        }
        if (condition == 904) {
            return "clear"
        }
        return if (condition in 905..1000) {
            "thunderstorm"
        } else "dunno"
    }
//}

class WeatherResponse() {
    @SerializedName("weather")
    var weather = ArrayList<Weather>()
    @SerializedName("main")
    var main: Main? = null
    @SerializedName("wind")
    var wind: Wind? = null
    @SerializedName("sys")
    var sys: Sys? = null
}

class Weather {
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("main")
    var main: String? = null
    @SerializedName("description")
    var description: String? = null
    @SerializedName("icon")
    var icon: String? = null
}

class Main {
    @SerializedName("temp")
    var temp: Float = 0.toFloat()

    @SerializedName("humidity")
    var humidity: Float = 0.toFloat()

    @SerializedName("pressure")
    var pressure: Float = 0.toFloat()

    @SerializedName("temp_min")
    var temp_min: Float = 0.toFloat()

    @SerializedName("temp_max")
    var temp_max: Float = 0.toFloat()

}

class Wind {
    @SerializedName("speed")
    var speed: Float = 0.toFloat()

    @SerializedName("deg")
    var deg: Float = 0.toFloat()
}

class Sys {
    @SerializedName("country")
    var country: String? = null

    @SerializedName("sunrise")
    var sunrise: Long = 0

    @SerializedName("sunset")
    var sunset: Long = 0
}