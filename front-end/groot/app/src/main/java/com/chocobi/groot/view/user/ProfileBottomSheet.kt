package com.chocobi.groot.view.user


import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.GlobalVariables.Companion.changeImgView
import com.chocobi.groot.data.PERMISSION_GALLERY
import com.chocobi.groot.data.REQUEST_STORAGE
import com.chocobi.groot.util.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.signup.DupNameResponse
import com.chocobi.groot.view.signup.DupNameService
import com.chocobi.groot.view.user.model.ProfileRequest
import com.chocobi.groot.view.user.model.UserService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    private var isCheckedDupName: Boolean = false
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

//        중복 확인 버튼 클릭
        val nameUniqueBtn = view.findViewById<Chip>(R.id.nameUniqueBtn)
        nameUniqueBtn.setOnClickListener {
            checkDupName(requireContext(), nameText.text.toString())
        }

//        카메라 버튼 클릭
        val profileImgBtn = view.findViewById<ImageButton>(R.id.profileImgBtn)
        profileImgBtn.setOnClickListener {
            requestPermissions()
        }

//        완료 버튼 클릭
        val profileSubmitBtn = view.findViewById<Button>(R.id.profileSubmitBtn)
        profileSubmitBtn.setOnClickListener {
            var newNickName = nameText.text.toString()
            if (newNickName == "") {
                Toast.makeText(requireContext(), "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else if (newNickName != UserData.getNickName() && !isCheckedDupName) {
                Toast.makeText(requireContext(), "닉네임 중복 여부를 확인해주세요", Toast.LENGTH_SHORT).show()
            } else {
                changeProfile(requireContext(), newNickName, imgFile)
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
            requestPermissions(
//                권한 설정 수정
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
//                arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                PERMISSION_GALLERY
            )
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES).all {
            ContextCompat.checkSelfPermission(
                requireContext(), it
            ) == PackageManager.PERMISSION_GRANTED
        } || arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE).all {
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
                Toast.makeText(requireContext(), "갤러리 권한 설정이 필요합니다.", Toast.LENGTH_SHORT).show()
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
            val dataCheck = data.data
            Log.d("CommunityPostFragmentData", "$dataCheck")
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
            Log.d("CommunityPostFragmentImgFile", "$imgFile")

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

    private fun changeProfile(context: Context, nickname: String, file: File?) {
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
                    Log.d("CommunityPostFragmentABCD", filePart.toString())
                    Log.d(TAG, "$body")
                    if (body != null) {
                        Toast.makeText(context, "프로필이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                    GlobalVariables.getUser()
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Log.d(TAG, "프로필 변경 실패")
                }
            })
    }


    private fun checkDupName(context: Context, textName: String) {
        if (textName == "" || textName == UserData.getNickName()) {
            return
        }
        //        retrofit 객체 만들기
        var retrofit = Retrofit.Builder()
            .baseUrl(GlobalVariables.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var dupNameService = retrofit.create(DupNameService::class.java)
        dupNameService.requestDupName(textName)
            .enqueue(object : Callback<DupNameResponse> {
                override fun onResponse(
                    call: Call<DupNameResponse>,
                    response: Response<DupNameResponse>
                ) {
                    var checkDupNameMsg = response.body()?.msg
                    if (checkDupNameMsg == null) {
                        try {
                            JSONObject(response.errorBody()?.string()).let { json ->
                                checkDupNameMsg = json.getString("msg")
                            }
                        } catch (e: JSONException) {
                            // 예외 처리: msg 속성이 존재하지 않는 경우
                            checkDupNameMsg = "닉네임을 입력해주세요"
                            e.printStackTrace()
                        }
                    } else {
                        isCheckedDupName = true
                    }
                    var dialog = AlertDialog.Builder(
                        context,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                    )
                    Log.d(TAG, response.toString())
                    dialog.setTitle("닉네임 중복 체크")
                    dialog.setMessage(checkDupNameMsg)
                    dialog.setPositiveButton(
                        "확인",
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        })
                    dialog.show()
                }

                override fun onFailure(call: Call<DupNameResponse>, t: Throwable) {
                    Toast.makeText(context, "닉네임 중복 확인 실패", Toast.LENGTH_SHORT).show()
                }
            })
    }
}