package com.chocobi.groot.view.search

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.chocobi.groot.R


class SearchCameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_camera)

//        imageUrl 전달받기
        var imageUrl = intent.getStringExtra("ImageUrl")
        Log.d("SearchCameraActivity", imageUrl.toString())
    }
}