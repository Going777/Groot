package com.chocobi.groot.view.community

import PostImageAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.PERMISSION_GALLERY
import com.chocobi.groot.data.REQUEST_STORAGE
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.community.adapter.TagAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import kotlin.math.log


@Suppress("DEPRECATION")
class CommunityShareFragment : Fragment() {

    private val TAG = "CommunityShareFragment"
    private val LIMITREGIONCNT = 1

    private var region: String? = null
    private var isFiltered = false // 필터가 걸려있는 상황인지 체크

    private lateinit var postImageAdapter: PostImageAdapter
    private val imageList: ArrayList<File?> = ArrayList()
    private val maxImageCnt = 3
    private lateinit var articleImg: ImageView
    private var imgFile: File? = null
    private var imageFiles: ArrayList<File> = ArrayList()
    private var imageArray: Array<File?>? = null
    private var thelist: MutableList<MultipartBody.Part?> = mutableListOf(null, null, null)

    private lateinit var tagRecyclerView: RecyclerView
    private lateinit var tagInput: EditText
    private lateinit var regionFilterBtn: Button
    private lateinit var chipRegionGroup: ChipGroup


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_community_share, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        val context: Context = requireContext()
        postImageAdapter = PostImageAdapter(imageList, context)
        recyclerView.adapter = postImageAdapter
        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)


        // 태그를 보여줄 RecyclerView와 입력을 받을 EditText를 레이아웃에서 참조합니다.
        tagRecyclerView = view.findViewById(R.id.tagRecyclerView)
        val flexboxLayoutManager = FlexboxLayoutManager(context)
        flexboxLayoutManager.justifyContent = JustifyContent.FLEX_START
//        recyclerView.layoutManager = flexboxLayoutManager

        tagInput = view.findViewById(R.id.tagInput)

        // RecyclerView에 사용할 레이아웃 매니저와 어댑터를 생성합니다.
//        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val tagAdapter = TagAdapter()

        // RecyclerView에 레이아웃 매니저와 어댑터를 설정합니다.
        tagRecyclerView.layoutManager = flexboxLayoutManager
        tagRecyclerView.adapter = tagAdapter

        // EditText의 키보드 액션을 설정합니다.
        tagInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                // EditText의 내용을 가져옵니다.
                val tag = tagInput.text.toString().trim()
                if (tag.isNotEmpty()) {
                    // 태그 어댑터에 태그를 추가합니다.
                    tagAdapter.addTag(tag)
                    // EditText의 내용을 리셋합니다.
                    tagInput.setText("")
                }
                true
            } else {
                false
            }
        }

        // EditText에서 포커스가 떠날 때, 자동으로 태그를 추가하는 기능을 구현합니다.
        tagInput.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                // EditText의 내용을 가져옵니다.
                val tag = tagInput.text.toString().trim()
                if (tag.isNotEmpty()) {
                    // 태그 어댑터에 태그를 추가합니다.
                    tagAdapter.addTag(tag)
                    // EditText의 내용을 리셋합니다.
                    tagInput.setText("")
                }
            }
        }


        // 이미지 업로드
        val postCameraBtn = view.findViewById<ImageButton>(R.id.postCameraBtn)

        postCameraBtn.setOnClickListener {
//            val intent = Intent(Intent.ACTION_PICK)
//            intent.type = "image/*"
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//
//            activityResult.launch(intent)
            requestPermissions()

        }


//        var retrofit = RetrofitClient.getClient()!!
//        var communityPostService = retrofit.create(CommunityPostService::class.java)
//        val userPK = UserData.getUserPK()

