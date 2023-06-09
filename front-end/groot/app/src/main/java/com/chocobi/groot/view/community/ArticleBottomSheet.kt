package com.chocobi.groot.view.community

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.util.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.community.model.CommunityService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ArticleBottomSheet(
    context: Context,
    private val articleId: Int,
    private val isShareCategory: Boolean,
    private val isShared: Boolean?,
    private val listener: ArticleBottomSheetListener
) :
    BottomSheetDialogFragment() {

    private val TAG = "ArticleBottomSheet"
    private lateinit var mActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.bottom_sheet_article, container, false)
        mActivity = activity as MainActivity


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val userPK = UserData.getUserPK()
        view?.findViewById<TextView>(R.id.editArticle)?.setOnClickListener {
            mActivity.changeFragment("community_edit_post")
            dismiss()
        }

        var shareArticleText = view?.findViewById<TextView>(R.id.shareArticle)
        if (isShareCategory) {
            if (isShared == false) {
                shareArticleText?.text = "나눔 완료"
            } else {
                shareArticleText?.text = "나눔 취소"
            }
            shareArticleText?.setOnClickListener {
                changeShareStatus(articleId, userPK)

                dismiss()
            }
        } else {
            shareArticleText?.visibility = View.GONE
        }

        view?.findViewById<TextView>(R.id.deleteArticle)?.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("글을 삭제하시겠습니까?")
            dialog.setPositiveButton("네") { dialog, which ->
                deleteArticle(articleId)
                requireActivity().supportFragmentManager.popBackStack()
                dialog.dismiss()
                dismiss()
            }
            dialog.setNegativeButton("아니요") { dialog, which ->
                dialog.dismiss()
                dismiss()
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

    private fun changeShareStatus(articleId: Int, userPK: Int) {
        val retrofit = RetrofitClient.getClient()
        val communityShareStatusService = retrofit?.create(CommunityShareStatusService::class.java)
        communityShareStatusService?.requestCommunityShareStatus(
            ShareStatusRequest(
                articleId,
                userPK
            )
        )
            ?.enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.code() == 200) {
                        listener.onGetDetailRequested()
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Log.d(TAG, "나눔상태 변경 실패")
                }
            })

    }
}