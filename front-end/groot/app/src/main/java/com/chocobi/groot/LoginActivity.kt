package com.chocobi.groot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var loginIdInput = findViewById<EditText>(R.id.loginIdInput)
        var loginPwInput = findViewById<EditText>(R.id.loginPwInput)
        val basicLoginBtn = findViewById<Button>(R.id.basicLoginBtn)
        basicLoginBtn.setOnClickListener {
            var textId = loginIdInput.text.toString()
            var textPw = loginPwInput.text.toString()

            var dialog = AlertDialog.Builder(this)
            dialog.setTitle("알림!")
            dialog.setMessage("id:" + textId + "pw:" + textPw)
            dialog.show()

        }



    }
}