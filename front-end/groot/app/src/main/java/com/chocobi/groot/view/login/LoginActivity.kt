package com.chocobi.groot.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.signup.SignupActivity
import com.chocobi.groot.view.signup.SignupResponse
import com.chocobi.groot.view.signup.SocialSignupActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private lateinit var loginIdInput: EditText
    private lateinit var loginPwInput: EditText
    private lateinit var loginIdInputImg: ImageView
    private lateinit var loginPwInputImg: ImageView
    private lateinit var naverLoginBtn: LinearLayout
    private lateinit var kakaoLoginBtn: LinearLayout

    private var nickname: String? = null
    private var profileImg: String? = null
    private var socialAccessToken: String? = null

    private var textId: String = ""
    private var textPw: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        소셜 로그인 처리
        kakaoLoginBtn = findViewById(R.id.kakaoLoginBtn)
        naverLoginBtn = findViewById(R.id.naverLoginBtn)
        handleKakaoLogin()
        handleNaverLogin()

        val basicLoginBtn = findViewById<Button>(R.id.basicLoginBtn)
        val toSignupText = findViewById<TextView>(R.id.toSignupText)
        loginIdInput = findViewById(R.id.loginIdInput)
        loginPwInput = findViewById(R.id.loginPwInput)
        loginIdInputImg = findViewById(R.id.loginIdInputImg)
        loginPwInputImg = findViewById(R.id.loginPwInputImg)

        checkEditText(loginIdInput, loginIdInputImg)
        checkEditText(loginPwInput, loginPwInputImg)

//        로그인 버튼 클릭시
        basicLoginBtn.setOnClickListener {
            Log.d("LoginActivity", "onCreate() ${textId}// 아이디")
            Log.d("LoginActivity", "onCreate() ${textPw}// 비번")
//            아이디 입력 안했을 때
            if (textId.isBlank()) {
                GlobalVariables.defaultAlertDialog(context = this, message = "아이디를 입력해주세요")
//            비밀번호 입력 안했을 때
            } else if (textPw.isBlank()) {
                GlobalVariables.defaultAlertDialog(context = this, message = "비밀번호를 입력해주세요")
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

    private fun handleKakaoLogin() {
        kakaoLoginBtn.setOnClickListener {
            // 카카오계정으로 로그인 공통 callback 구성
            // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
                    // 사용자 정보 요청 (기본)
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e(TAG, "사용자 정보 요청 실패", error)
                        } else if (user != null) {
                            socialAccessToken = token.accessToken
                            nickname = user.kakaoAccount?.profile?.nickname
                            profileImg = user.kakaoAccount?.profile?.thumbnailImageUrl
                            Log.d("LoginActivity","onSuccess() 네이버 토큰 $socialAccessToken")

                            socialLogin("kakao")
                        }
                    }
                }
            }

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        Log.e(TAG, "카카오톡으로 로그인 실패", error)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                    } else if (token != null) {
                        Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                        UserApiClient.instance.me { user, error ->
                            if (error != null) {
                                Log.e(TAG, "사용자 정보 요청 실패", error)
                            } else if (user != null) {
                                socialAccessToken = token.accessToken
                                nickname = user.kakaoAccount?.profile?.nickname
                                profileImg = user.kakaoAccount?.profile?.thumbnailImageUrl
                                Log.d("LoginActivity","onSuccess() 네이버 토큰 $socialAccessToken")

                                socialLogin("kakao")
                            }
                        }
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }

    private fun handleNaverLogin() {
        naverLoginBtn.setOnClickListener {
            val oAuthLoginCallback = object : OAuthLoginCallback {
                override fun onSuccess() {
                    // 네이버 로그인 API 호출 성공 시 유저 정보를 가져온다
                    NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                        override fun onSuccess(result: NidProfileResponse) {
                            socialAccessToken = NaverIdLoginSDK.getAccessToken()
                            nickname = result.profile?.nickname.toString()
                            profileImg = result.profile?.profileImage.toString()
                            Log.d("LoginActivity","onSuccess() 네이버 토큰 $socialAccessToken")
                            socialLogin("naver")
                        }

                        override fun onError(errorCode: Int, message: String) {
                            //
                        }

                        override fun onFailure(httpStatus: Int, message: String) {
                            //
                        }
                    })
                }

                override fun onError(errorCode: Int, message: String) {
                    val naverAccessToken = NaverIdLoginSDK.getAccessToken()
                    Log.e(TAG, "naverAccessToken : $naverAccessToken")
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    //
                }
            }

            NaverIdLoginSDK.initialize(
                this@LoginActivity,
                com.chocobi.groot.BuildConfig.NAVER_CLIENT_ID,
                com.chocobi.groot.BuildConfig.NAVER_CLIENT_SECRET,
                "Groot"
            )
            NaverIdLoginSDK.authenticate(this@LoginActivity, oAuthLoginCallback)
        }
    }

    private fun socialLogin(type: String) {
        Log.d("LoginActivity","socialLogin() 소셜 로그인 요청")
        val retrofit = RetrofitClient.basicClient()!!
        val loginService = retrofit.create(LoginService::class.java)
        val firebaseToken = UserData.getUserFirebase()
        loginService.requestSocialLogin(SocialLoginRequest(oauthProvider = type, accessToken =  socialAccessToken!!, firebaseToken = firebaseToken))
            .enqueue(object : Callback<SignupResponse> {
                override fun onResponse(
                    call: Call<SignupResponse>,
                    response: Response<SignupResponse>
                ) {
//                    우리 서버에 존재하는 경우
                    if (response.code() == 200) {
                        var loginBody = response.body()

//                    토큰 저장
                        if (loginBody != null) {
                            GlobalVariables.prefs.setString("access_token", loginBody.accessToken)
                            GlobalVariables.prefs.setString("refresh_token", loginBody.accessToken)
                            GlobalVariables.getUser()
                            UserData.setIsSocialLogined(type)
                        }

                        var intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
//                    우리 서버에 존재 안하는 경우
                    else {
                        // 토큰 정보 보기
                        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                            if (error != null) {
                                Log.e(TAG, "토큰 정보 보기 실패", error)
                            }
                            else if (tokenInfo != null) {
                                Log.i(TAG, "토큰 정보 보기 성공" +
                                        "\n회원번호: ${tokenInfo.id}" +
                                        "\n만료시간: ${tokenInfo.expiresIn} 초")
                            }
                        }
                        Log.d("LoginActivity", "onResponse() $response")
                        val intent =
                            Intent(this@LoginActivity, SocialSignupActivity::class.java)
                        intent.putExtra("nickname", nickname)
                        intent.putExtra("profile_img", profileImg)
                        intent.putExtra("social_access_token", socialAccessToken)
                        intent.putExtra("type", type)
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
//                    통신 실패시 실행되는 코드
                    var dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("실패!")
                    dialog.setMessage(t.message)
                    dialog.show()
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
        val firebaseToken = UserData.getUserFirebase()

        //            로그인 요청 보내기
        loginService.requestLogin(LoginRequest(textId, textPw, firebaseToken))
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
                        GlobalVariables.defaultAlertDialog(
                            context = context,
                            message = "등록되지 않은 아이디이거나\n아이디 또는 비밀번호를 잘못 입력했습니다."
                        )
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