//        var filePart: MultipartBody.Part? = null
//
//        if (file != null) {
//            val mediaType = "image/*".toMediaTypeOrNull()
//            val requestFile = RequestBody.create(mediaType, file!!)
//            filePart = MultipartBody.Part.createFormData("image", file!!.name, requestFile)
//        }
//
//        val imageParts = arrayOfNulls<MultipartBody.Part>(imageFiles.size)
//        for ((i, imageFile) in imageFiles.withIndex()) {
//            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), imageFile)
//            imageParts[i] = MultipartBody.Part.createFormData("image[]", imageFile.name, requestFile)
//        }

        val toPostListBtn = view.findViewById<Button>(R.id.toPostListBtn)
        var titleInput = view.findViewById<EditText>(R.id.titleInput)
        var contentInput = view.findViewById<EditText>(R.id.contentInput)

        // 등록 버튼 클릭 시 제목과 내용 입력값
        toPostListBtn.setOnClickListener(View.OnClickListener {

            val category = "자유"
            var title = titleInput?.text.toString()
            var content = contentInput?.text.toString()
            postArticle(category, title, content, imageList)
        })

        // 뒤로가기 버튼 클릭시 게시판 목록으로 돌아가기
        val backBtn = view.findViewById<ImageButton>(R.id.backBtn)
        backBtn.contentDescription = "뒤로가기"
        backBtn.setOnClickListener {
//            val transaction = parentFragmentManager.beginTransaction()
//            transaction.replace(R.id.fragment_container, previousFragment)
//            transaction.addToBackStack(null)
//            transaction.commit()
        }


        // 제목과 내용 글자 수 체크 및 제한
        var titleCnt = view.findViewById<TextView>(R.id.titleCnt)
        var contentCnt = view.findViewById<TextView>(R.id.contentCnt)

        var titleCntValue = 0
        var contentCntValue = 0


        // 글자 수 체크 및 제한
        fun textWatcher() {
            titleInput.addTextChangedListener(object : TextWatcher {
                @SuppressLint("SetTextI18n")
                override fun afterTextChanged(s: Editable?) {
                    titleCntValue = titleInput.length()
                    titleCnt.text = "$titleCntValue / 30"

                    if (titleInput.text.length >= 30) {
                        Toast.makeText(requireContext(), "제목은 30자까지 입력 가능합니다.", Toast.LENGTH_LONG)
                            .show()
                    }
//                    Alert 코드
//                    if (titleInput.text.length >= 30) {
//                        val builder = AlertDialog.Builder(requireContext())
//                        builder.setMessage("30자 이내로 입력해주세요.").setPositiveButton("확인", null).show()
//                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // 입력하기 전에 호출됩니다.
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 입력 중에 호출됩니다.
                }
            })
            contentInput.addTextChangedListener(object : TextWatcher {
                @SuppressLint("SetTextI18n")
                override fun afterTextChanged(s: Editable?) {
                    contentCntValue = contentInput.length()
                    contentCnt.text = "$contentCntValue / 1500"

                    if (contentInput.text.length >= 1500) {
                        Toast.makeText(requireContext(), "내용은 1500자까지 입력 가능합니다.", Toast.LENGTH_LONG)
                            .show()
                    }
//                    Alert 코드
//                    if (titleInput.text.length >= 30) {
//                        val builder = AlertDialog.Builder(requireContext())
//                        builder.setMessage("30자 이내로 입력해주세요.").setPositiveButton("확인", null).show()
//                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // 입력하기 전에 호출됩니다.
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 입력 중에 호출됩니다.
                }
            })
        }
        textWatcher()

        regionFilter(view)

        return view
    }

    override fun onResume() {
        super.onResume()
        region = arguments?.getStringArrayList("region_full_list")?.get(0)
        addChip(region)
    }

    private fun addChip(test: String?) {
        if (test != null) {
            chipRegionGroup.addView(
                Chip(
                    requireContext(),
                    null,
                    R.style.REGION_CHIP_ICON
                ).apply {
                    text = test
                    isCloseIconVisible = false
                })
        }
    }

    private fun regionFilter(view: View) {
        val mActivity = activity as MainActivity
        regionFilterBtn = view.findViewById(R.id.regionFilterBtn)
        chipRegionGroup = view.findViewById(R.id.chipRegionGroup)

        regionFilterBtn.setOnClickListener {
            val regionFilterBottomSheet =
                RegionFilterBottomSheet(requireContext(), LIMITREGIONCNT)
            regionFilterBottomSheet.show(
                mActivity.supportFragmentManager,
                regionFilterBottomSheet.tag
            )
        }
    }

    private fun requestPermissions() {
        if (allPermissionsGranted()) {
            openGallery()
        } else {
            requestPermissions(
//                권한 설정 수정
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
//                arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
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
        return arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE).all {
            ContextCompat.checkSelfPermission(
                requireContext(), it
            ) == PackageManager.PERMISSION_GRANTED
        } || arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES).all {
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
//        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        startActivityForResult(intent, REQUEST_STORAGE)

    }

    @SuppressLint("NotifyDataSetChanged")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_STORAGE && resultCode == Activity.RESULT_OK && data != null) {
            val dataCheck = data.clipData
            Log.d("CommunityPostFragmentData", "$dataCheck")
            data.let { data ->

//                val imageUri = data.data
//                if (imageUri != null) {
//                    articleImg.setImageURI(imageUri)
//                    imgFile = uriToFile(imageUri)
//                    Log.d("CommunityPostFragmentImgFile", "$imgFile")
//
//                }
//                imgFile?.let { imageFiles.add(it) }
//
//                Log.d("CommunityPostFragmentImageFiles", "$imageFiles")


                val clipData = data.clipData

                if (clipData != null) {
                    for (index in 0 until clipData.itemCount) {
                        if (imageList.size >= maxImageCnt) {
                            Toast.makeText(
                                requireContext(),
                                "이미지는 최대 ${maxImageCnt}개까지 선택할 수 있습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            break
                        }
                        val imageUri = clipData.getItemAt(index).uri
                        if (imageUri != null) {
//                            articleImg.setImageURI(imageUri)
                            imgFile = uriToFile(imageUri)
                        }
                        imageList.add(imgFile)

                        Log.d("CommunityPostFragmentImageList", "$imageList")

                    }
                    Toast.makeText(
                        requireContext(),
                        "${imageList.size}개의 이미지가 선택되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val imageUri = data.data
                    if (imageUri != null) {
                        if (imageList.size >= maxImageCnt) {
                            Toast.makeText(
                                requireContext(),
                                "이미지는 최대 ${maxImageCnt}개까지 선택할 수 있습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            imgFile = uriToFile(imageUri)
                            return@let
                        }
                        imageList.add(imgFile)
                        Toast.makeText(
                            requireContext(),
                            "이미지가 선택되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                postImageAdapter.notifyDataSetChanged()

                val recyclerView: RecyclerView = view!!.findViewById(R.id.recyclerView)
                val context: Context = requireContext()
                postImageAdapter = PostImageAdapter(imageList, context)
                recyclerView.adapter = postImageAdapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())

                if (imageList.isNotEmpty()) {
                    recyclerView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.GONE
                }
                postImageAdapter.notifyDataSetChanged()

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


    private fun postArticle(
        category: String,
        title: String,
        content: String,
        files: ArrayList<File?>?
    ) {
        val retrofit = RetrofitClient.getClient()!!
        val communityPostService = retrofit.create(CommunityPostService::class.java)
        val userPK = UserData.getUserPK()

        var filePart: MultipartBody.Part? = null
        var file: File?
        val mediaType = "image/*".toMediaTypeOrNull()

//        val imageParts = mutableListOf<MultipartBody.Part>()
//
//        Log.d("CommunityPostFragmentFiles", "$imageList")
//        if (imageList.size >= 1) {
//            if (imageList?.get(0) != null) {
//                val requestFile = imageList[0]?.let { RequestBody.create(mediaType, it) }
//                filePart =
//                    MultipartBody.Part.createFormData("image", imageList[0]!!.name, requestFile!!)
//                Log.d("CommunityPostFragmentFilePart", "$filePart")
//
//                imageParts.add(filePart)
//            }
//        }
//        if (imageList.size >= 2) {
//            if (imageList?.get(1) != null) {
//                val requestFile = imageList[1]?.let { RequestBody.create(mediaType, it) }
//                filePart =
//                    MultipartBody.Part.createFormData("image", imageList[1]!!.name, requestFile!!)
//                imageParts.add(filePart)
//            }
//        }
//        if (imageList.size == 3) {
//            if (imageList?.get(2) != null) {
//                val requestFile = imageList[2]?.let { RequestBody.create(mediaType, it) }
//                filePart =
//                    MultipartBody.Part.createFormData("image", imageList[2]!!.name, requestFile!!)
//                imageParts.add(filePart)
//            }
//        }
//
//        Log.d("CommunityPostFragmentImageParts", "$imageParts")


//        파일 첨부
        val imageParts = MultipartBody.Builder().setType(MultipartBody.FORM)

        if (files != null && files.size > 0) {
            for ((i, imageFile) in files.withIndex()) {
                imageParts.addFormDataPart(
                    "images",
                    imageFile?.name,
                    RequestBody.create("image/*".toMediaTypeOrNull(), imageFile!!)
                )
            }
            val imageMultiPart = imageParts.build()
            if (files != null && files.size > 0) {
                for (i in 1..files.size) {
                    thelist[i - 1] = imageMultiPart.part(i - 1)
                }
            }
        }





        communityPostService.requestCommunityPost(
            ArticlePostRequest(
                userPK,
                category,
                title,
                content
            ), thelist[0], thelist[1], thelist[2]
        )
            .enqueue(object : Callback<CommunityPostResponse> {
                override fun onResponse(
                    call: Call<CommunityPostResponse>,
                    response: Response<CommunityPostResponse>
                ) {
                    val body = response.body()
                    Log.d("CommunityPostFragmentBody", "$body")
                }

                override fun onFailure(call: Call<CommunityPostResponse>, t: Throwable) {
                    Log.d("CommunityPostFragment", "게시글 작성 실패")
                }
            })
    }


}
