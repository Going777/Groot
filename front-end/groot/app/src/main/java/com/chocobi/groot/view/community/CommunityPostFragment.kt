package com.chocobi.groot.view.community

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil.setContentView
import com.chocobi.groot.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CommunityPostFragment : Fragment() {

//    Fragment에서 findViewById를 사용하려면 view 객체를 먼저 선언해야함
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_community_post, container, false)
        val toPostListBtn = view.findViewById<Button>(R.id.toPostListBtn)

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

        val titleInput = view.findViewById<EditText>(R.id.titleInput).toString()
        val contentInput = view.findViewById<EditText>(R.id.contentInput).toString()

//        fun textWatcher() {
//            titleInput.addTextChangedListener(object: TextWatcher {
//                override fun afterTextChanged(s: Editable?) {
//                    titleCntValue++
//                }
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                    // 입력하기 전에 호출됩니다.
//                }
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    // 입력 중에 호출됩니다.
//                }
//            })
//        }


    // 등록 버튼 클릭 시 제목과 내용 입력값
        toPostListBtn.setOnClickListener(View.OnClickListener{
            // 요청 보내기
            communityPostService.requestLogin(titleInput, contentInput).enqueue(object:
                Callback<CommunityPost>{
                // 통신 성공 시 실행되는 코드
                override fun onResponse(
                    call: Call<CommunityPost>,
                    response: Response<CommunityPost>
                ) {
                    TODO("Not yet implemented")
                }

                // 통신 실패 시 실행되는 코드
                override fun onFailure(call: Call<CommunityPost>, t: Throwable) {
                    TODO("Not yet implemented")
                }
                })
        })


        return view
    }
}
