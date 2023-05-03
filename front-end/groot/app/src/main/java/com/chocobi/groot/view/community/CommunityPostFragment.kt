package com.chocobi.groot.view.community

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.chocobi.groot.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("DEPRECATION")
class CommunityPostFragment : Fragment() {

//    companion object {
//        private const val PICK_IMAGE_REQUEST = 1
//    }
//
//    private lateinit var imageView: ImageView
    //    Fragment에서 findViewById를 사용하려면 view 객체를 먼저 선언해야함
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_community_post, container, false)

//        imageView = view.findViewById(R.id.imageInput)
//        imageView.setOnClickListener {
//            openGallery()
//        }


        val toPostListBtn = view.findViewById<Button>(R.id.toPostListBtn)

        // 제목과 내용 글자 수 체크 및 제한
        var titleCnt = view.findViewById<TextView>(R.id.titleCnt)
        var contentCnt = view.findViewById<TextView>(R.id.contentCnt)

        var titleCntValue = 0
        var contentCntValue = 0


        //        retrofit 객체 만들기
        var retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var communityPostService = retrofit.create(CommunityPostService::class.java)

        val titleInput = view.findViewById<EditText>(R.id.titleInput)
        val contentInput = view.findViewById<EditText>(R.id.contentInput)

        // 글자 수 체크 및 제한
        fun textWatcher() {
            titleInput.addTextChangedListener(object: TextWatcher {
                @SuppressLint("SetTextI18n")
                override fun afterTextChanged(s: Editable?) {
                    titleCntValue = titleInput.length()
                    titleCnt.text = "$titleCntValue / 30"

                    if(titleInput.text.length >= 30) {
                        Toast.makeText(requireContext(),"제목은 30자까지 입력 가능합니다.", Toast.LENGTH_LONG).show()
                    }
//                    Alert 코드
//                    if (titleInput.text.length >= 30) {
//                        val builder = AlertDialog.Builder(requireContext())
//                        builder.setMessage("30자 이내로 입력해주세요.").setPositiveButton("확인", null).show()
//                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // 입력하기 전에 호출됩니다.
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 입력 중에 호출됩니다.
                }
            })
            contentInput.addTextChangedListener(object: TextWatcher {
                @SuppressLint("SetTextI18n")
                override fun afterTextChanged(s: Editable?) {
                    contentCntValue = contentInput.length()
                    contentCnt.text = "$contentCntValue / 1500"

                    if(contentInput.text.length >= 1500) {
                        Toast.makeText(requireContext(),"내용은 1500자까지 입력 가능합니다.", Toast.LENGTH_LONG).show()
                    }
//                    Alert 코드
//                    if (titleInput.text.length >= 30) {
//                        val builder = AlertDialog.Builder(requireContext())
//                        builder.setMessage("30자 이내로 입력해주세요.").setPositiveButton("확인", null).show()
//                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // 입력하기 전에 호출됩니다.
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // 입력 중에 호출됩니다.
                }
            })
        }
        textWatcher()

        // 등록 버튼 클릭 시 제목과 내용 입력값
        toPostListBtn.setOnClickListener(View.OnClickListener{
            // 요청 보내기
            communityPostService.requestLogin(titleInput.toString(), contentInput.toString()).enqueue(object:
                Callback<CommunityPostResponse>{
                // 통신 성공 시 실행되는 코드
                override fun onResponse(
                    call: Call<CommunityPostResponse>,
                    response: Response<CommunityPostResponse>
                ) {
                    TODO("Not yet implemented")
                }

                // 통신 실패 시 실행되는 코드
                override fun onFailure(call: Call<CommunityPostResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
                })
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


    return view
    }

//    private fun openGallery() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, PICK_IMAGE_REQUEST)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
//            val imageUri = data.data
//            val imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
//            imageView.setImageBitmap(imageBitmap)
//        }
//    }
}
