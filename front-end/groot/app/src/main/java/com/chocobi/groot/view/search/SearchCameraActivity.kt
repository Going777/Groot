package com.chocobi.groot.view.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import kotlin.random.Random


class SearchCameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_camera)

//        imageUrl 전달받기
        var imageUri = intent.getStringExtra("imageUri")?.toUri()
        Log.d("SearchCameraActivity", imageUri.toString())

//        image 띄우기
        var resultImgView = findViewById<ImageView>(R.id.resultImgView)
        resultImgView.setImageURI(imageUri)

//        디테일 버튼 조작
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("toPage", "search_detail")
            startActivity(intent)
        }

//        퍼센트 조작
        var percentText = findViewById<TextView>(R.id.percentText)

        var randomNum = Random.nextInt(15, 90)
        percentText.text = randomNum.toString() + "%"
    }
}