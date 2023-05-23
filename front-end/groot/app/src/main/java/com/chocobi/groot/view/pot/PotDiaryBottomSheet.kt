package com.chocobi.groot.view.pot

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.PERMISSION_GALLERY
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.pot.model.EditDiaryRequest
import com.chocobi.groot.view.pot.model.PotService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream


class PotDiaryBottomSheet(
    context: Context,
    private val diaryId: Int,
    private val potId: Int,
    private val potName: String,
    private val pastContent: String?,
    private val pastImgPath: String?,
    private var diaryWater: Boolean?,
    private var diaryPruning: Boolean?,
    private var diaryBug: Boolean?,
    private var diarySun: Boolean?,
    private var diaryNutrients: Boolean?,
) : BottomSheetDialogFragment() {
    private val TAG = "PotDiaryBottomSheet"

    //    private var potId: Int? = null
//    private var potName: String? = null
    private var potPlant: String? = null
    private var imageFile: File? = null
    private var imgpath: String? = null

    private lateinit var mActivity: MainActivity

    private lateinit var attachedPhoto: ImageView
    private lateinit var attachPhotoSection: LinearLayout
    private lateinit var attachedPhotoSection: ConstraintLayout
    private lateinit var diaryContent: TextInputEditText
    private lateinit var postDiaryBtnClickBtn: AppCompatButton
    private lateinit var nestedScrollView: NestedScrollView

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val rootView = inflater.inflate(R.layout.bottom_sheet_pot_diary, container, false)


        mActivity = activity as MainActivity

        findView(rootView)
//        filterChipGroup()
        postDiaryBtnClick()

        potCharImg = arguments?.getString("potCharImg")

//        화면 구성
        var potNameText = rootView.findViewById<TextView>(R.id.potNameText)
        var potNameText2 = rootView.findViewById<TextView>(R.id.potNameText2)
        potNameText.text = potName
        potNameText2.text = potName
        water = diaryWater ?: false
        pruning = diaryPruning ?: false
        bug = diaryBug ?: false
        sun = diarySun ?: false
        nutrients = diaryNutrients ?: false


//        사진 첨부 취소 버튼
        val attachCancleBtn = rootView.findViewById<ImageButton>(R.id.attachCancleBtn)

//        기존 사진 첨부시 삭제 버튼이 적용 안되어서 주석처리함
//        attachPhotoSection!!.setOnClickListener {
//        사진 첨부 섹션
        val attachPhotoSection = rootView.findViewById<LinearLayout>(R.id.attachPhotoSection)
//        첨부된 이미지 섹션
        val attachedPhotoSection =
            rootView.findViewById<ConstraintLayout>(R.id.attachedPhotoSection)

        myImageView = rootView.findViewById(R.id.attachedPhoto)



        attachPhotoSection.setOnClickListener {
            mActivity.setGalleryStatus("pot_diary_edit")
            mActivity.requirePermissions(
                arrayOf(
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                PERMISSION_GALLERY
            )
        }
        attachCancleBtn.setOnClickListener {
            Log.d(TAG, "클릭클릭")
            attachPhotoSection!!.visibility = View.VISIBLE
            attachedPhotoSection!!.visibility = View.GONE
            imageFile = null
            imgpath = pastImgPath
        }
//        }

        return rootView
    }


    private fun findView(view: View) {
        nestedScrollView = view.findViewById(R.id.nestedScrollView)
        //        사진 첨부 섹션
        attachPhotoSection = view.findViewById(R.id.attachPhotoSection)
//        첨부된 이미지 섹션
        attachedPhotoSection = view.findViewById(R.id.attachedPhotoSection)
        attachedPhoto = view.findViewById(R.id.attachedPhoto)

        waterChip = view.findViewById(R.id.waterChip)
        potChip = view.findViewById(R.id.potChip)
        bugChip = view.findViewById(R.id.bugChip)
        sunChip = view.findViewById(R.id.sunChip)
        pillChip = view.findViewById(R.id.pillChip)

        waterChip.isChecked = diaryWater ?: false
        potChip.isChecked = diaryPruning ?: false
        bugChip.isChecked = diaryBug ?: false
        sunChip.isChecked = diarySun ?: false
        pillChip.isChecked = diaryNutrients ?: false

        if (pastImgPath != null) {
            GlobalVariables.changeImgView(attachedPhoto, pastImgPath, requireContext())
//            attachPhotoSection.visibility = View.VISIBLE
            attachedPhotoSection.visibility = View.VISIBLE
        }

        alertConstraintChip(waterChip, diaryWater ?: false, "물 주기")
        alertConstraintChip(potChip, diaryPruning ?: false, "분갈이")
        alertConstraintChip(bugChip, diaryBug ?: false, "해충 퇴치")
        alertConstraintChip(sunChip, diarySun ?: false, "햇빛 쬐기")
        alertConstraintChip(pillChip, diaryNutrients ?: false, "영양제 주기")

        diaryContent = view.findViewById(R.id.diaryContent)
        diaryContent.setText(pastContent)
        postDiaryBtnClickBtn = view.findViewById(R.id.postDiaryBtn)
    }

    private fun filterChipGroup() {
        waterChip.setOnCheckedChangeListener { buttonView, isChecked ->
            Toast.makeText(requireContext(), "수행한 활동은 변경하실 수 없습니다", Toast.LENGTH_SHORT).show()
        }
        potChip.setOnCheckedChangeListener { buttonView, isChecked ->
            Toast.makeText(requireContext(), "수행한 활동은 변경하실 수 없습니다", Toast.LENGTH_SHORT).show()
        }
        bugChip.setOnCheckedChangeListener { buttonView, isChecked ->
            Toast.makeText(requireContext(), "수행한 활동은 변경하실 수 없습니다", Toast.LENGTH_SHORT).show()
        }
        sunChip.setOnCheckedChangeListener { buttonView, isChecked ->
            Toast.makeText(requireContext(), "수행한 활동은 변경하실 수 없습니다", Toast.LENGTH_SHORT).show()
        }
        pillChip.setOnCheckedChangeListener { buttonView, isChecked ->
            Toast.makeText(requireContext(), "수행한 활동은 변경하실 수 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    //    비활성화 칩 처리
    private fun alertConstraintChip(targetChip: Chip, targetStatus: Boolean, action: String) {
        if (targetStatus == true) {
            targetChip.isChecked = true
        }
        targetChip.isEnabled = false
    }

    private fun postDiaryBtnClick() {
        postDiaryBtnClickBtn.setOnClickListener {
            content = diaryContent.text.toString()
            editDiary()
        }
    }

    private fun editDiary() {
        val retrofit = RetrofitClient.getClient()!!
        val potService = retrofit.create(PotService::class.java)
        var filePart: MultipartBody.Part? = null
        if (imageFile != null) {
            val mediaType = "image/*".toMediaTypeOrNull()
            val requestFile = RequestBody.create(mediaType, imageFile!!)
            filePart = MultipartBody.Part.createFormData("image", imageFile!!.name, requestFile)
        }
        val userPK = UserData.getUserPK()

        potService.requestEditDiary(
            EditDiaryRequest(
                id = diaryId,
                potId = potId,
                content = content,
                water = null,
                pruning = null,
                bug = null,
                sun = null,
                nutrients = null,
                userPK = userPK,
                imgPath = imgpath,
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
                            Toast.makeText(context, "$potName 다이어리가 수정되었습니다", Toast.LENGTH_SHORT)
                                .show()
//                            다이어리 페이지로 이동
                            mActivity.changeFragment("pot_diary")
                            dismiss()
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
        nestedScrollView.post {
            nestedScrollView.smoothScrollTo(0, nestedScrollView.getChildAt(0).height)
        }
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