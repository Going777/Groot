package com.chocobi.groot.view.addpot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.chocobi.groot.R
import com.chocobi.groot.data.UserData

class Pot1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pot1)

        //        imageUri 전달받기
        var imageUri = intent.getStringExtra("imageUri")
        var plantNameLong = findViewById<TextView>(R.id.plantNameLong)
        var plantNameShort = findViewById<TextView>(R.id.plantNameShort)
        var potName = findViewById<TextView>(R.id.potName)

        val plantName = intent.getStringExtra("plantName")
        val plantId = intent.getIntExtra("plantId", 0)
        val plantNameSplit = plantName?.split(" ")?.get(0) ?: ""
        val userName = UserData.getNickName()
        var tempPotName = userName + "의 " + plantNameSplit
        plantNameLong.text = plantName
        plantNameShort.text = plantNameSplit
        potName.text = tempPotName



        //        image 띄우기
        var potPhoto = findViewById<ImageView>(R.id.potPhoto)
        potPhoto.setImageURI(imageUri?.toUri())

        val add1Btn = findViewById<Button>(R.id.add1Btn)
        add1Btn.setOnClickListener {
            var intent = Intent(this, Pot2Activity::class.java)
//            intent.putExtra("toPage", "plant_add1")
            intent.putExtra("tempPotName", tempPotName)
            intent.putExtra("plantNameSplit", plantNameSplit)
            intent.putExtra("plantId", plantId)
            intent.putExtra("imageUri", imageUri)
            startActivity(intent)
        }


    }
}