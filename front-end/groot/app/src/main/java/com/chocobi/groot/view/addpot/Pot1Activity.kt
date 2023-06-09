package com.chocobi.groot.view.addpot

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterButton
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.UserData
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class Pot1Activity : AppCompatActivity() {

    private var growType: String? = null
    private var mgmtLevel: String? = null
    private var characterGlbPath: String? = null
    private lateinit var potInfochipGroup: ChipGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pot1)

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

        //        imageUri 전달받기
        var imageUri = intent.getStringExtra("imageUri")
        var imageUrl = intent.getStringExtra("imageUrl")

        Log.d("Pot1Activity","onCreate() uri값 ${imageUri}//")
        Log.d("Pot1Activity","onCreate() url값 ${imageUrl}//")


        var plantNameText = findViewById<TextView>(R.id.plantNameText)



        val plantName = intent.getStringExtra("plantName")
        val plantId = intent.getIntExtra("plantId", 0)
        Log.d("Pot2Activity","onCreate() 플랜트 아이디 ${plantId}")
        val userName = UserData.getNickName()
        val tempPlantName = plantName!!.replace(" (", "\n(").replace(" ‘","\n‘")
        plantNameText.text = tempPlantName






        growType = intent.getStringExtra("growType")
        mgmtLevel = intent.getStringExtra("mgmtLevel")
        Log.d("Pot2Activity","onCreate() ///생장 ${growType}///")
        Log.d("Pot2Activity","onCreate() ///숙련도 ${mgmtLevel}///")
        characterGlbPath = intent.getStringExtra("characterGlbPath")
        potInfochipGroup = findViewById(R.id.potInfochipGroup)


        val growTypes = growType?.split(",")?.toTypedArray() ?: arrayOf()

//        if (test != null) {
//            chipRegionGroup.addView(
//                Chip(
//                    requireContext(),
//                    null,
//                    R.style.REGION_CHIP_ICON
//                ).apply {
//                    text = test
//                    isCloseIconVisible = false
//                })
//        }


        Log.d("Pot1Activity","onCreate() 숙련도 ${mgmtLevel}")
        Log.d("Pot1Activity","onCreate() 생장 ${growTypes}")
        if (mgmtLevel != "" && mgmtLevel != null) {
            potInfochipGroup.addView(
                Chip(this, null, R.style.REGION_CHIP_ICON).apply {
                    text = mgmtLevel
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    setChipBackgroundColorResource(R.color.main)
                    chipStrokeWidth = 0f
                }
            )
        }
        if (growTypes != null) {
            for (type in growTypes) {
                potInfochipGroup.addView(
                    Chip(this, null, R.style.REGION_CHIP_ICON).apply {
                        text = type
                        setTextColor(ContextCompat.getColor(context, R.color.white))
                        setChipBackgroundColorResource(R.color.sub)
                        chipStrokeWidth = 0f
                    }
                )
            }
        }


        Log.d("Pot1Activity", "onCreate() 111 $growType")
        Log.d("Pot1Activity", "onCreate() 111 $mgmtLevel")
        Log.d("Pot1Activity", "onCreate() 111 $characterGlbPath")

        //        image 띄우기
        var potPhoto = findViewById<ImageView>(R.id.potPhoto)
        if(imageUri.isNullOrEmpty() || imageUri.isNullOrBlank()) {
            GlobalVariables.changeImgView(potPhoto, imageUrl!!, this)
        } else {
            potPhoto.setImageURI(imageUri?.toUri())
        }

        val add1Btn = findViewById<ImageFilterButton>(R.id.add1Btn)
        add1Btn.setOnClickListener {
            var intent = Intent(this, Pot2Activity::class.java)
//            intent.putExtra("toPage", "plant_add1")
            intent.putExtra("plantName", tempPlantName)
            intent.putExtra("plantId", plantId)
            intent.putExtra("imageUri", imageUri)
            intent.putExtra("growType", growType)
            intent.putExtra("mgmtLevel", mgmtLevel)
            intent.putExtra("characterGlbPath", characterGlbPath)
            startActivity(intent)
        }


    }
}