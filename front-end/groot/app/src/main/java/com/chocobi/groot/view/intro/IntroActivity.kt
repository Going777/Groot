package com.chocobi.groot.view.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
import android.util.Log
import android.widget.Button
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.view.login.LoginActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

//        화분 정보 받아왔는지 체크
        val isExistPlantData = GlobalVariables.prefs.getString("plant_names", "")
        if (isExistPlantData == "") {
//        화분 이름 받아오기
            getPlantNameList()
        }

//        시작하기 버튼 클릭시 -> 로그인 화면으로
        var toLoginBtn = findViewById<Button>(R.id.toLoginbtn)
        toLoginBtn.setOnClickListener {
            goToLogin()
        }
    }

    private fun goToLogin() {
        var intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun getPlantNameList() {
        val retrofit = Retrofit.Builder()
            .baseUrl(GlobalVariables.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val plantNamesService = retrofit.create(IntroDataService::class.java)

//        요청 보내기
        plantNamesService.requestPlantNames().enqueue(object : Callback<PlantNamesResponse> {
            //            요청 성공
            override fun onResponse(
                call: Call<PlantNamesResponse>,
                response: Response<PlantNamesResponse>
            ) {
                if (response.code() == 200) {
                    val plantNameBody = response.body()
                    val test = plantNameBody?.nameList

                    if (plantNameBody != null) {
                        val plantNames = plantNameBody.nameList.joinToString()
                        GlobalVariables.prefs.setString("plant_names", plantNames)
                    }
                }
            }

            //            요청 실패
            override fun onFailure(call: Call<PlantNamesResponse>, t: Throwable) {
                Log.d("로그", "IntroActivity 실패: $t")
            }
        })
    }
}