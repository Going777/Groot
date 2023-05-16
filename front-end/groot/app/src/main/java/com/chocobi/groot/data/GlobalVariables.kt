package com.chocobi.groot.data

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.BuildConfig
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.view.login.LoginActivity
import com.chocobi.groot.view.login.LoginRequest
import com.chocobi.groot.view.user.model.GetUserResponse
import com.chocobi.groot.view.user.model.RefreshRequest
import com.chocobi.groot.view.user.model.RefreshResponse
import com.chocobi.groot.view.user.model.UserService
import com.kakao.sdk.common.KakaoSdk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GlobalVariables : Application() {
    companion object {
        lateinit var prefs: PreferenceUtil

        private val TAG = "GlobalVariables"

        private var BASE_URL = "https://k8a303.p.ssafy.io"
        private var accessToken: String? = null
        private var refreshToken: String? = null

        fun getBaseUrl(): String {
            return BASE_URL
        }



        fun fetchUserData() {
            val fetchGetUser = CoroutineScope(Dispatchers.Main).async {
                getUser()
            }
        }

        fun getUser() {
            var retrofit = RetrofitClient.getClient()!!
            var userService = retrofit.create(UserService::class.java)

            userService.getUser().enqueue(object : Callback<GetUserResponse> {
                override fun onResponse(
                    call: Call<GetUserResponse>,
                    response: Response<GetUserResponse>
                ) {
                    var getUserBody = response.body()
                    Log.d(TAG, getUserBody?.msg.toString())
                    if (getUserBody?.user != null) {
                        UserData.setUserPK(getUserBody.user.userPK)
                        UserData.setUserId(getUserBody.user.userId)
                        UserData.setNickName(getUserBody.user.nickName)
                        UserData.setProfile(getUserBody.user.profile)
                        UserData.setRegisterDate(getUserBody.user.registerDate)
                    } else {
                        refresh()
                    }
                }

                override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                    Log.d(TAG, "getuser 실패")
                    refresh()
                }
            })
        }

        fun refresh() {
            var retrofit = Retrofit.Builder()
                .baseUrl(GlobalVariables.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            var userService = retrofit.create(UserService::class.java)

            val accessToken = prefs.getString("access_token", "")
            val refreshToken = prefs.getString("refresh_token", "")

            userService.refresh(RefreshRequest("string", accessToken, refreshToken))
                .enqueue(object : Callback<RefreshResponse> {
                    override fun onResponse(
                        call: Call<RefreshResponse>,
                        response: Response<RefreshResponse>
                    ) {
                        var refreshBody = response.body()
                        if (refreshBody != null) {
                            Log.d(TAG, refreshBody?.msg.toString())
                            val tokenAction = CoroutineScope(Dispatchers.Main).launch {
                                prefs.setString("access_token", refreshBody?.accessToken.toString())
                            }
                            val userAction = CoroutineScope(Dispatchers.Main).launch {
                                getUser()
                            }
                        } else {
                            var errMsg = "$refreshBody"
                            try {
                                errMsg = JSONObject(response.errorBody()?.string()).let { json ->
                                    json.getString("msg")
                                }
                            } catch (e: JSONException) {
                                // 예외 처리: msg 속성이 존재하지 않는 경우
                                e.printStackTrace()
                            }

                            Log.d(TAG, errMsg.toString())
                            prefs.setString("access_token", "")
                            prefs.setString("refresh_token", "")
                        }
                    }

                    override fun onFailure(call: Call<RefreshResponse>, t: Throwable) {
                        Log.d(TAG, "refresh 실패")
                        prefs.setString("access_token", "")
                        prefs.setString("refresh_token", "")
                    }
                })

        }


        //    키보드 내리기
        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.window.decorView.applicationWindowToken, 0)
        }

        fun changeImgView(imgView: ImageView, imgUrl: String, context: Context) {
            imgView.post {
                ThreadUtil.startThread {
                    val futureTarget: FutureTarget<Bitmap> = Glide.with(context)
                        .asBitmap()
                        .load(imgUrl)
                        .submit(imgView.width, imgView.height)

                    val bitmap = futureTarget.get()

                    ThreadUtil.startUIThread(0) {
                        imgView.setImageBitmap(bitmap)
                    }
                }
            }
        }

        fun defaultAlertDialog(context: Context, title: String? = null, message: String? = null, positiveFtn: (() -> Unit)? = null, existNegativeBtn: Boolean = false) {
            var dialog = AlertDialog.Builder(context)
            dialog.setTitle(title)
            dialog.setMessage(message)
            dialog.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                positiveFtn?.invoke()
            })
            if(existNegativeBtn) {
                dialog.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
            }
            dialog.show()
        }

        /** 날씨 관련 데이터 **/
        data class WeatherData(
            val weatherType: String,
            val temperature: Int,
            val humidity: Int,
        )

        private var current = LocalDateTime.now()
        private var dateFormatter = DateTimeFormatter.ofPattern("M월 d일")
        private var currentDate = current.format(dateFormatter)
        private var weatherType: String = "sun"
        private var temperature: Int = 15
        private var humidity: Int = 20

        fun getCurrentDate(): String {
            return currentDate
        }

        fun getWeatherData(): WeatherData {
            return WeatherData(weatherType, temperature, humidity)
        }

        fun updateWeatherData(type: String, temp: Int, hum: Int) {
            weatherType = type
            temperature = temp
            humidity = hum
        }
    }


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        // Kakao SDK 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)

        prefs = PreferenceUtil(applicationContext)


//        val tokenAction = CoroutineScope(Dispatchers.IO).async {
//            accessToken = prefs.getString("access_token", "")
//        }
//
//        val refreshAction = CoroutineScope(Dispatchers.IO).async {
//            refreshToken = prefs.getString("refresh_token", "")
//        }
//
//        val userAction = CoroutineScope(Dispatchers.Main).launch {
//            tokenAction.await()
//            if (accessToken != "") {
//                getUser()
//            } else {
//                refreshAction.await()
//            }
//        }
    }

}