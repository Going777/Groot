package com.chocobi.groot

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        sharedPreference
        val shared = getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
        val editor = shared.edit() // 수정을 위한 에디터

//        초기 토큰값 확인
        var access_token = shared.getString("access_token", "초기값")
        var dialog = AlertDialog.Builder(this@LoginActivity)
        dialog.setTitle("초기값")
        dialog.setMessage(access_token)
        dialog.show()



//        retrofit 객체 만들기
        var retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

//        service 객체 만들기
        var loginService = retrofit.create(LoginService::class.java)


        var loginIdInput = findViewById<EditText>(R.id.loginIdInput)
        var loginPwInput = findViewById<EditText>(R.id.loginPwInput)
        val basicLoginBtn = findViewById<Button>(R.id.basicLoginBtn)
        basicLoginBtn.setOnClickListener {
            var textId = loginIdInput.text.toString()
            var textPw = loginPwInput.text.toString()

            loginService.requestLogin(textId, textPw).enqueue(object:Callback<Login>{

                override fun onFailure(call: Call<Login>, t: Throwable) {
//                    통신 실패시 실행되는 코드
                    var dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("실패!")
                    dialog.setMessage(t.message)
                    dialog.show()
                }

                override fun onResponse(call: Call<Login>, response: Response<Login>) {
//                    통신 성공시 실행되는 코드
                    var login = response.body()

                    var dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("알림!")

//                    토큰 저장
                    editor.putString("access_token", login?.access_token)
                    editor.putString("refresh_token", login?.access_token)
                    editor.commit()

//                    토큰 확인
                    access_token = shared.getString("access_token", "")
                    dialog.setMessage(access_token)
                    dialog.show()
                }
            })



        }



    }
}