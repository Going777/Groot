package com.chocobi.groot.view.pot

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.PERMISSION_GALLERY
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.pot.model.DiaryRequest
import com.chocobi.groot.view.pot.model.PotService
import com.google.android.filament.ToneMapper.Linear
import com.google.android.material.chip.Chip
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class PotDiaryCreateFragment : Fragment() {

    private val TAG = "PotDiaryCreateFragment"

    private var potId: Int? = null
    private var potName: String? = null
    private var potPlant: String? = null
    private var imageFile: File? = null

    private lateinit var mActivity: MainActivity

    private lateinit var attachedPhoto: ImageView
    private lateinit var attachPhotoSection: LinearLayout
    private lateinit var attachedPhotoSection: ConstraintLayout
    private lateinit var diaryContent: EditText
    private lateinit var postDiaryBtnClickBtn: AppCompatButton

    private lateinit var waterChip: Chip
    private lateinit var potChip: Chip
    private lateinit var bugChip: Chip
    private lateinit var sunChip: Chip
    private lateinit var pillChip: Chip

    private var water: Boolean = false
    private var pruning: Boolean = false
    private var nutrients: Boolean = false
    private var bug: Boolean = false
    private var sun: Boolean = false
    private var content: String? = null
    private var potCharImg: String? = null


    private var myImageView: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mActivity = activity as MainActivity
        val rootView = inflater.inflate(R.layout.fragment_pot_diary_create, container, false)

//        변수 받기
        potId = arguments?.getInt("potId")
        potName = arguments?.getString("potName")
        potPlant = arguments?.getString("potPlant")

        findView(rootView)
        filterChipGroup()
        postDiaryBtnClick()

        potCharImg = arguments?.getString("potCharImg")

//        화면 구성
        var potNameText = rootView.findViewById<TextView>(R.id.potNameText)
        var potNameText2 = rootView.findViewById<TextView>(R.id.potNameText2)
        potNameText.text = potName
        potNameText2.text = potName
        var potPlantText = rootView.findViewById<TextView>(R.id.potPlantText)
        potPlantText.text = potPlant
        var potCharImage = rootView.findViewById<ImageView>(R.id.characterImg)
        if (potCharImg is String) {
            GlobalVariables.changeImgView(potCharImage, potCharImg.toString(), requireContext())
        }


//        사진 첨부 취소 버튼
        val attachCancleBtn = rootView.findViewById<ImageButton>(R.id.attachCancleBtn)

        attachPhotoSection!!.setOnClickListener {
//        사진 첨부 섹션
            val attachPhotoSection = rootView.findViewById<LinearLayout>(R.id.attachPhotoSection)
//        첨부된 이미지 섹션
            val attachedPhotoSection =
                rootView.findViewById<ConstraintLayout>(R.id.attachedPhotoSection)

            myImageView = rootView.findViewById(R.id.attachedPhoto)

            attachPhotoSection.setOnClickListener {
                mActivity.setGalleryStatus("pot_diary_create")
                mActivity.requirePermissions(
                    arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                    PERMISSION_GALLERY
                )
            }
            attachCancleBtn.setOnClickListener {
                attachPhotoSection!!.visibility = View.VISIBLE
                attachedPhotoSection!!.visibility = View.GONE
                imageFile = null
            }
        }
        return rootView
    }


    private fun findView(view: View) {
        //        사진 첨부 섹션
        attachPhotoSection = view.findViewById<LinearLayout>(R.id.attachPhotoSection)
//        첨부된 이미지 섹션
        attachedPhotoSection = view.findViewById<ConstraintLayout>(R.id.attachedPhotoSection)
        attachedPhoto = view.findViewById<ImageView>(R.id.attachedPhoto)

        diaryContent = view.findViewById(R.id.diaryContent)

        waterChip = view.findViewById(R.id.waterChip)
        potChip = view.findViewById(R.id.potChip)
        bugChip = view.findViewById(R.id.bugChip)
        sunChip = view.findViewById(R.id.sunChip)
        pillChip = view.findViewById(R.id.pillChip)

        diaryContent = view.findViewById(R.id.diaryContent)
        postDiaryBtnClickBtn = view.findViewById(R.id.postDiaryBtn)
    }

    private fun filterChipGroup() {
        waterChip.setOnCheckedChangeListener { buttonView, isChecked ->
            water = isChecked
        }
        potChip.setOnCheckedChangeListener { buttonView, isChecked ->
            pruning = isChecked
        }
        bugChip.setOnCheckedChangeListener { buttonView, isChecked ->
            bug = isChecked
        }
        sunChip.setOnCheckedChangeListener { buttonView, isChecked ->
            sun = isChecked
        }
        pillChip.setOnCheckedChangeListener { buttonView, isChecked ->
            nutrients = isChecked
        }
    }

    private fun postDiaryBtnClick() {
        postDiaryBtnClickBtn.setOnClickListener {
            content = diaryContent.text.toString()
            postDiary()
        }
    }

    private fun postDiary() {
        val retrofit = RetrofitClient.getClient()!!
        val potService = retrofit.create(PotService::class.java)
        var filePart: MultipartBody.Part? = null
        if (imageFile != null) {
            val mediaType = "image/*".toMediaTypeOrNull()
            val requestFile = RequestBody.create(mediaType, imageFile!!)
            filePart = MultipartBody.Part.createFormData("image", imageFile!!.name, requestFile)
        }

        potService.requestPostDiary(
            DiaryRequest(
                potId!!,
                content,
                water,
                pruning,
                bug,
                sun,
                nutrients
            ), filePart
        )
            .enqueue(object : retrofit2.Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.code() == 200) {
                        var body = response.body()
                        Log.d(TAG, "$body")
                        if (body != null) {
                            Toast.makeText(context, "$potName 다이어리가 작성되었습니다", Toast.LENGTH_SHORT).show()
//                            다이어리 페이지로 이동
                            mActivity.changeFragment("pot_diary")
                        }
                    } else {
                        Log.d("PotDiaryCreateFragment", "onResponse() 다이어리 작성 실패ㅜㅜㅜ $response")
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Log.d(TAG, "다이어리 작성 실패")
                }
            })
    }

    fun attachPhoto(uri: Uri) {
        attachedPhotoSection!!.visibility = View.VISIBLE
        attachedPhoto?.setImageURI(uri)
        imageFile = uriToFile(uri)
    }

    private fun uriToFile(uri: Uri): File? {
        val inputStream = context?.contentResolver?.openInputStream(uri)
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