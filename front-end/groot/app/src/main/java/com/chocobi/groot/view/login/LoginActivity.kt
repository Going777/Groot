package com.chocobi.groot.view.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.signup.SignupActivity
import com.chocobi.groot.view.user.model.GetUserResponse
import com.chocobi.groot.view.user.model.UserService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val basicLoginBtn = findViewById<Button>(R.id.basicLoginBtn)
        val toSignupText = findViewById<TextView>(R.id.toSignupText)

//        로그인 버튼 클릭시
        basicLoginBtn.setOnClickListener {
            login()
        }

//        회원가입 안내 텍스트 클릭시
        toSignupText.setOnClickListener {
            var intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        //        회원가입 안내 텍스트 클릭시
        toSignupText.setOnClickListener {
            var intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        //        retrofit 객체 만들기
        var retrofit = Retrofit.Builder()
            .baseUrl(GlobalVariables.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

//        service 객체 만들기
        var loginService = retrofit.create(LoginService::class.java)

        var loginIdInput = findViewById<EditText>(R.id.loginIdInput)
        var loginPwInput = findViewById<EditText>(R.id.loginPwInput)
        var textId = loginIdInput.text.toString()
        var textPw = loginPwInput.text.toString()

        //            로그인 요청 보내기
        loginService.requestLogin(LoginRequest(textId, textPw))
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.code() == 200) {
                        Log.d("LoginActivity", "로그인 성공")

//                    통신 성공시 실행되는 코드
                        var loginBody = response.body()

//                    토큰 저장
                        if (loginBody != null) {
                            GlobalVariables.prefs.setString("access_token", loginBody.accessToken)
                            GlobalVariables.prefs.setString("refresh_token", loginBody.accessToken)
                            GlobalVariables.getUser()

                        }

                        var intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.d("LoginActivity", response.code().toString())
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                    통신 실패시 실행되는 코드
                    var dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("실패!")
                    dialog.setMessage(t.message)
                    dialog.show()
                }
            })
    }
}