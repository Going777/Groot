package com.chocobi.groot.view.pot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R

class Pot2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pot2)

        //        Plant Detail 페이지로 이동
        val add2Btn = findViewById<Button>(R.id.add2Btn)
        add2Btn.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("toPage", "plant_detail")
            startActivity(intent)
        }
    }
}