package com.chocobi.groot.view.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.kakao.sdk.common.util.Utility
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SocialSignupActivity : AppCompatActivity() {
    private lateinit var basicSignupBtn: Button
    private lateinit var dupNameBtn: Button

    private var nickname: String? = null
    private var profileImg: String? = null
    private var social_access_token: String? = null
    private var type: String? = null

    private var isCheckedDupName = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_signup)

        getData()
        findView()
        signup()
    }

    private fun getData() {
        nickname = intent.getStringExtra("nickname")
        profileImg = intent.getStringExtra("profileImg")
        social_access_token = intent.getStringExtra("social_access_token")
        type = intent.getStringExtra("type")
    }

    private fun findView(){
        basicSignupBtn = findViewById(R.id.basicSignupBtn)
        dupNameBtn = findViewById(R.id.dupNameBtn)
    }

    private fun signup() {
        basicSignupBtn.setOnClickListener {
            GlobalVariables.defaultAlertDialog(this@SocialSignupActivity, title = "로그인 성공", "반갑습니다!!")
        }
    }

    private fun checkDupNameBtn() {
        dupNameBtn.setOnClickListener {
            GlobalVariables.hideKeyboard(this)
            if (!nickname.isNullOrEmpty()) {
                checkDupName(nickname!!)
            }
        }
    }

    private fun checkDupName(nickname: String) {
        if (nickname == "") {
            return
        }
        //        retrofit 객체 만들기
        var retrofit = RetrofitClient.basicClient()!!
        var dupNameService = retrofit.create(DupNameService::class.java)
        dupNameService.requestDupName(nickname)
            .enqueue(object : Callback<DupNameResponse> {
                override fun onResponse(
                    call: Call<DupNameResponse>,
                    response: Response<DupNameResponse>
                ) {
                    var checkDupNameMsg = response.body()?.msg
                    if (checkDupNameMsg == null) {
                        try {
                            JSONObject(response.errorBody()?.string()).let { json ->
                                checkDupNameMsg = json.getString("msg")
                            }
                        } catch (e: JSONException) {
                            // 예외 처리: msg 속성이 존재하지 않는 경우
                            checkDupNameMsg = "닉네임을 입력해주세요."
                            e.printStackTrace()
                        }
                    } else {
                        isCheckedDupName = true
                    }
                    GlobalVariables.defaultAlertDialog(
                        context = this@SocialSignupActivity,
                        title = "닉네임 중복 체크",
                        message = checkDupNameMsg
                    )
                }

                override fun onFailure(call: Call<DupNameResponse>, t: Throwable) {
                    GlobalVariables.defaultAlertDialog(
                        context = this@SocialSignupActivity,
                        title = "닉네임 중복 체크 실패",
                        message = t.message
                    )
                }
            })
    }
}