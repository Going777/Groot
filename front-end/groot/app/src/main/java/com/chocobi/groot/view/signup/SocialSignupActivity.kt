package com.chocobi.groot.view.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.login.LoginService
import com.chocobi.groot.view.login.SocialLoginRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SocialSignupActivity : AppCompatActivity() {
    private lateinit var basicSignupBtn: Button
    private lateinit var dupNameBtn: Button
    private lateinit var backBtn: ImageButton
    private lateinit var signupNameInputImg: ImageView
    private lateinit var signupNameInput: EditText

    private var nickname: String? = null
    private var profileImg: String? = null
    private var socialAccessToken: String? = null
    private lateinit var type: String

    private var isCheckedDupName = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_signup)

        getData()
        findView()

//        뒤로가기 버튼 눌렀을 때
        backBtn.setOnClickListener {
            this.onBackPressed()
        }

//        중복 확인 버튼 눌렀을 때
        dupNameBtn.setOnClickListener {
            GlobalVariables.hideKeyboard(this)
            if (!signupNameInput.text.isNullOrEmpty()) {
                checkDupName(nickname!!)
            } else {
                GlobalVariables.defaultAlertDialog(
                    this,
                    title = "닉네임 중복 체크",
                    message = "닉네임을 입력해주세요"
                )
            }
        }

//        가입 완료 버튼 눌렀을 때
        basicSignupBtn.setOnClickListener {
            when {
                !isCheckedDupName -> {
                    GlobalVariables.defaultAlertDialog(
                        context = this,
                        title = "닉네임 중복 체크",
                        message = "닉네임 중복 여부를 확인해주세요"
                    )
                }

                else -> {
                    signup()
                }
            }
        }
    }

    private fun getData() {
        nickname = intent.getStringExtra("nickname")
        profileImg = intent.getStringExtra("profile_img")
        socialAccessToken = intent.getStringExtra("social_access_token")
        type = intent.getStringExtra("type")!!

        Log.d("SocialSignupActivity", "getData() ${nickname}")
        Log.d("SocialSignupActivity", "getData() ${profileImg}")
        Log.d("SocialSignupActivity", "getData() ${socialAccessToken}")
        Log.d("SocialSignupActivity", "getData() ${type}")
    }

    private fun findView() {
        basicSignupBtn = findViewById(R.id.basicSignupBtn)
        dupNameBtn = findViewById(R.id.dupNameBtn)
        backBtn = findViewById(R.id.backBtn)
        signupNameInput = findViewById(R.id.signupNameInput)
        signupNameInputImg = findViewById(R.id.signupNameInputImg)

        if (nickname != null && nickname != "null") {
            signupNameInput.setText(nickname)
            if(nickname != "") {
                signupNameInputImg.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.point
                    )
                )
            }
        } else {
            signupNameInput.hint = "닉네임"
        }
        checkEditText(signupNameInput, signupNameInputImg)
    }

    private fun signup() {
        Log.d("LoginActivity", "socialLogin() 소셜 로그인 요청")
        val retrofit = RetrofitClient.basicClient()!!
        val loginService = retrofit.create(LoginService::class.java)
        val firebaseToken = UserData.getUserFirebase()
        val nickname = signupNameInput.text.toString()
        Log.d("SocialSignupActivity","signup() 닉네임 $socialAccessToken")
        Log.d("SocialSignupActivity","signup() 닉네임 $nickname")
        loginService.requestSocialLogin(
            SocialLoginRequest(
                oauthProvider = type!!,
                accessToken = socialAccessToken!!,
                nickName = nickname,
                firebaseToken = firebaseToken
            )
        )
            .enqueue(object : Callback<SignupResponse> {
                override fun onResponse(
                    call: Call<SignupResponse>,
                    response: Response<SignupResponse>
                ) {
                    if (response.code() == 200) {

//                    통신 성공시 실행되는 코드
                        var loginBody = response.body()

//                    토큰 저장
                        if (loginBody != null) {
                            GlobalVariables.prefs.setString("access_token", loginBody.accessToken)
                            GlobalVariables.prefs.setString("refresh_token", loginBody.accessToken)
                            GlobalVariables.getUser()
                            UserData.setIsSocialLogined(type!!)

                            GlobalVariables.defaultAlertDialog(
                                this@SocialSignupActivity,
                                "회원가입 성공",
                                "Groot에 오신 것을 환영합니다!", ::moveToMain, false
                            )
                        }

                        // 파이어베이스에 등록
                        // Firebase Realtime Database에 접속합니다.
                        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                        val usersRef: DatabaseReference = database.getReference("users")

                        // 이미 등록된 사용자의 정보를 가져와서 Firebase에 저장합니다.
                        fun registerUserInFirebase(userId: String, userLoginId: String) {
                            // 사용자 정보를 usersRef에 추가합니다.
                            val userRef: DatabaseReference = usersRef.child(userId)
                            userRef.child("username").setValue(userLoginId)
                        }

// 사용자 등록 예시
//                        val userId = userPk // 이미 존재하는 사용자의 고유한 ID
                        val userId = response.body()!!.userPK
                        val userLoginId = userId

                        registerUserInFirebase(userId, userLoginId)

                    } else {
                        Log.d("SocialSignupActivity", "onResponse() 실패 $resources")
                    }
                }

                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
//                    통신 실패시 실행되는 코드
                    GlobalVariables.defaultAlertDialog(
                        this@SocialSignupActivity,
                        "회원가입 실패",
                        t.message
                    )

                }
            })
    }

    private fun moveToMain() {
        var intent =
            Intent(
                this@SocialSignupActivity,
                MainActivity::class.java
            )
        startActivity(intent)
    }

    private fun checkDupName(nickname: String) {
        Log.d("SocialSignupActivity", "checkDupName() 중복 체크하는 닉네임 $nickname")
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
                            checkDupNameMsg = "닉네임을 입력해주세요"
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

    private fun checkEditText(editText: EditText, imageView: ImageView) {
        var message = ""
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                message = editText.text.toString()
                isCheckedDupName = false
                nickname = message
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

            override fun afterTextChanged(s: Editable?) {}
        })

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Enter(또는 Done) 키가 눌렸을 때 수행할 동작
                GlobalVariables.hideKeyboard(this)
                true
            } else {
                false
            }
        }
    }
}