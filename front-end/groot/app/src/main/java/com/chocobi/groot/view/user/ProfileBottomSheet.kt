package com.chocobi.groot.view.user


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.GlobalVariables.Companion.changeImgView
import com.chocobi.groot.data.PERMISSION_GALLERY
import com.chocobi.groot.data.REQUEST_STORAGE
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.user.model.ProfileRequest
import com.chocobi.groot.view.user.model.UserService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class ProfileBottomSheet(context: Context) : BottomSheetDialogFragment() {
    val TAG = "ProfileBottomSheet"
    private lateinit var profileImg: CircleImageView
    private lateinit var basicImg: CircleImageView
    private var profileString: String? = null
    private var imgFile: File? = null
    private var isChanged: Boolean = false
    private var isBasic: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.bottom_sheet_profile, container, false)
        val mActivity = activity as MainActivity

//        초기 설정
        val nameText = view.findViewById<EditText>(R.id.nameText)
        nameText.setText(UserData.getNickName())
        profileImg = view.findViewById<CircleImageView>(R.id.profileImg)
        basicImg = view.findViewById<CircleImageView>(R.id.basicImg)
        var userProfile = UserData.getProfile()
        if (userProfile != "" && userProfile != null) {
            Log.d(TAG, userProfile)
            changeImgView(profileImg, userProfile, requireContext())
            profileImg.visibility = View.VISIBLE
            profileImg.borderWidth = (5 * resources.displayMetrics.density).toInt()
        } else {
            basicImg.borderWidth = (5 * resources.displayMetrics.density).toInt()
        }
        basicImg.setOnClickListener {
            basicImg.borderWidth = (5 * resources.displayMetrics.density).toInt()
            profileImg.borderWidth = 0
            isBasic = true
        }
        profileImg.setOnClickListener {
            isBasic = false
            profileImg.borderWidth = (5 * resources.displayMetrics.density).toInt()
            basicImg.borderWidth = 0
        }

//        카메라 버튼 클릭
        val profileImgBtn = view.findViewById<ImageButton>(R.id.profileImgBtn)
        profileImgBtn.setOnClickListener {
            requestPermissions()
        }

//        완료 버튼 클릭
        val profileSubmitBtn = view.findViewById<Button>(R.id.profileSubmitBtn)
        profileSubmitBtn.setOnClickListener {
            if (nameText.text.toString() == "") {
                Toast.makeText(
                    requireContext(),
                    "닉네임을 입력해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                changeProfile(requireContext(), nameText.text.toString(), imgFile)
                dismiss()
            }
        }



        return view
    }


    private fun requestPermissions() {
        if (allPermissionsGranted()) {
            openGallery()
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                PERMISSION_GALLERY
            )
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES).all {
            ContextCompat.checkSelfPermission(
                requireContext(), it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_GALLERY) {
            if (allPermissionsGranted()) {
                openGallery()
            } else {
                Toast.makeText(
                    activity,
                    "갤러리 권한 설정이 필요합니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_STORAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_STORAGE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            if (imageUri != null) {
                isChanged = true
                isBasic = false
                profileImg.visibility = View.VISIBLE
                profileImg.borderWidth = (5 * resources.displayMetrics.density).toInt()
                basicImg.borderWidth = 0
                profileImg.setImageURI(imageUri)
                imgFile = uriToFile(imageUri)

            }
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

    private fun changeProfile(context:Context, nickname: String, file: File?) {
        var retrofit = RetrofitClient.getClient()!!
        var userService = retrofit.create(UserService::class.java)
        val userPK = UserData.getUserPK()
        if (isChanged && !isBasic) {
            profileString = UserData.getProfile()
        } else {
            profileString = null
        }

        var filePart: MultipartBody.Part? = null

        if (file != null && !isBasic) {
            val mediaType = "image/*".toMediaTypeOrNull()
            val requestFile = RequestBody.create(mediaType, file)
            filePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

        userService.changeProfile(ProfileRequest(userPK, nickname, profileString), filePart)
            .enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    var body = response.body()
                    Log.d(TAG, "$body")
                    if (body != null) {
                        Toast.makeText(
                            context,
                            "프로필이 변경되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    GlobalVariables.getUser()
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Log.d(TAG, "프로필 변경 실패")
                }
            })
    }
}