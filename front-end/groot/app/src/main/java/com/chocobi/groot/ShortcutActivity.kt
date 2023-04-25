package com.chocobi.groot

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.chocobi.groot.mlkit.kotlin.ml.ArActivity
import com.chocobi.groot.view.login.LoginActivity
import com.chocobi.groot.view.signup.SignupActivity
import com.chocobi.groot.view.weather.WeatherFragment
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class ShortcutActivity : AppCompatActivity() {

    companion object {
        var BaseUrl = "http://api.openweathermap.org/"
        var AppId = "faf492ab03cfcf1c5cd016bad32edb7b"
        var lat = "37.445293"
        var lon = "126.785823"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shortcut)

        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                WeatherFragment.WEATHER_REQUEST
            )
            return
        }

        //Create Retrofit Builder
        val retrofit = Retrofit.Builder()
            .baseUrl(BaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherService::class.java)
        val call = service.getCurrentWeatherData(lat, lon, AppId)

        val textView = findViewById<TextView>(R.id.textView)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.d("MainActivity", "result :" + t.message)
            }

            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.code() == 200) {
                    val weatherResponse = response.body()
                    Log.d("MainActivity", "result: " + weatherResponse.toString())
                    var cTemp = weatherResponse!!.main!!.temp - 273.15  //켈빈을 섭씨로 변환
                    var minTemp = weatherResponse!!.main!!.temp_min - 273.15
                    var maxTemp = weatherResponse!!.main!!.temp_max - 273.15
                    val stringBuilder =
                        "지역: " + weatherResponse!!.sys!!.country + "\n" +
                                "현재기온: " + cTemp + "\n" +
                                "최저기온: " + minTemp + "\n" +
                                "최고기온: " + maxTemp + "\n" +
                                "풍속: " + weatherResponse!!.wind!!.speed + "\n" +
                                "일출시간: " + weatherResponse!!.sys!!.sunrise + "\n" +
                                "일몰시간: " + weatherResponse!!.sys!!.sunset + "\n" +
                                "아이콘: " + weatherResponse!!.weather!!.get(0).icon + "\n" +
                                "위경도: " + lat + "," + lon

                    textView.text = stringBuilder
                }
            }

        })

        val shortcutLogin = findViewById<Button>(R.id.shortcutLogin)
        val shortcutSignup = findViewById<Button>(R.id.shortcutSignup)
        val shortcutMain = findViewById<Button>(R.id.shortcutMain)
        val shortcutIntro = findViewById<Button>(R.id.shortcutIntro)
        val shortcutAr = findViewById<Button>(R.id.shortcutAr)

        shortcutLogin.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        shortcutSignup.setOnClickListener {
            var intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        shortcutMain.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        shortcutIntro.setOnClickListener {
            var intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
        }

        shortcutAr.setOnClickListener {
            var intent = Intent(this, ArActivity::class.java)
            startActivity(intent)
        }

    }
}

interface WeatherService {

    @GET("data/2.5/weather")
    fun getCurrentWeatherData(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String
    ):
            Call<WeatherResponse>
}

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