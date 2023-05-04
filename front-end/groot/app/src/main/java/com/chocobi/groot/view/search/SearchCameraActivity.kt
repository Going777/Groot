package com.chocobi.groot.view.search

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
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
import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.plant.PlantBottomSheet
import com.chocobi.groot.view.pot.Pot1Activity
import com.chocobi.groot.view.search.model.PlantIdentifyResponse
import com.chocobi.groot.view.search.model.SearchService
import com.chocobi.groot.view.user.model.ProfileRequest
import com.chocobi.groot.view.user.model.UserService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random


class SearchCameraActivity : AppCompatActivity() {
    private val TAG = "SearchCameraActivity"
    private var file: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_camera)

//        imageUri 전달받기
        var imageUri = intent.getStringExtra("imageUri")
        var cameraStatus = intent.getStringExtra("cameraStatus")
        Log.d(TAG, "intent/imageUri:"+imageUri.toString())
        Log.d(TAG, "intent/cameraStatus:"+cameraStatus.toString())


        if (imageUri != null) {
            file = uriToFile(imageUri.toUri())
            identifyPlant(this, file)
        }


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
                addPlantBtn.visibility = View.GONE
                searchBtn.visibility = View.GONE
                detailBtn.visibility = View.VISIBLE
            }

            "addPlant" -> {
                addPlantBtn.visibility = View.VISIBLE
                searchBtn.visibility = View.VISIBLE
                detailBtn.visibility = View.GONE
            }
        }
    }

    private fun identifyPlant(context: Context, file: File?) {
        if (file == null) {
            return
        }
        var retrofit = RetrofitClient.basicClient()!!
        var searchService = retrofit.create(SearchService::class.java)

        var filePart: MultipartBody.Part? = null

        val mediaType = "image/png".toMediaTypeOrNull()
        val requestFile = RequestBody.create(mediaType, file)
        filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)


        searchService.identifyPlant(filePart).enqueue(
            object : Callback<PlantIdentifyResponse> {
                override fun onResponse(
                    call: Call<PlantIdentifyResponse>,
                    response: Response<PlantIdentifyResponse>
                ) {
                    var body = response.body()
                    Log.d(TAG, "$body")
                    if (body != null) {
                        Toast.makeText(context, "$body", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d(TAG, "${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<PlantIdentifyResponse>, t: Throwable) {
                    Log.d(TAG, "식물 식별 실패")
                }
            })
    }

    private fun uriToFile(uri: Uri): File? {
        val inputStream = contentResolver?.openInputStream(uri)
        val tempFile = File.createTempFile("prefix", "extension")
        tempFile.deleteOnExit()
        val outputStream = FileOutputStream(tempFile)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }
}