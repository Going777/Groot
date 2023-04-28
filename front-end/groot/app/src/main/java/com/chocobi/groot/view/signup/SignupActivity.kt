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
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignupActivity : AppCompatActivity() {
    var isCheckedDupId = false
    var isCheckedDupName = false
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
        var dupIdBtn = findViewById<Button>(R.id.dupIdBtn)
        var dupNameBtn = findViewById<Button>(R.id.dupNameBtn)
        var basicSignupBtn = findViewById<Button>(R.id.basicSignupBtn)


        //        retrofit 객체 만들기
        var retrofit = Retrofit.Builder()
            .baseUrl(GlobalVariables.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        //        id 중복체크
        //        service 객체 만들기
        var dupIdService = retrofit.create(DupIdService::class.java)
        dupIdBtn.setOnClickListener {
            var textId = signupIdInput.text.toString()
            dupIdService.requestDupId(textId)
                .enqueue(object : Callback<DupIdResponse> {
                    override fun onResponse(
                        call: Call<DupIdResponse>,
                        response: Response<DupIdResponse>
                    ) {
                        var checkDupId = response.body()?.msg
                        if (checkDupId == null) {
                            checkDupId =
                                JSONObject(response.errorBody()?.string()!!).getString("msg")
                        } else {
                            isCheckedDupId = true
                        }
                        var dialog = AlertDialog.Builder(
                            this@SignupActivity,
                            android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                        )
                        Log.d("SignupActivity", response.toString())
                        dialog.setTitle("아이디 중복 체크")
                        dialog.setMessage(checkDupId)
                        dialog.setPositiveButton(
                            "확인",
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                            })
                        dialog.show()
                    }

                    override fun onFailure(call: Call<DupIdResponse>, t: Throwable) {
                        var dialog = AlertDialog.Builder(this@SignupActivity)
                        dialog.setTitle("아이디 중복체크 실패")
                        dialog.setMessage(t.message)
                        dialog.show()
                    }
                })

        }


        //        닉네임 중복체크
        //        service 객체 만들기
        var dupNameService = retrofit.create(DupNameService::class.java)
        dupNameBtn.setOnClickListener {
            var textName = signupNameInput.text.toString()

            dupNameService.requestDupName(textName)
                .enqueue(object : Callback<DupNameResponse> {
                    override fun onResponse(
                        call: Call<DupNameResponse>,
                        response: Response<DupNameResponse>
                    ) {
                        var checkDupName = response.body()?.msg
                        if (checkDupName == null) {
                            checkDupName =
                                JSONObject(response.errorBody()?.string()!!).getString("msg")
                        } else {
                            isCheckedDupName = true
                        }
                        var dialog = AlertDialog.Builder(
                            this@SignupActivity,
                            android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                        )
                        Log.d("SignupActivity", response.toString())
                        dialog.setTitle("닉네임 중복 체크")
                        dialog.setMessage(checkDupName)
                        dialog.setPositiveButton(
                            "확인",
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                            })
                        dialog.show()
                    }

                    override fun onFailure(call: Call<DupNameResponse>, t: Throwable) {
                        var dialog = AlertDialog.Builder(this@SignupActivity)
                        dialog.setTitle("닉네임 중복체크 실패")
                        dialog.setMessage(t.message)
                        dialog.show()
                    }
                })
        }


//        회원가입
        //        service 객체 만들기
        var signupService = retrofit.create(SignupService::class.java)
        basicSignupBtn.setOnClickListener {
            var textId = signupIdInput.text.toString()
            var textName = signupNameInput.text.toString()
            var textPw = signupPwInput.text.toString()
            var textConfPw = signupConfPwInput.text.toString()
            var textProfile = ""

            var dialog = AlertDialog.Builder(this@SignupActivity)
            when {
                !isCheckedDupId -> {
                    dialog.setTitle("안내")
                    dialog.setMessage("아이디 중복 여부를 확인해주세요.")
                    dialog.show()
                }

                !isCheckedDupName -> {
                    dialog.setTitle("안내")
                    dialog.setMessage("닉네임 중복 여부를 확인해주세요.")
                    dialog.show()
                }

                textPw != textConfPw -> {
                    dialog.setTitle("안내")
                    dialog.setMessage("비밀번호를 확인해주세요.")
                    dialog.show()
                }

                else -> {
                    signupService.requestSignup(SignupRequest(textId, textName, textPw))
                        .enqueue(object : Callback<SignupResponse> {
                            override fun onResponse(
                                call: Call<SignupResponse>,
                                response: Response<SignupResponse>
                            ) {
                                var signup = response.body()
                                var signupMsg = signup?.msg
                                if (response.code() == 200) {


                                    //                    토큰 저장
                                    editor.putString("access_token", signup?.accessToken)
//                      editor.putString("refresh_token", login?.refreshToken)
                                    editor.commit()


                                    var dialog = AlertDialog.Builder(
                                        this@SignupActivity,
                                        android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                                    )

                                    dialog.setTitle("환영합니다!")
                                    dialog.setMessage(signupMsg)
                                    dialog.setPositiveButton(
                                        "확인",
                                        DialogInterface.OnClickListener { dialog, which ->
                                            var intent =
                                                Intent(
                                                    this@SignupActivity,
                                                    MainActivity::class.java
                                                )
                                            startActivity(intent)
                                        })
                                    dialog.show()
                                } else {
                                    signupMsg =
                                        JSONObject(
                                            response.errorBody()?.string()!!
                                        ).getString("msg")
                                    Log.d("SignupActivity", response.code().toString())
                                }


                            }

                            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                                var dialog = AlertDialog.Builder(this@SignupActivity)
                                dialog.setTitle("회원가입 실패")
                                dialog.setMessage(t.message)
                                dialog.show()
                            }
                        })
                }
            }


        }


    }
}