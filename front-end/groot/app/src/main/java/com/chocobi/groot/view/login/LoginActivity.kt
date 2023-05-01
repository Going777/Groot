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
import com.chocobi.groot.data.TokenInterceptor
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

//        sharedPreference
        val shared = getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
        val editor = shared.edit() // 수정을 위한 에디터

//        초기 토큰값 확인
//        var access_token = shared.getString("access_token", "초기값")
//        var dialog = AlertDialog.Builder(this@LoginActivity)
//        dialog.setTitle("초기값")
//        dialog.setMessage(access_token)
//        dialog.show()


//        retrofit 객체 만들기
        var retrofit = Retrofit.Builder()
            .baseUrl(GlobalVariables.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

//        service 객체 만들기
        var loginService = retrofit.create(LoginService::class.java)
        var userService = retrofit.create(UserService::class.java)


        var loginIdInput = findViewById<EditText>(R.id.loginIdInput)
        var loginPwInput = findViewById<EditText>(R.id.loginPwInput)
        val basicLoginBtn = findViewById<Button>(R.id.basicLoginBtn)
        val toSignupText = findViewById<TextView>(R.id.toSignupText)


//        로그인 버튼 클릭시
        basicLoginBtn.setOnClickListener {
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
                            var login = response.body()

//                            var dialog = AlertDialog.Builder(this@LoginActivity)
//                            dialog.setTitle("알림!")

//                    토큰 저장
                            if (login != null) {

                                GlobalVariables.prefs.setString("access_token", login.accessToken)
                                GlobalVariables.prefs.setString("refresh_token", login.accessToken)
//
//                            editor.putString("access_token", login.accessToken)
//                            editor.putString("refresh_token", login.refreshToken)
//                            editor.commit()

                            userService.getUser("Bearer " + login.accessToken).enqueue(object : Callback<GetUserResponse> {
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
                                    }
                                }

                                override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                                    var dialog = AlertDialog.Builder(this@LoginActivity)
                                    dialog.setTitle("실패!")
                                    dialog.setMessage(t.message)
                                    dialog.show()
                                }
                            })
                            }

//                    토큰 확인
//                    access_token = shared.getString("access_token", "")
//                    dialog.setMessage(access_token)
//                    dialog.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
//                        var intent = Intent(this@LoginActivity, MainActivity::class.java)
//                        startActivity(intent)
//                    })
//                    dialog.show()
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
}