package com.chocobi.groot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.chocobi.groot.view.login.LoginActivity
import com.chocobi.groot.view.signup.SignupActivity

class ShortcutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shortcut)

        val shortcutLogin = findViewById<Button>(R.id.shortcutLogin)
        val shortcutSignup = findViewById<Button>(R.id.shortcutSignup)
        val shortcutMain = findViewById<Button>(R.id.shortcutMain)

        shortcutLogin.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        shortcutSignup.setOnClickListener {
            var intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        shortcutMain.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}