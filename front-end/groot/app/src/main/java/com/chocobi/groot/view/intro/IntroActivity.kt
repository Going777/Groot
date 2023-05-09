package com.chocobi.groot.view.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.view.login.LoginActivity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

class IntroActivity : AppCompatActivity() {
    private val TAG = "IntroActivity"
    private var accessToken: String? = null
    private var refreshToken: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)


//        token 갱신
        val tokenAction = CoroutineScope(Dispatchers.IO).async {
            accessToken = GlobalVariables.prefs.getString("access_token", "")
        }

        val refreshAction = CoroutineScope(Dispatchers.IO).async {
            refreshToken = GlobalVariables.prefs.getString("refresh_token", "")
        }

        val userAction = CoroutineScope(Dispatchers.Main).launch {
            tokenAction.await()
            if (accessToken != "") {
                GlobalVariables.getUser()
            } else {
                refreshAction.await()
            }
        }

        refreshToken = GlobalVariables.prefs.getString("refresh_token", "")

//        화분 정보 받아왔는지 체크
        val isExistPlantData = GlobalVariables.prefs.getString("plant_names", "")
        if (isExistPlantData == "") {
//        화분 이름 받아오기
            getPlantNameList()
//            지역 받아오기
            getRegionNameList()
        } else {
            GlobalVariables.prefs.setString("plant_names", "")
            Toast.makeText(this, "초기화", Toast.LENGTH_SHORT).show()
        }

//        시작하기 버튼 클릭시 -> 로그인 화면으로
        var toLoginBtn = findViewById<Button>(R.id.toLoginbtn)
        toLoginBtn.setOnClickListener {
            goToLogin()
        }
    }

    private fun goToLogin() {
        if (refreshToken == "" || refreshToken == null) {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getPlantNameList() {
        val retrofit = Retrofit.Builder()
            .baseUrl(GlobalVariables.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val IntroDataService = retrofit.create(IntroDataService::class.java)

//        요청 보내기
        IntroDataService.requestPlantNames().enqueue(object : Callback<PlantNamesResponse> {
            //            요청 성공
            override fun onResponse(
                call: Call<PlantNamesResponse>,
                response: Response<PlantNamesResponse>
            ) {
                if (response.code() == 200) {
                    val plantNameBody = response.body()

                    Log.d("IntroActivity", "onResponse(), 식물 $plantNameBody")
//                    전역 변수에 식물 이름 리스트 저장
                    if (plantNameBody != null) {
                        val plantNames = plantNameBody.nameList.joinToString()
                        GlobalVariables.prefs.setString("plant_names", plantNames)
                    }
                }
            }

            //            요청 실패
            override fun onFailure(call: Call<PlantNamesResponse>, t: Throwable) {
                Log.d(TAG, "onFailure() 식물 이름 가져오기")
            }
        })
    }

    private fun getRegionNameList() {
        val retrofit = Retrofit.Builder()
            .baseUrl(GlobalVariables.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val IntroDataService = retrofit.create(IntroDataService::class.java)

//        요청 보내기
        IntroDataService.requestRegionNames().enqueue(object : Callback<RegionNameResponse> {
            //            요청 성공
            override fun onResponse(
                call: Call<RegionNameResponse>,
                response: Response<RegionNameResponse>
            ) {
                if (response.code() == 200) {
                    val regionNameBody = response.body()

                    Log.d("IntroActivity", "지역 onResponse() / $regionNameBody")
//                    전역 변수에 지역 리스트 저장
                    if (regionNameBody != null) {
                        val plantNames = regionNameBody.regions.joinToString()
                        GlobalVariables.prefs.setString("region_names", plantNames)
                    }
                }
            }

            //            요청 실패
            override fun onFailure(call: Call<RegionNameResponse>, t: Throwable) {
                Log.d("IntroActivity", "onFailure() 지역 가져오기")
            }
        })
    }
}