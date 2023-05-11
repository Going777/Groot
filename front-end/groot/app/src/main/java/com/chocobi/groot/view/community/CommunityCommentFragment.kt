package com.chocobi.groot.view.community

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.community.adapter.CommentAdapter
import com.chocobi.groot.view.community.model.Comment
import com.chocobi.groot.view.community.model.CommentContent
import com.chocobi.groot.view.community.model.CommunityCommentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityCommentFragment()  : Fragment() {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommentAdapter
    private lateinit var frameLayoutProgress: FrameLayout
    private lateinit var getData: CommunityCommentResponse
    private var articleId: Int = 0
    private lateinit var frameLayoutComment: FrameLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_community_comment, container, false)
        findViews(view)
        setListeners()
        initList()

        showProgress()

        frameLayoutComment = view.findViewById(R.id.frameLayoutComment)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById<RecyclerView>(R.id.commentRecycleView)
        frameLayoutProgress = view.findViewById(R.id.frameLayoutProgress)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        val args = arguments
        if (args != null) {
            articleId = args.getInt("articleId")
            Log.d("CommunityCommentFragment", "$articleId")
        }

        // retrofit 객체 만들기
        var retrofit = RetrofitClient.getClient()!!
        var communityCommentService = retrofit.create(CommunityCommentService::class.java)

        communityCommentService.requestCommunityComment(articleId).enqueue(object :
            Callback<CommunityCommentResponse>  {
            override fun onResponse(call: Call<CommunityCommentResponse>, response: Response<CommunityCommentResponse>) {
                if (response.code() == 200) {
                    Log.d("CommunityCommentFragment", "성공")
                    val checkResponse =  response.body()?.comment
                    getData = response.body()!!
                    Log.d("CommunityCommentFragment", "$checkResponse")

                    val list = createDummyData()
                    ThreadUtil.startUIThread(1000) {
                        adapter.reload(list)
                        hideProgress()

                    }
                } else {
                    Log.d("CommunityCommentFragment", "실패1")
                }
            }
            override fun onFailure(call: Call<CommunityCommentResponse>, t: Throwable) {
                Log.d("CommunityCommentFragment실패", "실패2")
            }
        })
        return view
    }


    private fun findViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById<RecyclerView>(R.id.commentRecycleView)
        frameLayoutProgress = view.findViewById(R.id.frameLayoutProgress)
    }

    private fun setListeners() {
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initList() {
        adapter = CommentAdapter(recyclerView)
        recyclerView.adapter = adapter // RecyclerView에 Adapter 설정
        val size = adapter.itemCount
        recyclerView.scrollToPosition(size - 1)
        adapter.delegate = object : CommentAdapter.RecyclerViewAdapterDelegate {
            override fun onLoadMore() {
            }

            fun reload(mutableList: MutableList<CommunityCommentResponse>) {
                TODO("Not yet implemented")
            }
            fun loadMore(mutableList: MutableList<CommunityCommentResponse>) {
                TODO("Not yet implemented")
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
    private fun showProgress() {
        frameLayoutProgress.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        frameLayoutProgress.visibility = View.GONE
    }
    private fun createDummyData(): MutableList<CommunityCommentResponse> {
        val list: MutableList<CommunityCommentResponse> = mutableListOf()

// API response를 이용하여 데이터 생성
        val comments = getData.comment
        Log.d("CommunityCommentFragmentComments", comments.toString())
        for (commentitem in comments) {
            val communityCommentResponse = CommunityCommentResponse(
                comment = listOf(
                    Comment(
                        userPK = commentitem.userPK ?: 0,
                        nickName = commentitem.nickName ?: "",
                        commentId = commentitem.commentId ?: 0,
                        content = commentitem.content ?: "",
                        profile = commentitem.profile ?: "",
                        createTime = commentitem.createTime,
                        updateTime = commentitem.updateTime
                    )
                ),
                result = getData.result,
                msg = getData.msg
            )
            list.add(communityCommentResponse)
        }
        Log.d("CommunityCommentFragmentList", list.toString())
        return list
    }
}

