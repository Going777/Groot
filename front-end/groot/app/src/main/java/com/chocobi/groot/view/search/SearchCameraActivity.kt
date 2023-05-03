package com.chocobi.groot.view.search

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.view.plant.PlantBottomSheet
import com.chocobi.groot.view.pot.Pot1Activity
import kotlin.random.Random


class SearchCameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_camera)

//        imageUri 전달받기
        var imageUri = intent.getStringExtra("imageUri")
        var cameraStatus = intent.getStringExtra("cameraStatus")
        Log.d("SearchCameraActivity", imageUri.toString())
        Log.d("SearchCameraActivity", cameraStatus.toString())


//        image 띄우기
        var resultImgView = findViewById<ImageView>(R.id.resultImgView)
        resultImgView.setImageURI(imageUri?.toUri())

//        퍼센트 조작
        var percentText = findViewById<TextView>(R.id.percentText)

        var randomNum = Random.nextInt(15, 90)
        percentText.text = randomNum.toString() + "%"

        //        디테일 버튼 조작
        val detailBtn = findViewById<Button>(R.id.detailBtn)
        detailBtn.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("toPage", "search_detail")
            startActivity(intent)
        }

        //        화분 등록 버튼 조작
        val addPlantBtn = findViewById<Button>(R.id.addPlantBtn)
        addPlantBtn.setOnClickListener {
            var intent = Intent(this, Pot1Activity::class.java)
            intent.putExtra("imageUri", imageUri)
            startActivity(intent)
        }

        //        검색 등록 버튼 조작
        val searchBtn = findViewById<Button>(R.id.searchBtn)
        searchBtn.setOnClickListener {
            val plantBottomSheet = PlantBottomSheet(this)
            plantBottomSheet.show(
                this.supportFragmentManager,
                plantBottomSheet.tag
            )

        }


//        버튼 visibility 조작
        when (cameraStatus) {
            "searchPlant" -> {
                addPlantBtn.visibility= View.GONE
                searchBtn.visibility= View.GONE
                detailBtn.visibility=View.VISIBLE
            }
            "addPlant" -> {
                addPlantBtn.visibility= View.VISIBLE
                searchBtn.visibility= View.VISIBLE
                detailBtn.visibility=View.GONE
            }
        }
    }
}