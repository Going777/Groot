package com.chocobi.groot.data

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.chocobi.groot.view.login.LoginRequest
import com.chocobi.groot.view.user.model.GetUserResponse
import com.chocobi.groot.view.user.model.RefreshRequest
import com.chocobi.groot.view.user.model.RefreshResponse
import com.chocobi.groot.view.user.model.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GlobalVariables : Application() {
    companion object {
        lateinit var prefs: PreferenceUtil

        private var BASE_URL = "https://k8a303.p.ssafy.io"

        fun getBaseUrl(): String {
            return BASE_URL
        }

        fun setBaseUrl(url: String) {
            BASE_URL = url
        }

        fun getUser() {
            var retrofit = RetrofitClient.getClient()!!
            var userService = retrofit.create(UserService::class.java)

            userService.getUser().enqueue(object : Callback<GetUserResponse> {
                override fun onResponse(
                    call: Call<GetUserResponse>,
                    response: Response<GetUserResponse>
                ) {
                    var getUser = response.body()
                    Log.d("LoginActivity", getUser?.msg.toString())

                    if (getUser?.user != null) {
                        UserData.setId(getUser.user.id)
                        UserData.setUserId(getUser.user.userId)
                        UserData.setNickName(getUser.user.nickName)
                        UserData.setProfile(getUser.user.profile)
                        UserData.setRegisterDate(getUser.user.registerDate)
                    } else {
                        refresh()
                    }
                }

                override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                    Log.d("GlobalVariables", "getuser 실패")
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

            userService.refresh(RefreshRequest(accessToken, refreshToken))
                .enqueue(object : Callback<RefreshResponse> {
                    override fun onResponse(
                        call: Call<RefreshResponse>,
                        response: Response<RefreshResponse>
                    ) {
                        var refresh = response.body()
                        Log.d("LoginActivity", refresh?.msg.toString())
                        if (refresh != null) {
                            prefs.setString("access_token", refresh?.accessToken.toString())
                            getUser()
                        } else {
                            prefs.setString("access_token", "")
                            prefs.setString("refresh_token", "")
                        }
                    }

                    override fun onFailure(call: Call<RefreshResponse>, t: Throwable) {
                        Log.d("GlobalVariables", "refresh 실패")
                    }
                })

        }
    }


    override fun onCreate() {
        super.onCreate()
        prefs = PreferenceUtil(applicationContext)
    }
}