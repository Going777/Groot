package com.chocobi.groot

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.chocobi.groot.mlkit.kotlin.ml.ArActivity
import com.chocobi.groot.view.intro.IntroActivity
import com.chocobi.groot.view.login.LoginActivity
import com.chocobi.groot.view.signup.SignupActivity

class ShortcutActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shortcut)

        val shortcutLogin = findViewById<Button>(R.id.shortcutLogin)
        val shortcutSignup = findViewById<Button>(R.id.shortcutSignup)
        val shortcutMain = findViewById<Button>(R.id.shortcutMain)
        val shortcutIntro = findViewById<Button>(R.id.shortcutIntro)
        val shortcutAr = findViewById<Button>(R.id.shortcutAr)
        val shortcutCharacter = findViewById<Button>(R.id.shortcutCharacter)

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

        shortcutIntro.setOnClickListener {
            var intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
        }

        shortcutAr.setOnClickListener {
            var intent = Intent(this, ArActivity::class.java)
            startActivity(intent)
        }

        shortcutCharacter.setOnClickListener {
            var intent = Intent(this, CharacterActivity::class.java)
            startActivity(intent)
        }
    }
}