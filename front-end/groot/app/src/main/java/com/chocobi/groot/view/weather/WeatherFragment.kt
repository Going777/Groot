package com.chocobi.groot.view.weather

import android.Manifest
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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

import com.chocobi.groot.R
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Math.ceil
import java.time.LocalDate
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

    val TAG: String = "로그"

    companion object {
        const val API_KEY: String = "28cad4ba682e9a7b543ea7dfe3b5d05b"
        const val WEATHER_URL: String = "https://api.openweathermap.org/data/2.5/weather"
        const val MIN_TIME: Long = 5000
        const val MIN_DISTANCE: Float = 1000F
        const val WEATHER_REQUEST: Int = 102
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //        현재 위치 가져오기
    var mLocationManager: LocationManager? = null
    var mLocationListener: LocationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, "WeatherFragment, onCreateView()")
        val rootView = inflater.inflate(R.layout.fragment_weather, container, false)

//        현재 시간 가져오기
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("MM월 dd일")
        val formattedDate = current.format(formatter)

//        현재 위치 받아오기


        val textView = rootView.findViewById<TextView>(R.id.textView)
        val dateText = rootView.findViewById<TextView>(R.id.date_text)
        val thermometerText = rootView.findViewById<TextView>(R.id.thermomete_text)
        val humidityText = rootView.findViewById<TextView>(R.id.humidity_text)
        // Inflate the layout for this fragment

        dateText.text = formattedDate

        getWeatherInCurrentLocation(thermometerText, humidityText)

        return rootView
    }

    private fun getWeatherInCurrentLocation(thermometerText: TextView, humidityText: TextView) {
//        현재 위치 받아오기
        var current_lat: String = "37.5"
        var current_lon: String = "127.0"

        mLocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocationListener = LocationListener { p0 ->
            val params: RequestParams = RequestParams()
            current_lat = p0.latitude.toString()
            current_lon = p0.longitude.toString()
            doNetworking(current_lat, current_lon, thermometerText, humidityText)
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

    private fun doNetworking(lat: String, lon: String, thermometerText: TextView, humidityText: TextView) {
        Log.d(TAG, "WeatherFragment $lat, $lon")

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
                    var cTemp = ceil(weatherResponse!!.main!!.temp - 273.15).toInt()  //켈빈을 섭씨로 변환
                    var hum = weatherResponse!!.main!!.humidity.toInt()
//                    val stringBuilder =
//                        "지역: " + weatherResponse!!.sys!!.country + "\n" +
//                                "현재기온: " + cTemp + "\n" +
//                                "최저기온: " + minTemp + "\n" +
//                                "최고기온: " + maxTemp + "\n" +
//                                "풍속: " + weatherResponse!!.wind!!.speed + "\n" +
//                                "일출시간: " + weatherResponse!!.sys!!.sunrise + "\n" +
//                                "일몰시간: " + weatherResponse!!.sys!!.sunset + "\n" +
//                                "아이콘: " + weatherResponse!!.weather!!.get(0).icon + "\n"
//                    Log.d(TAG, "WeatherFragment, $stringBuilder")
                    thermometerText.text = cTemp.toString() + "℃"
                    humidityText.text = hum.toString() + "%"
                }
            }

            //            통신 실패
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                var dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle("실패!")
            }
        })
    }

//
//    override fun onPause() {
//        super.onPause()
////        if(mLocationManager!=null){
////            mLocationManager.removeUpdates(mLocationListener)
////        }
//    }


//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment BlankFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            WeatherFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}

