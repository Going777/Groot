package com.chocobi.groot.view.intro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.login.LoginActivity
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class IntroActivity : AppCompatActivity() {
    private val TAG = "IntroActivity"
    private var accessToken: String? = null
    private var refreshToken: String? = null


    //    뒤로가기 조작
    private var backPressedTime: Long = 0
    private val backPressedInterval: Long = 2000 // 두 번째 뒤로가기 클릭 간의 시간 간격 (여기서는 2초로 설정)

    override fun onBackPressed() {
        if (System.currentTimeMillis() - backPressedTime < backPressedInterval) {
            // 뒤로가기 버튼이 두 번 연속으로 클릭되었을 때 앱 종료
            finishAffinity()
        } else {
            // 첫 번째 뒤로가기 클릭
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        //        firebase 토큰 확인
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                token ?: ""
                UserData.setUserFirebase(token)
            }


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

////        화분 정보 받아왔는지 체크
//        val isExistPlantData = GlobalVariables.prefs.getString("plant_names", "")
//        if (isExistPlantData == "") {
////        화분 이름 받아오기
//            getPlantNameList()
////            지역 받아오기
//            getRegionNameList()
//        } else {
//            GlobalVariables.prefs.setString("plant_names", "")
//            Toast.makeText(this, "초기화", Toast.LENGTH_SHORT).show()
//        }

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

//    private fun getPlantNameList() {
//        val retrofit = Retrofit.Builder()
//            .baseUrl(GlobalVariables.getBaseUrl())
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val IntroDataService = retrofit.create(IntroDataService::class.java)
//
////        요청 보내기
//        IntroDataService.requestPlantNames().enqueue(object : Callback<PlantNamesResponse> {
//            //            요청 성공
//            override fun onResponse(
//                call: Call<PlantNamesResponse>,
//                response: Response<PlantNamesResponse>
//            ) {
//                if (response.code() == 200) {
//                    val plantNameBody = response.body()
//
//                    Log.d("IntroActivity", "onResponse(), 식물 $plantNameBody")
////                    전역 변수에 식물 이름 리스트 저장
//                    if (plantNameBody != null) {
//                        val plantNames = plantNameBody.nameList.joinToString()
//                        GlobalVariables.prefs.setString("plant_names", plantNames)
//                    }
//                }
//            }
//
//            //            요청 실패
//            override fun onFailure(call: Call<PlantNamesResponse>, t: Throwable) {
//                Log.d(TAG, "onFailure() 식물 이름 가져오기")
//            }
//        })
//    }
//
//    private fun getRegionNameList() {
//        val retrofit = Retrofit.Builder()
//            .baseUrl(GlobalVariables.getBaseUrl())
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val IntroDataService = retrofit.create(IntroDataService::class.java)
//
////        요청 보내기
//        IntroDataService.requestRegionNames().enqueue(object : Callback<RegionNameResponse> {
//            //            요청 성공
//            override fun onResponse(
//                call: Call<RegionNameResponse>,
//                response: Response<RegionNameResponse>
//            ) {
//                if (response.code() == 200) {
//                    val regionNameBody = response.body()
//
//                    Log.d("IntroActivity", "지역 onResponse() / $regionNameBody")
////                    전역 변수에 지역 리스트 저장
//                    if (regionNameBody != null) {
//                        val plantNames = regionNameBody.regions.joinToString()
//                        GlobalVariables.prefs.setString("region_names", plantNames)
//                    }
//                }
//            }
//
//            //            요청 실패
//            override fun onFailure(call: Call<RegionNameResponse>, t: Throwable) {
//                Log.d("IntroActivity", "onFailure() 지역 가져오기")
//            }
//        })
//    }
}