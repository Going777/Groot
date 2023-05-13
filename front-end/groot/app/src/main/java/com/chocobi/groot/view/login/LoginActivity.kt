package com.chocobi.groot.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.signup.SignupActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private lateinit var loginIdInput: EditText
    private lateinit var loginPwInput: EditText
    private lateinit var loginIdInputImg: ImageView
    private lateinit var loginPwInputImg: ImageView
    private lateinit var overlayView: View
    private var textId: String = ""
    private var textPw: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val basicLoginBtn = findViewById<Button>(R.id.basicLoginBtn)
        val toSignupText = findViewById<TextView>(R.id.toSignupText)
        loginIdInput = findViewById(R.id.loginIdInput)
        loginPwInput = findViewById(R.id.loginPwInput)
        loginIdInputImg = findViewById(R.id.loginIdInputImg)
        loginPwInputImg = findViewById(R.id.loginPwInputImg)
//        overlayView = findViewById(R.id.overlayView)

        checkEditText(loginIdInput, loginIdInputImg)
        checkEditText(loginPwInput, loginPwInputImg)

//        로그인 버튼 클릭시
        basicLoginBtn.setOnClickListener {
            Log.d("LoginActivity", "onCreate() ${textId}// 아이디")
            Log.d("LoginActivity", "onCreate() ${textPw}// 비번")
//            아이디 입력 안했을 때
            if (textId.isBlank()) {
                GlobalVariables.defaultAlertDialog(context = this, message = "아이디를 입력해주세요.")
//            비밀번호 입력 안했을 때
            } else if (textPw.isBlank()) {
                GlobalVariables.defaultAlertDialog(context = this, message = "비밀번호를 입력해주세요.")
//            로그인 함수 실행
            } else {
                login(this)
            }
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

    private fun checkEditText(editText: EditText, imageView: ImageView) {
        var message = ""
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                message = editText.text.toString()
//            텍스트가 있으면 새싹 색깔 point로 변경
                if (message.isNotEmpty()) {
                    imageView.setColorFilter(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.point
                        )
                    )
//            텍스트가 비면 새싹 색깔 grey로 변경
                } else {
                    imageView.setColorFilter(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.grey
                        )
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {
                when (editText) {
                    loginIdInput -> textId = editText.text.toString()
                    loginPwInput -> textPw = editText.text.toString()
                }
            }

        })

//        editText.setOnFocusChangeListener { v, hasFocus ->
//            if (hasFocus) {
//                overlayView.visibility = View.VISIBLE
//            } else {
//                overlayView.visibility = View.GONE
//            }
//        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Enter(또는 Done) 키가 눌렸을 때 수행할 동작
//                overlayView.visibility = View.GONE
                GlobalVariables.hideKeyboard(this)
                true
            } else {
                false
            }
        }

//        overlayView.setOnTouchListener { v, event ->
//            overlayView.visibility = View.GONE
//            GlobalVariables.hideKeyboard(this)
//            true
//        }
    }

    private fun login(context: Context) {
        //        retrofit 객체 만들기
        var retrofit = RetrofitClient.basicClient()!!

//        service 객체 만들기
        var loginService = retrofit.create(LoginService::class.java)

        textId = loginIdInput.text.toString()
        textPw = loginPwInput.text.toString()

        //            로그인 요청 보내기
        loginService.requestLogin(LoginRequest(textId, textPw))
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.code() == 200) {
                        Log.d(TAG, "로그인 성공")

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
                        GlobalVariables.defaultAlertDialog(context = context, message = "등록되지 않은 아이디이거나\n아이디 또는 비밀번호를 잘못 입력했습니다.")
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

//    private fun requestSubscribe() {
//        val retrofit = RetrofitClient.getClient()!!
//        val loginService = retrofit.create(LoginService::class.java)
//        loginService.requestSubscribe().enqueue(object : Callback<SubscribeResponse> {
//            override fun onResponse(
//                call: Call<SubscribeResponse>,
//                response: Response<SubscribeResponse>
//            ) {
//                Log.d("LoginActivity", "onResponse() $response")
//                if (response.code() == 200) {
//
//                    Log.d("LoginActivity", "onResponse() 구독 요청 성공 $response")
//                } else {
//
//                    Log.d("LoginActivity", "onResponse() 구독 요청 실패1 $response")
//                }
//            }
//
//            override fun onFailure(call: Call<SubscribeResponse>, t: Throwable) {
//                Log.d("LoginActivity", "onResponse() 구독 요청 실패2")
//            }
//
//        })
//    }
}