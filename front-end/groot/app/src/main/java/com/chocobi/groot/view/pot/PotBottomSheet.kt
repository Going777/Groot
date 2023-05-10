package com.chocobi.groot.view.pot

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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.MsgResponse
import com.chocobi.groot.data.PERMISSION_GALLERY
import com.chocobi.groot.data.REQUEST_STORAGE
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.pot.model.PotImgResponse
import com.chocobi.groot.view.pot.model.PotNameRequest
import com.chocobi.groot.view.pot.model.PotService
import com.chocobi.groot.view.pot.model.PotStatusRequest
import com.chocobi.groot.view.sensor.SensorActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream


class PotBottomSheet(context: Context, private val listener: PotBottomSheetListener) :
    BottomSheetDialogFragment() {
    private val TAG = "PotBottomSheet"

    private var potId: Int = 0
    private var plantId: Int = 0
    private var potName: String = ""
    private var potPlant: String = ""
    private lateinit var mActivity: MainActivity
    private lateinit var dialog: AlertDialog.Builder
    private lateinit var potImgSection: LinearLayout
    private lateinit var potNameSection: LinearLayout
    private lateinit var settingPotSection: LinearLayout
    private lateinit var sheetTitle: TextView
    private lateinit var potImg: ImageView
    private var imgFile: File? = null

    fun setPotId(id: Int) {
        potId = id
    }

    fun setPlantId(id: Int) {
        plantId = id
    }

    fun setPotName(name: String) {
        potName = name
    }

    fun setPotPlant(name: String) {
        potPlant = name
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.bottom_sheet_pot, container, false)

        mActivity = activity as MainActivity

        sheetTitle = view.findViewById(R.id.sheetTitle)
        potImgSection = view.findViewById(R.id.potImgSection)
        potNameSection = view.findViewById(R.id.potNameSection)
        settingPotSection = view.findViewById(R.id.settingPotSection)
        potImg = view.findViewById(R.id.potImg)


//        화분 이름 변경 레이아웃 조작
        val nameText = view.findViewById<EditText>(R.id.nameText)
        nameText.setText(potName)
        val editPotNameBtn = view.findViewById<ImageButton>(R.id.editPotName)
        editPotNameBtn.setOnClickListener {
            showNameSection()
        }
        val editNameCancelBtn = view.findViewById<Button>(R.id.editNameCancelBtn)
        editNameCancelBtn.setOnClickListener {
            hideNameSection()
        }
        val editNameBottomBtn = view.findViewById<Button>(R.id.editNameBottomBtn)
        editNameBottomBtn.setOnClickListener {
            var newPotName = nameText.text.toString()
            changePotImg(potId, newPotName, null)
        }


//        화분 이미지 변경 레이아웃 조작
        val editPotImgBtn = view.findViewById<ImageButton>(R.id.editPotImg)
        editPotImgBtn.setOnClickListener {
            showImgSection()
        }
        val editImgCancelBtn = view.findViewById<Button>(R.id.editImgCancelBtn)
        editImgCancelBtn.setOnClickListener {
            hideImgSection()
        }
        val editImgBottomBtn = view.findViewById<Button>(R.id.editImgBottomBtn)
        editImgBottomBtn.setOnClickListener {
            changePotImg(potId, null, imgFile)
        }

//        화분 이미지 변경 갤러리 버튼
        val potScanBtn = view.findViewById<ImageButton>(R.id.potScanBtn)
        potScanBtn.setOnClickListener {
            requestPermissions()
        }

//        화분 위치 추천
        val potPositionBtn = view.findViewById<ImageButton>(R.id.potPosition)
        potPositionBtn.setOnClickListener {
            var intent = Intent(requireContext(), SensorActivity::class.java)
            intent.putExtra("plantId", plantId)
            intent.putExtra("plantName", potPlant)
            startActivity(intent)
        }


//        화분 삭제
        val deletePotBtn = view.findViewById<ImageButton>(R.id.deletePot)
        deletePotBtn.setOnClickListener {
            dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("화분 삭제")
            dialog.setMessage("화분을 삭제하시겠습니까?")
            dialog.setPositiveButton(
                "삭제",
                DialogInterface.OnClickListener { dialog, which ->
                    deletePot(potId)
                    dialog.dismiss()
                })
            dialog.setNegativeButton(
                "취소",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
            dialog.show()
        }

        //        화분 떠나보내기
        val gonePotBtn = view.findViewById<ImageButton>(R.id.gonePot)
        gonePotBtn.setOnClickListener {
            dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("화분 떠나보내기")
            dialog.setMessage("화분이 죽었나요?")
            dialog.setPositiveButton(
                "네",
                DialogInterface.OnClickListener { dialog, which ->
                    gonePot(potId)
                    dialog.dismiss()
                })
            dialog.setNegativeButton(
                "아니오",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
            dialog.show()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        view?.findViewById<Button>(R.id.button_bottom_sheet)?.setOnClickListener {
//            dismiss()
//        }
    }

    private fun deletePot(potId: Int) {
        var retrofit = RetrofitClient.getClient()!!
        var potService = retrofit.create(PotService::class.java)
        potService.deletePot(potId).enqueue(object :
            Callback<MsgResponse> {
            override fun onResponse(
                call: Call<MsgResponse>,
                response: Response<MsgResponse>
            ) {
                val body = response.body()
                if (body != null && response.code() == 200) {
                    Log.d(TAG, "body: $body")
                    dismiss()
                    Toast.makeText(
                        requireContext(),
                        "화분이 삭제되었습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                    mActivity.changeFragment("pot")

                } else {
                    Log.d(TAG, "실패1")
                }
            }

            override fun onFailure(call: Call<MsgResponse>, t: Throwable) {
                Log.d(TAG, "실패2")
            }
        })

    }

    private fun gonePot(potId: Int) {
        var retrofit = RetrofitClient.getClient()!!
        var potService = retrofit.create(PotService::class.java)
        potService.gonePot(potId, PotStatusRequest("gone")).enqueue(object :
            Callback<MsgResponse> {
            override fun onResponse(
                call: Call<MsgResponse>,
                response: Response<MsgResponse>
            ) {
                val body = response.body()
                if (body != null && response.code() == 200) {
                    Log.d(TAG, "body: $body")
                    dismiss()
                    Toast.makeText(
                        requireContext(),
                        "화분을 떠나보냈습니다. 다음에는 더 잘할 수 있을 거예요!",
                        Toast.LENGTH_LONG
                    ).show()
                    listener.onGetDetailRequested()
                } else {
                    Log.d(TAG, "실패1")
                }
            }

            override fun onFailure(call: Call<MsgResponse>, t: Throwable) {
                Log.d(TAG, "실패2")
            }
        })

    }

    private fun changePotImg(potId: Int, potName: String?, file: File?) {
        var retrofit = RetrofitClient.getClient()!!
        var potService = retrofit.create(PotService::class.java)

        var filePart: MultipartBody.Part? = null

        if (file != null) {
            val mediaType = "image/*".toMediaTypeOrNull()
            val requestFile = RequestBody.create(mediaType, file)
            filePart = MultipartBody.Part.createFormData("img", file.name, requestFile)
        }

        potService.changePotImg(potId, PotNameRequest(potName), filePart)
            .enqueue(object : Callback<PotImgResponse> {
                override fun onResponse(
                    call: Call<PotImgResponse>,
                    response: Response<PotImgResponse>
                ) {
                    var body = response.body()
                    Log.d("CommunityPostFragmentABCD", filePart.toString())
                    Log.d(TAG, "$body")
                    if (body != null) {
                        listener.onGetDetailRequested()
                        if (file != null) {

                            Toast.makeText(requireContext(), "화분 이미지가 변경되었습니다.", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(requireContext(), "화분 이름이 변경되었습니다.", Toast.LENGTH_SHORT)
                                .show()
                        }

                        dismiss()
                    }
                }

                override fun onFailure(call: Call<PotImgResponse>, t: Throwable) {
                    Log.d(TAG, "화분 이미지 변경 실패")
                }
            })

    }

    private fun showImgSection() {
        potImgSection.visibility = View.VISIBLE
        settingPotSection.visibility = View.GONE
        sheetTitle.text = "화분 이미지 변경하기"
    }

    private fun hideImgSection() {
        potImgSection.visibility = View.GONE
        settingPotSection.visibility = View.VISIBLE
        sheetTitle.text = "화분 설정"
    }

    private fun showNameSection() {
        potNameSection.visibility = View.VISIBLE
        settingPotSection.visibility = View.GONE
        sheetTitle.text = "화분 이름 변경하기"
    }

    private fun hideNameSection() {
        potNameSection.visibility = View.GONE
        settingPotSection.visibility = View.VISIBLE
        sheetTitle.text = "화분 설정"
    }

    private fun requestPermissions() {
        if (allPermissionsGranted()) {
            openGallery()
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_GALLERY
            )
            requestPermissions(
//                권한 설정 수정
//                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
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
            Log.d(TAG, "$dataCheck")
            val imageUri = data.data
            if (imageUri != null) {
                potImg.visibility = View.VISIBLE
                potImg.setImageURI(imageUri)
                imgFile = uriToFile(imageUri)
            }
            Log.d(TAG, "$imgFile")

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
