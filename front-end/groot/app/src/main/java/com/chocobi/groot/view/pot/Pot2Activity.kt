package com.chocobi.groot.view.pot

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.pot.model.PotRequest
import com.chocobi.groot.view.pot.model.PotResponse
import com.chocobi.groot.view.pot.model.PotService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class Pot2Activity : AppCompatActivity() {
    private val TAG = "Pot2Activity"
    private var plantId: Int = 0
    private var isSuccessed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pot2)

        val plantNameSplit = intent.getStringExtra("plantNameSplit")
        plantId = intent.getIntExtra("plantId", 0)
        val tempPotName = intent.getStringExtra("tempPotName")
        var imageUri = intent.getStringExtra("imageUri")

        var plantNameShort = findViewById<TextView>(R.id.plantNameShort)
        var potNameText = findViewById<TextView>(R.id.potNameText)
        val potNameEdit = findViewById<EditText>(R.id.potNameEdit)

        plantNameShort.text = plantNameSplit
        potNameText.text = tempPotName
        potNameEdit.setText(tempPotName)

        //        화분 등록 및 Plant Detail 페이지로 이동
        val add2Btn = findViewById<Button>(R.id.add2Btn)
        add2Btn.setOnClickListener {
            Log.d(TAG, plantId.toString())
            Log.d(TAG, potNameEdit.text.toString())
            Log.d(TAG, imageUri.toString())
            if (plantId > 0 && potNameEdit.text.toString() != "" && imageUri != null) {

                var file = uriToFile(imageUri.toUri())
                addPot(this, plantId, potNameEdit.text.toString(), file)


            }
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
        var retrofit = RetrofitClient.getClient()!!
        var potService = retrofit.create(PotService::class.java)

        var filePart: MultipartBody.Part? = null

        if (file != null) {
            val mediaType = "image/*".toMediaTypeOrNull()
            val requestFile = RequestBody.create(mediaType, file)
            filePart = MultipartBody.Part.createFormData("img", file.name, requestFile)
        }

        potService.addPot(
            PotRequest(
                plantId = plantId,
                potName = potName,
                temperature = null,
                illuminance = null,
                humidity = null
            ), filePart
        )
            .enqueue(object : Callback<PotResponse> {
                override fun onResponse(
                    call: Call<PotResponse>,
                    response: Response<PotResponse>
                ) {
                    var body = response.body()
                    Log.d(TAG, "$body")
                    if (body != null) {
                        body.potId
                        Toast.makeText(context, body.potId.toString() + "번 화분이 등록 되었습니다.", Toast.LENGTH_SHORT).show()
                        var intent = Intent(context, MainActivity::class.java)
                        intent.putExtra("toPage", "plant_detail")
                        intent.putExtra("potId", body.potId)
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<PotResponse>, t: Throwable) {
                    Log.d(TAG, "화분 등록 실패")
                }
            })
    }
}