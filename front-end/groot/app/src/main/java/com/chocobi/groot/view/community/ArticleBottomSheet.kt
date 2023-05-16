package com.chocobi.groot.view.community

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.chocobi.groot.R
import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.community.model.CommunityService
import com.chocobi.groot.view.user.model.PasswordRequest
import com.chocobi.groot.view.user.model.UserService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ArticleBottomSheet (context: Context, private val articleId: Int) : BottomSheetDialogFragment() {

    private val TAG = "ArticleBottomSheet"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.bottom_sheet_article, container, false)


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.findViewById<TextView>(R.id.editArticle)?.setOnClickListener {

        }
        view?.findViewById<TextView>(R.id.deleteArticle)?.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("글을 삭제하시겠습니까?")
            dialog.setPositiveButton("네") { dialog, which ->
                deleteArticle(articleId)
                requireActivity().supportFragmentManager.popBackStack()
                dialog.dismiss()
            }
            dialog.setNegativeButton("아니요") { dialog, which ->
                dialog.dismiss()
            }
            dialog.show()

        }
    }

    private fun deleteArticle(articleId: Int) {
        val retrofit = RetrofitClient.getClient()!!
        val communityService = retrofit.create(CommunityService::class.java)

        communityService.deleteArticle(articleId)
            .enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.code() == 200) {
                        val res = response.body()
                        if (res != null) {
                            Log.d("CommunityDetailFragment", "onResponse() 삭제 성공 $res")
                            Log.d("CommunityDetailFragment", "onResponse() 삭제 성공 ${res?.msg}")
//                            성공했으면 게시글 페이지로 돌아가야 함


                        }
                    } else {
                        Log.d("CommunityDetailFragment", "onResponse() 삭제 실패1 $response")
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Log.d("CommunityDetailFragment", "onResponse() 삭제 실패2")
                }
            })
    }
}