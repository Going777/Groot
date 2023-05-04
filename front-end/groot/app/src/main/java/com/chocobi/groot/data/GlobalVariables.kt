package com.chocobi.groot.data

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.view.login.LoginActivity
import com.chocobi.groot.view.login.LoginRequest
import com.chocobi.groot.view.user.model.GetUserResponse
import com.chocobi.groot.view.user.model.RefreshRequest
import com.chocobi.groot.view.user.model.RefreshResponse
import com.chocobi.groot.view.user.model.UserService
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GlobalVariables : Application() {
    companion object {
        lateinit var prefs: PreferenceUtil
        
        private val TAG = "GlobalVariables"

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
                            prefs.setString("access_token", refreshBody?.accessToken.toString())
                            getUser()
                        } else {
                            var errMsg =  "$refreshBody"
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
                    }
                })

        }

        fun changeImgView(imgView: ImageView, userProfile:String, context: Context) {
            imgView.post {
                ThreadUtil.startThread {
                    val futureTarget: FutureTarget<Bitmap> = Glide.with(context)
                        .asBitmap()
                        .load(userProfile)
                        .submit(imgView.width, imgView.height)

                    val bitmap = futureTarget.get()

                    ThreadUtil.startUIThread(0) {
                        imgView.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        prefs = PreferenceUtil(applicationContext)
        var accessToken = prefs.getString("access_token", "")
        if (accessToken != "") {
            getUser()
            var refreshToken = prefs.getString("refresh_token", "")
            if (refreshToken == "") {
                var intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}