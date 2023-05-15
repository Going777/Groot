package com.chocobi.groot.view.signup

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignupActivity : AppCompatActivity() {
    private val TAG = "SignupActivity"
    private var isCheckedDupId = false
    private var isCheckedDupName = false

    private lateinit var signupIdInput: EditText
    private lateinit var signupNameInput: EditText
    private lateinit var signupPwInput: EditText
    private lateinit var signupConfPwInput: EditText
    private lateinit var signupIdInputImg: ImageView
    private lateinit var signupNameInputImg: ImageView
    private lateinit var signupPwInputImg: ImageView
    private lateinit var signupConfPwInputImg: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

//        ================================================================
//        ================================================================
//        뒤로 가기 버튼 처리해야 하는 곳
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            this.onBackPressed()
        }
//        ================================================================
//        ================================================================


        signupIdInput = findViewById(R.id.signupIdInput)
        signupNameInput = findViewById(R.id.signupNameInput)
        signupPwInput = findViewById(R.id.signupPwInput)
        signupConfPwInput = findViewById(R.id.signupConfPwInput)
        signupIdInputImg = findViewById(R.id.signupIdInputImg)
        signupNameInputImg = findViewById(R.id.signupNameInputImg)
        signupPwInputImg = findViewById(R.id.signupPwInputImg)
        signupConfPwInputImg = findViewById(R.id.signupConfPwInputImg)
        val dupIdBtn = findViewById<Button>(R.id.dupIdBtn)
        val dupNameBtn = findViewById<Button>(R.id.dupNameBtn)
        val basicSignupBtn = findViewById<Button>(R.id.basicSignupBtn)

        checkEditText(signupIdInput, signupIdInputImg)
        checkEditText(signupNameInput, signupNameInputImg)
        checkEditText(signupPwInput, signupPwInputImg)
        checkEditText(signupConfPwInput, signupConfPwInputImg)

        dupIdBtn.setOnClickListener {
            GlobalVariables.hideKeyboard(this)
            var textId = signupIdInput.text.toString()
            checkDupId(textId, this)
        }

        dupNameBtn.setOnClickListener {
            GlobalVariables.hideKeyboard(this)
            var textName = signupNameInput.text.toString()
            checkDupName(textName)
        }

        basicSignupBtn.setOnClickListener {
            GlobalVariables.hideKeyboard(this)
            var textId = signupIdInput.text.toString()
            var textName = signupNameInput.text.toString()
            var textPw = signupPwInput.text.toString()
            var textConfPw = signupConfPwInput.text.toString()
            var textProfile = ""

            when {
                !isCheckedDupId -> {
                    GlobalVariables.defaultAlertDialog(
                        context = this,
                        message = "아이디 중복 여부를 확인해주세요."
                    )
                }

                !isCheckedDupName -> {
                    GlobalVariables.defaultAlertDialog(
                        context = this,
                        message = "닉네임 중복 여부를 확인해주세요."
                    )
                }

                textPw != textConfPw -> {
                    GlobalVariables.defaultAlertDialog(context = this, message = "비밀번호를 확인해주세요.")
                }

                else -> {
                    signup(textId, textName, textPw)
                }
            }
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
//                when (editText) {
//                    loginIdInput -> textId = editText.text.toString()
//                    loginPwInput -> textPw = editText.text.toString()
//                }
            }

        })

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
    }

    private fun checkDupId(textId: String, context: Context) {
        if (textId == null) {
            return
        }
        var retrofit = RetrofitClient.basicClient()!!
        //        id 중복체크
        //        service 객체 만들기
        var dupIdService = retrofit.create(DupIdService::class.java)

        dupIdService.requestDupId(textId)
            .enqueue(object : Callback<DupIdResponse> {
                override fun onResponse(
                    call: Call<DupIdResponse>,
                    response: Response<DupIdResponse>
                ) {
                    var checkDupIdMsg = response.body()?.msg
                    if (checkDupIdMsg == null) {

                        try {
                            JSONObject(response.errorBody()?.string()).let { json ->
                                checkDupIdMsg = json.getString("msg")
                            }
                        } catch (e: JSONException) {
                            // 예외 처리: msg 속성이 존재하지 않는 경우
                            checkDupIdMsg = "아이디를 입력해주세요."
                            e.printStackTrace()
                        }

                    } else {
                        isCheckedDupId = true
                    }
                    Log.d(TAG, response.toString())
                    GlobalVariables.defaultAlertDialog(
                        context = context,
                        title = "아이디 중복 체크",
                        message = checkDupIdMsg
                    )
                }

                override fun onFailure(call: Call<DupIdResponse>, t: Throwable) {
                    GlobalVariables.defaultAlertDialog(
                        context = context,
                        title = "아이디 중복 체크 실패",
                        message = t.message
                    )
                }
            })

    }

    private fun checkDupName(textName: String) {
        if (textName == "") {
            return
        }
        //        retrofit 객체 만들기
        var retrofit = RetrofitClient.basicClient()!!
        var dupNameService = retrofit.create(DupNameService::class.java)
        dupNameService.requestDupName(textName)
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
                        context = this@SignupActivity,
                        title = "닉네임 중복 체크",
                        message = checkDupNameMsg
                    )
                }

                override fun onFailure(call: Call<DupNameResponse>, t: Throwable) {
                    GlobalVariables.defaultAlertDialog(
                        context = this@SignupActivity,
                        title = "닉네임 중복 체크 실패",
                        message = t.message
                    )
                }
            })

    }

    private fun signup(textId: String, textName: String, textPw: String) {
        //        retrofit 객체 만들기
        var retrofit = RetrofitClient.basicClient()!!
        var signupService = retrofit.create(SignupService::class.java)

        signupService.requestSignup(SignupRequest(textId, textName, textPw))
            .enqueue(object : Callback<SignupResponse> {
                override fun onResponse(
                    call: Call<SignupResponse>,
                    response: Response<SignupResponse>
                ) {
                    var signupBody = response.body()
                    var signupMsg = signupBody?.msg
                    if (response.code() == 200 && signupBody?.accessToken != null) {

                        //                    토큰 저장
                        GlobalVariables.prefs.setString("access_token", signupBody.accessToken)
                        GlobalVariables.prefs.setString("refresh_token", signupBody.refreshToken)
                        GlobalVariables.getUser()
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
                                response.errorBody()?.string()
                            )?.getString("msg")
                        Log.d(TAG, response.code().toString() + signupMsg)
                    }
                }

                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                    GlobalVariables.defaultAlertDialog(this@SignupActivity, "회원가입 실패", t.message)
                }
            })

    }
}