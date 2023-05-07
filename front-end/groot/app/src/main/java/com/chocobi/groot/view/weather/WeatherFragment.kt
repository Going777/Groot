package com.chocobi.groot.view.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.loopj.android.http.RequestParams
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Math.ceil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WeatherFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WeatherFragment : Fragment() {

    companion object {
        const val API_KEY: String = "28cad4ba682e9a7b543ea7dfe3b5d05b"
        const val MIN_TIME: Long = 5000
        const val MIN_DISTANCE: Float = 1000F
        const val WEATHER_REQUEST: Int = 102
    }

   private val TAG = "WeatherFragment"

    //        현재 위치 가져오기
    var mLocationManager: LocationManager? = null
    var mLocationListener: LocationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_weather, container, false)


//        레이아웃에 오늘 날짜 적용
        val dateText = rootView.findViewById<TextView>(R.id.date_text)
        dateText.text = GlobalVariables.getCurrentDate()

        val thermometerText = rootView.findViewById<TextView>(R.id.thermomete_text)
        val humidityText = rootView.findViewById<TextView>(R.id.humidity_text)
        val weatherBgView = rootView.findViewById<ImageView>(R.id.weather_bg)
        val weatherIcon = rootView.findViewById<ImageView>(R.id.weather_icon)

        val layoutParams = LayoutParams(
            thermometerText,
            humidityText,
            weatherBgView,
            weatherIcon
        )


//      현재 위치 날씨 받아오기
        getWeatherInCurrentLocation(layoutParams)
        updateWeatherImageView(layoutParams)


        return rootView
    }

    private fun getWeatherInCurrentLocation(layoutParams: LayoutParams) {
//        현재 위치 받아오기
        var current_lat: String = "37.5"
        var current_lon: String = "127.0"

        mLocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocationListener = LocationListener { p0 ->
            val params: RequestParams = RequestParams()
            current_lat = p0.latitude.toString()
            current_lon = p0.longitude.toString()
            doNetworking(current_lat, current_lon, layoutParams)
        }

//      권한 확인
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                WEATHER_REQUEST
            )
            return
        }
        mLocationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            MIN_TIME,
            MIN_DISTANCE,
            mLocationListener!!
        )
        mLocationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            MIN_TIME,
            MIN_DISTANCE,
            mLocationListener!!
        )
    }

    private fun doNetworking(lat: String, lon: String, layoutParams: LayoutParams) {
        Log.d(TAG, "doNetworking() / lat: $lat / lon: $lon")

//        retrofit 객체 만들기
        var retrofit = Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

//        service 객체 만들기
        var weatherService = retrofit.create(WeatherService::class.java)


//        날씨 요청 보내기
        weatherService.getWeather(lat, lon, API_KEY).enqueue(object : Callback<WeatherResponse> {
            //            통신 성공
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.code() == 200) {
                    val weatherResponse = response.body()
                    val target = weatherCondition(weatherResponse!!.weather.get(0).id)
                    val cTemp = ceil(weatherResponse!!.main!!.temp - 273.15).toInt()  //켈빈을 섭씨로 변환
                    val hum = weatherResponse!!.main!!.humidity.toInt()
                    GlobalVariables.updateWeatherData(target, cTemp, hum)
                    updateWeatherImageView(layoutParams)

                }
            }

            //            통신 실패
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                var dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle("실패!")
            }
        })
    }

    private fun weatherCondition(condition: Int): String {
        if (condition in 200..299) {
            return "rain_thunder"
        } else if (condition in 300..599) {
            return "rain"
        } else if (condition in 600..700) {
            return "snow"
        } else if (condition in 701..799) {
            return "cloudy"
        } else if (condition == 800) {
            return "sun"
        } else if (condition in 801..804) {
            return "cloudy"
        } else if (condition in 900..902) {
            return "rain_thunder"
        }
        return "sun"
    }

    private fun updateWeatherImageView(
        layoutParams: LayoutParams,
    ) {
        val (target, cTemp, hum) = GlobalVariables.getWeatherData()
        layoutParams.thermometerText.text = cTemp.toString() + "℃"
        layoutParams.humidityText.text = hum.toString() + "%"
        when (target) {
            "sun" -> {
                layoutParams.weatherBgView.setImageResource(R.drawable.weather_sun_gradient_bg)
                layoutParams.weatherIcon.setImageResource(R.drawable.weather_sun)
            }

            "cloudy" -> {
                layoutParams.weatherBgView.setImageResource(R.drawable.weather_cloudy_gradient_bg)
                layoutParams.weatherIcon.setImageResource(R.drawable.weather_cloudy)
            }

            "snow" -> {
                layoutParams.weatherBgView.setImageResource(R.drawable.weather_snow_gradient_bg)
                layoutParams.weatherIcon.setImageResource(R.drawable.weather_snow)
            }

            "rain" -> {
                layoutParams.weatherBgView.setImageResource(R.drawable.weather_rain_gradient_bg)
                layoutParams.weatherIcon.setImageResource(R.drawable.weather_rain)
            }

            "rain_thunder" -> {
                layoutParams.weatherBgView.setImageResource(R.drawable.weather_rain_thunder_gradient_bg)
                layoutParams.weatherIcon.setImageResource(R.drawable.weather_rain_thunder)
            }
        }
    }
}


