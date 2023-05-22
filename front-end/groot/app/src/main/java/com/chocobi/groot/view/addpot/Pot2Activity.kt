package com.chocobi.groot.view.addpot

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import com.airbnb.lottie.LottieAnimationView
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.addpot.model.AddPotRequest
import com.chocobi.groot.view.addpot.model.AddPotResponse
import com.chocobi.groot.view.addpot.model.AddPotService
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.github.sceneview.SceneView
import io.github.sceneview.node.ModelNode
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import io.github.sceneview.utils.Color
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation

class Pot2Activity : AppCompatActivity() {
    private val TAG = "Pot2Activity"
    private var plantId: Int = 0
    private var plantName: String = ""
    private var isRegistered = false

    private lateinit var scrollView: RelativeLayout
    private lateinit var characterSceneView: SceneView
    private lateinit var lottieView: LottieAnimationView
    private lateinit var potNameLayout: TextInputLayout

    private lateinit var potNameEdit: TextInputEditText

    private var growType: String? = null
    private var mgmtLevel: String? = null
    private var characterGlbPath: String? = null
    private var modelNode: ModelNode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pot2)

        val categoryNameTextView = findViewById<TextView>(R.id.categoryName)
        val categoryIcon = findViewById<ImageView>(R.id.categoryIcon)
        categoryNameTextView.text = "화분"
        categoryIcon.setImageResource(R.drawable.ic_pot)

//        ================================================================
//        ================================================================
//        뒤로 가기 버튼 처리해야 하는 곳
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            this.onBackPressed()
        }
//        ================================================================
//        ================================================================

        plantName = intent.getStringExtra("plantName").toString()
        plantId = intent.getIntExtra("plantId", 0)
        val tempPotName = intent.getStringExtra("tempPotName")
//        val tempPotName = "루티"
        var imageUri = intent.getStringExtra("imageUri")
        val plantNameText = findViewById<TextView>(R.id.plantNameText)
        plantNameText.text = plantName

        potNameEdit = findViewById<TextInputEditText>(R.id.potNameEdit)

//        potNameText.text = tempPotName
//        potNameEdit.setText(tempPotName)

        scrollView = findViewById(R.id.scrollView)
        characterSceneView = findViewById(R.id.characterSceneView)
        lottieView = findViewById(R.id.lottieView)
        growType = intent.getStringExtra("growType")
        mgmtLevel = intent.getStringExtra("mgmtLevel")
        characterGlbPath = intent.getStringExtra("characterGlbPath")

//        키보드 올라왔을 때 그만큼 스크롤 올리기
        potNameEdit.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                scrollView.post {
                    scrollView.scrollTo(0, potNameEdit.bottom)
                }
            }
        }

        lottieView.setAnimation(R.raw.congratulation)
        setCharacterSceneView()


        //        화분 등록 및 Plant Detail 페이지로 이동
        val add2Btn = findViewById<ImageButton>(R.id.add2Btn)
        add2Btn.setOnClickListener {
            if (isRegistered) {
                Toast.makeText(this, "화분을 등록중입니다.", Toast.LENGTH_SHORT).show()
            } else {
                val potName = potNameEdit.text.toString()
                if (potName == "") {
                    Toast.makeText(this, "루티에게 이름을 지어주세요", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(TAG, plantId.toString())
                    Log.d(TAG, potName)
                    Log.d(TAG, imageUri.toString())
                    if (plantId > 0) {
                        Log.d("Pot2Activity", "onCreate() ${plantId}// ${imageUri}//")
                        var file: File? = null
                        if (imageUri != null) {
                            file = uriToFile(imageUri.toUri())
                        }

                        addPot(this, plantId, potNameEdit.text.toString(), file)
                    }
                }
            }
        }
    }

    private fun setCharacterSceneView() {

        Log.d("Pot1Activity", "onCreate() 222 $growType")
        Log.d("Pot1Activity", "onCreate() 222 $mgmtLevel")
        Log.d("Pot1Activity", "onCreate() 222 $characterGlbPath")

        if (modelNode != null) {
            characterSceneView.removeChild(modelNode!!)
        }

        characterSceneView.backgroundColor = Color(255.0f, 255.0f, 255.0f, 0.0f)

        modelNode = ModelNode().apply {
            loadModelGlbAsync(
                glbFileLocation = characterGlbPath!!,
                autoAnimate = false,
                scaleToUnits = 0.8f,
                centerOrigin = Position(x = 0f, y = 1f, z = 0f),
            ) {
                isScaleEditable = true
                isRotationEditable = false
                maxEditableScale = 1f
                minEditableScale = 0.5f
                rotation = Rotation(30.0f, 0.0f, 0.0f)
                playAnimation(15, true)

                Handler().postDelayed({
                    stopAnimation(15)
                    lottieView.playAnimation()
                    playAnimation(18, true)
                }, 1500)
            }
        }
        if (modelNode != null) {
            characterSceneView.addChild(modelNode!!)
        }
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

    private fun addPot(context: Context, plantId: Int, potName: String, file: File?) {
        Log.d("Pot2Activity", "addPot() 요청을 하겠어요")
        isRegistered = true

        var retrofit = RetrofitClient.getClient()!!
        var AddPotService = retrofit.create(AddPotService::class.java)

        var filePart: MultipartBody.Part? = null

        if (file != null) {
            val mediaType = "image/*".toMediaTypeOrNull()
            val requestFile = RequestBody.create(mediaType, file)
            filePart = MultipartBody.Part.createFormData("img", file.name, requestFile)
        }

        AddPotService.addPot(
            AddPotRequest(
                plantId = plantId,
                potName = potName,
                temperature = null,
                illuminance = null,
                humidity = null
            ), filePart
        )
            .enqueue(object : Callback<AddPotResponse> {
                override fun onResponse(
                    call: Call<AddPotResponse>,
                    response: Response<AddPotResponse>
                ) {
                    var body = response.body()
                    Log.d(TAG, "$body")
                    if (body != null) {
//                        body.potId
//                        Toast.makeText(
//                            context,
//                            body.potId.toString() + "번 화분이 등록 되었습니다.",
//                            Toast.LENGTH_SHORT
//                        ).show()
                        var intent = Intent(context, MainActivity::class.java)
                        intent.putExtra("toPage", "pot_detail")
                        intent.putExtra("potId", body.potId)
                        intent.putExtra("potName", potName)
                        intent.putExtra("potPlant", plantName)
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<AddPotResponse>, t: Throwable) {
                    Log.d(TAG, "화분 등록 실패")
                }
            })
    }
}