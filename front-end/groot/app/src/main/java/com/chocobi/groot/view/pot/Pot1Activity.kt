package com.chocobi.groot.view.pot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.core.net.toUri
import com.chocobi.groot.R

class Pot1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pot1)

        //        imageUri 전달받기
        var imageUri = intent.getStringExtra("imageUri")


        //        image 띄우기
        var potPhoto = findViewById<ImageView>(R.id.potPhoto)
        potPhoto.setImageURI(imageUri?.toUri())

        val add1Btn = findViewById<Button>(R.id.add1Btn)
        add1Btn.setOnClickListener {
            var intent = Intent(this, Pot2Activity::class.java)
//            intent.putExtra("toPage", "plant_add1")
            startActivity(intent)
        }


    }
}