package com.chocobi.groot.view.signup

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.view.login.LoginActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //        sharedPreference
        val shared = getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
        val editor = shared.edit() // 수정을 위한 에디터

        var signupIdInput = findViewById<EditText>(R.id.signupIdInput)
        var signupNameInput = findViewById<EditText>(R.id.signupNameInput)
        var signupPwInput = findViewById<EditText>(R.id.signupPwInput)
        var signupConfPwInput = findViewById<EditText>(R.id.signupConfPwInput)
        var basicSignupBtn = findViewById<Button>(R.id.basicSignupBtn)


        //        retrofit 객체 만들기
        var retrofit = Retrofit.Builder()
            .baseUrl(GlobalVariables.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //        service 객체 만들기
        var signupService = retrofit.create(SignupService::class.java)

//        회원가입 버튼 클릭시
        basicSignupBtn.setOnClickListener {
            var textId = signupIdInput.text.toString()
            var textName = signupNameInput.text.toString()
            var textPw = signupPwInput.text.toString()
            var textConfPw = signupConfPwInput.text.toString()
            var textProfile = ""

            if (textPw == textConfPw) {
                signupService.requestSignup(SignupRequest(textId, textName, textPw))
//            signupService.requestSignup(textId, textName, textPw, textProfile)
                    .enqueue(object : Callback<Signup> {
                        override fun onResponse(call: Call<Signup>, response: Response<Signup>) {
                            if (response.code() == 200) {

                                var signup = response.body()

                                //                    토큰 저장
                                editor.putString("access_token", signup?.accessToken)
//                      editor.putString("refresh_token", login?.refreshToken)
                                editor.commit()

                                var dialog = AlertDialog.Builder(
                                    this@SignupActivity,
                                    android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                                )

                                dialog.setTitle("환영합니다!")
                                dialog.setMessage("회원가입이 완료되었습니다" + signup?.msg)
                                dialog.setPositiveButton(
                                    "확인",
                                    DialogInterface.OnClickListener { dialog, which ->
                                        var intent =
                                            Intent(this@SignupActivity, MainActivity::class.java)
                                        startActivity(intent)
                                    })
                                dialog.show()
                            } else {
                                Log.d("SignupActivity", response.code().toString())
                            }


                        }

                        override fun onFailure(call: Call<Signup>, t: Throwable) {
                            var dialog = AlertDialog.Builder(this@SignupActivity)
                            dialog.setTitle("회원가입 실패")
                            dialog.setMessage(t.message)
                            dialog.show()
                        }
                    })


            } else {
                var dialog = AlertDialog.Builder(this@SignupActivity)
                dialog.setTitle("안내")
                dialog.setMessage("비밀번호를 확인해주세요.")
                dialog.show()
            }

        }
    }
}