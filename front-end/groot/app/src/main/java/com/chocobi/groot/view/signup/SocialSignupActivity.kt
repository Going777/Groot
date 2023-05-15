package com.chocobi.groot.view.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.kakao.sdk.common.util.Utility

class SocialSignupActivity : AppCompatActivity() {
    private lateinit var basicSignupBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_social_signup)

        findView()

        signup()
    }

    private fun findView(){
        basicSignupBtn = findViewById(R.id.basicSignupBtn)
    }

    private fun signup() {
        basicSignupBtn.setOnClickListener {
            GlobalVariables.defaultAlertDialog(this@SocialSignupActivity, title = "로그인 성공", "반갑습니다!!")
        }
    }
}