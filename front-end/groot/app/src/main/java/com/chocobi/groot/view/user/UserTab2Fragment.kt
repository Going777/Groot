package com.chocobi.groot.view.user

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
import com.chocobi.groot.view.community.adapter.RecyclerViewAdapter
import com.chocobi.groot.view.community.model.Articles
import com.chocobi.groot.view.community.model.CommunityArticleListResponse
import com.chocobi.groot.view.community.model.ArticleContent
import com.chocobi.groot.view.user.model.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserTab2Fragment : Fragment() {

    private val TAG = "UserTab2Fragment"
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var frameLayoutProgress: FrameLayout
    private lateinit var getData: CommunityArticleListResponse
    private var REQUESTPAGESIZE = 10
    private var communityArticlePage = 0 // 초기 페이지 번호를 0으로 설정합니다.
    private var isLastPage = false // 마지막 페이지인지 여부를 저장하는 변수입니다.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_tab2, container, false)
        findViews(view)
        setListeners()
        initList()
        reload()

        showProgress()

        requestUserArticleList("load")

        return view
    }

    private fun requestUserArticleList(usage: String) {
        if (usage == "loadMore") {
            communityArticlePage++
        } else {
            communityArticlePage = 0
        }
//                retrofit 객체 만들기
        var retrofit = RetrofitClient.getClient()!!
        var userService = retrofit.create(UserService::class.java)

        userService.requestUserArticleList(communityArticlePage, REQUESTPAGESIZE).enqueue(object :
            Callback<CommunityArticleListResponse> {
            override fun onResponse(
                call: Call<CommunityArticleListResponse>,
                response: Response<CommunityArticleListResponse>
            ) {
                if (response.code() == 200) {
                    Log.d(TAG, "성공")
                    getData = response.body()!!
                    val list = createDummyData(0, REQUESTPAGESIZE)
                    if (usage != "reload") {
                        val totalElements = getData.articles.total // 전체 데이터 수
                        val currentPage = communityArticlePage // 현재 페이지 번호
                        val isLast =
                            (currentPage + 1) * REQUESTPAGESIZE >= totalElements // 마지막 페이지 여부를 판단합니다.
                        if (isLast) { // 마지막 페이지라면, isLastPage를 true로 설정합니다.
                            isLastPage = true
                        }
                    }
                    if (usage == "loadMore") {
                        ThreadUtil.startUIThread(1000) {
                            adapter.loadMore(list)
                            hideProgress()
                        }
                    } else {
                        ThreadUtil.startUIThread(1000) {
                            adapter.reload(list)
                            hideProgress()
                        }

                    }
                } else {
                    Log.d(TAG, "실패1")
                }
            }

            override fun onFailure(call: Call<CommunityArticleListResponse>, t: Throwable) {
                Log.d(TAG, "실패2")
            }
        })
    }

    private fun findViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerView)
        frameLayoutProgress = view.findViewById(R.id.frameLayoutProgress)
    }

    private fun setListeners() {
        swipeRefreshLayout.setOnRefreshListener {
            reload()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initList() {
        adapter = RecyclerViewAdapter()
        adapter.delegate = object : RecyclerViewAdapter.RecyclerViewAdapterDelegate {
            override fun onLoadMore() {
                loadMore()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun reload() {
        requestUserArticleList("reload")
    }


    private fun loadMore() {
        if (isLastPage) { // 마지막 페이지라면, 로딩을 멈춥니다.
            return
        }
        showProgress()
        requestUserArticleList("loadMore")
    }


    private fun showProgress() {
        frameLayoutProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        frameLayoutProgress.visibility = View.GONE
    }

    private fun createDummyData(
        offset: Int,
        limit: Int
    ): MutableList<CommunityArticleListResponse> {
        val list: MutableList<CommunityArticleListResponse> = mutableListOf()

        // API response를 이용하여 데이터 생성
        val contents = getData.articles.content
        for (i in offset until (offset + limit)) {
            if (i >= contents.size) {
                break
            }
            val article = contents[i]
            // 이미지가 있을 때는 visibility를 visible로 설정하고, 없을 때는 gone으로 설정합니다.
            val visibility = if (article.img.isNullOrEmpty()) View.GONE else View.VISIBLE

            val communityArticleListResponse = CommunityArticleListResponse(
                articles = Articles(
                    content = listOf(
                        ArticleContent(
                            articleId = article.articleId,
                            category = article.category,
                            userPK = article.userPK,
                            nickName = article.nickName,
                            title = article.title,
                            tags = article.tags,
                            views = article.views,
                            commentCnt = article.commentCnt,
                            bookmark = article.bookmark,
                            shareRegion = article.shareRegion,
                            shareStatus = article.shareStatus,
                            createTime = article.createTime,
                            updateTime = article.updateTime,
                            img = article.img
                        )
                    ),
                    total = getData.articles.total,
                    pageable = getData.articles.pageable
                ),
                result = getData.result,
                msg = getData.msg
            )
            list.add(communityArticleListResponse)
        }
        return list
    }
}