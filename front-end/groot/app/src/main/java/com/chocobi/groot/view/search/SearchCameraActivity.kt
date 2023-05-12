package com.chocobi.groot.view.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.pot.PlantBottomSheet
import com.chocobi.groot.view.addpot.Pot1Activity
import com.chocobi.groot.view.search.model.PlantIdentifyResponse
import com.chocobi.groot.view.search.model.SearchService
import io.github.sceneview.SceneView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream


class SearchCameraActivity : AppCompatActivity() {
    private val TAG = "SearchCameraActivity"
    private var file: File? = null
    private var plantId : Int? = null
    private var plantName : String? = null
    private var plantSci : String? = null
    private var cameraStatus: String? = null
    private var growType: String? = null
    private var mgmtLevel: String? = null
    private var characterGlbPath: String? = null

    private lateinit var plantNameText : TextView
    private lateinit var plantScoreText : TextView
    private lateinit var plantSciText : TextView
    private lateinit var frameLayoutProgress: LinearLayout
    private lateinit var cardView: CardView
    private lateinit var addPotBtn: Button
    private lateinit var searchBtn: Button
    private lateinit var detailBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_camera)

//        growType = "다육형,직립형"
//        mgmtLevel = "초보자"
//        characterGlbPath = "https://groot-a303-s3.s3.ap-northeast-2.amazonaws.com/assets/straight_0.glb"

        plantNameText = findViewById(R.id.plantNameText)
        plantSciText = findViewById(R.id.plantSciText)
        plantScoreText = findViewById(R.id.plantScoreText)
        frameLayoutProgress = findViewById(R.id.frameLayoutProgress)
        cardView = findViewById(R.id.cardView)

//        imageUri 전달받기
        var imageUri = intent.getStringExtra("imageUri")
        cameraStatus = intent.getStringExtra("cameraStatus")
        Log.d(TAG, "intent/imageUri:"+imageUri.toString())
        Log.d(TAG, "intent/cameraStatus:"+cameraStatus.toString())


        if (imageUri != null) {
            file = uriToFile(imageUri.toUri())
            identifyPlant(this, file)
        }


//        image 띄우기
        var resultImgView = findViewById<ImageView>(R.id.resultImgView)
        resultImgView.setImageURI(imageUri?.toUri())


        //        디테일 버튼 조작
        detailBtn = findViewById(R.id.detailBtn)
        detailBtn.setOnClickListener {
            Log.d("SearchCameraActivity","onCreate() ${plantId}볼랲ㅍ")
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("toPage", "search_detail")
            intent.putExtra("plant_id", plantId.toString())
            startActivity(intent)
        }

        //        화분 등록 버튼 조작
        addPotBtn = findViewById(R.id.addPotBtn)
        addPotBtn.setOnClickListener {
            var intent = Intent(this, Pot1Activity::class.java)
            intent.putExtra("imageUri", imageUri)
            intent.putExtra("plantName", plantName)
            intent.putExtra("plantId", plantId)
            intent.putExtra("growType", growType)
            intent.putExtra("mgmtLevel", mgmtLevel)
            Log.d("Pot2Activity","onCreate() 보내는 값 ///생장 ${growType}///")
            Log.d("Pot2Activity","onCreate() 보내는 값 ///숙련도 ${mgmtLevel}///")
            intent.putExtra("characterGlbPath", characterGlbPath)
            startActivity(intent)
        }

        //        검색 등록 버튼 조작
        searchBtn = findViewById(R.id.searchBtn)
        searchBtn.setOnClickListener {
            val plantBottomSheet = PlantBottomSheet(this, "fail_serach")
            plantBottomSheet.show(
                this.supportFragmentManager,
                plantBottomSheet.tag
            )
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
                    var msg = response.body()?.msg
                    Log.d(TAG, "$body")
                    if (body != null) {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        plantNameText.text = body.plant.krName
                        plantSciText.text = body.plant.sciName
                        plantScoreText.text = body.plant.score.toString() + "%"
                        plantId = body.plant.plantId
                        plantName = body.plant.krName
                        plantSci = body.plant.sciName
                        growType = body.plant.grwType
                        mgmtLevel = body.plant.mgmtLevel
                        characterGlbPath = body.character.glbPath
                        hideProgress()
                    } else {
                        Log.d(TAG, "${response.errorBody()}")
                        hideProgress()
                    }
                }

                override fun onFailure(call: Call<PlantIdentifyResponse>, t: Throwable) {
                    Log.d(TAG, "식물 식별 실패")
                    hideProgress()
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



    private fun hideProgress() {
        frameLayoutProgress.visibility = View.GONE
        cardView.visibility = View.VISIBLE

        //        버튼 visibility 조작
        when (cameraStatus) {
            "searchPlant" -> {
                addPotBtn.visibility = View.GONE
                searchBtn.visibility = View.GONE
                detailBtn.visibility = View.VISIBLE
            }

            "addPot" -> {
                addPotBtn.visibility = View.VISIBLE
                searchBtn.visibility = View.VISIBLE
                detailBtn.visibility = View.GONE
            }
        }
    }
}