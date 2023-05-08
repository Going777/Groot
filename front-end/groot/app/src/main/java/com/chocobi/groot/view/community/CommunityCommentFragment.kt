package com.chocobi.groot.view.community

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
import com.chocobi.groot.view.community.adapter.RecyclerViewAdapter
import com.chocobi.groot.view.community.model.Comment
import com.chocobi.groot.view.community.model.CommentContent
import com.chocobi.groot.view.community.model.CommunityCommentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityCommentFragment  : Fragment() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommentAdapter
    private lateinit var frameLayoutProgress: FrameLayout
    private lateinit var getData: CommunityCommentResponse
    private var articleId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_community_comment, container, false)
        findViews(view)
        setListeners()
        initList()
        reload()

        showProgress()

        val args = arguments
        if (args != null) {
            articleId = args.getInt("articleId")
            Log.d("CommunityCommentFragment", "$articleId")
        }

        Log.d("CommunityCommentFragmentArguments", "$arguments")

        Log.d("CommunityCommentFragment", "$articleId")



        //                retrofit 객체 만들기
        var retrofit = RetrofitClient.getClient()!!


        var communityCommentService = retrofit.create(CommunityCommentService::class.java)
        var communityArticleId = 37
        var communityCommentPage = 0
        var communityCommentSize = 10

        communityCommentService.requestCommunityComment(articleId,communityCommentPage,communityCommentSize).enqueue(object :
            Callback<CommunityCommentResponse>  {
            override fun onResponse(call: Call<CommunityCommentResponse>, response: Response<CommunityCommentResponse>) {
                if (response.code() == 200) {
                    Log.d("CommunityCommentFragment", "성공")
                    val checkResponse =  response.body()?.comment?.content
                    getData = response.body()!!
                    Log.d("CommunityCommentFragment", "$checkResponse")


                    val totalElements = getData.comment.total // 전체 데이터 수
                    val currentPage = communityCommentPage // 현재 페이지 번호
                    val pageSize = 10 // 페이지 당 아이템 수
                    val isLast = (currentPage + 1) * pageSize >= totalElements // 마지막 페이지 여부를 판단합니다.

                    if (isLast) { // 마지막 페이지라면, isLastPage를 true로 설정합니다.
                        isLastPage = true
                    }
                    val list = createDummyData(0, 10)
                    ThreadUtil.startUIThread(1000) {
                        adapter.reload(list)
                        hideProgress()
                    }
                } else {
                    Log.d("CommunityCommentFragment", "실패1")
                }
            }

            override fun onFailure(call: Call<CommunityCommentResponse>, t: Throwable) {
                Log.d("CommunityCommentFragment", "실패2")
            }

        })


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

    private fun findViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.commentRecycleView)
        frameLayoutProgress = view.findViewById(R.id.frameLayoutProgress)
    }

    private fun setListeners() {
        swipeRefreshLayout.setOnRefreshListener {
            reload()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initList() {
        adapter = CommentAdapter(recyclerView)
        adapter.delegate = object : CommentAdapter.RecyclerViewAdapterDelegate {
            override fun onLoadMore() {
                loadMore()
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

    private fun reload() {
        var retrofit = RetrofitClient.getClient()!!

        var communityCommentService = retrofit.create(CommunityCommentService::class.java)

        var communityArticleId = 37
        var communityCommentPage = 0
        var communityCommentSize = 10

        val args = arguments
        if (args != null) {
            articleId = args.getInt("articleId")
            Log.d("CommunityCommentFragment", "$articleId")
        }

        communityCommentService.requestCommunityComment(articleId,communityCommentPage,communityCommentSize).enqueue(object :
            Callback<CommunityCommentResponse> {
            override fun onResponse(call: Call<CommunityCommentResponse>, response: Response<CommunityCommentResponse>) {
                if (response.code() == 200) {
                    Log.d("CommunityCommentFragment", "성공")
                    val checkResponse =  response.body()?.comment?.content
                    getData = response.body()!!
                    Log.d("CommunityCommentFragment", "$checkResponse")

                    val list = createDummyData(0, 10)
                    ThreadUtil.startUIThread(1000) {
                        adapter.reload(list)
                        hideProgress()
                    }
                } else {
                    Log.d("CommunityCommentFragment", "실패1")
                }
            }

            override fun onFailure(call: Call<CommunityCommentResponse>, t: Throwable) {
                Log.d("CommunityCommentFragment", "실패2")
            }

        })
    }


    private var communityCommentPage = 0 // 초기 페이지 번호를 0으로 설정합니다.

    private var isLastPage = false // 마지막 페이지인지 여부를 저장하는 변수입니다.

    private fun loadMore() {
        if (isLastPage) { // 마지막 페이지라면, 로딩을 멈춥니다.
            return
        }

        showProgress()

        // 페이지 번호를 1 증가시킵니다.
        communityCommentPage++

        // Retrofit을 사용하여 새로운 데이터를 받아옵니다.
        var retrofit = RetrofitClient.getClient()!!

        var communityCommentService = retrofit.create(CommunityCommentService::class.java)
        var communityArticleId = 37
        var communityCommentPage = 0
        var communityCommentSize = 10

        communityCommentService.requestCommunityComment(articleId, communityCommentPage, communityCommentSize).enqueue(object :
            Callback<CommunityCommentResponse> {
            override fun onResponse(call: Call<CommunityCommentResponse>, response: Response<CommunityCommentResponse>) {
                if (response.code() == 200) {
                    Log.d("loadmore", "성공")
                    val checkResponse =  response.body()?.comment?.content
                    getData = response.body()!!
                    Log.d("loadmore", "$checkResponse")

                    val totalElements = getData.comment.total // 전체 데이터 수
                    val currentPage = communityCommentPage // 현재 페이지 번호
                    val pageSize = 10 // 페이지 당 아이템 수
                    val isLast = (currentPage + 1) * pageSize >= totalElements // 마지막 페이지 여부를 판단합니다.
                    if (isLast) { // 마지막 페이지라면, isLastPage를 true로 설정합니다.
                        isLastPage = true
                    }

                    val list = createDummyData(0, 10)

                    ThreadUtil.startUIThread(1000) {
                        adapter.loadMore(list)
                        hideProgress()
                    }
                } else {
                    Log.d("loadmore", "실패1")
                }
            }

            override fun onFailure(call: Call<CommunityCommentResponse>, t: Throwable) {
                Log.d("loadmore", "실패2")
            }

        })
    }



    private fun showProgress() {
        frameLayoutProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        frameLayoutProgress.visibility = View.GONE
    }

    private fun createDummyData(offset: Int, limit: Int): MutableList<CommunityCommentResponse> {
        val list: MutableList<CommunityCommentResponse> = mutableListOf()

        // API response를 이용하여 데이터 생성
        val contents = getData.comment.content
        for (i in offset until (offset + limit)) {
            if (i >= contents.size) {
                break
            }
            val commentitem = contents[i]
            val communityCommentResponse = CommunityCommentResponse(
                comment = Comment(
                    content = listOf(
                        CommentContent(
                            commentId = commentitem.commentId,
                            userPK = commentitem.userPK,
                            content = commentitem.content,
                            nickName = commentitem.nickName,
                            createTime = commentitem.createTime,
                            updateTime = commentitem.updateTime,
                            profile = commentitem.profile
                        )
                    ),
                    total = getData.comment.total,
                    pageable = getData.comment.pageable
                ),
                result = getData.result,
                msg = getData.msg
            )
            list.add(communityCommentResponse)
        }
        return list
    }


}

