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
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.community.adapter.RecyclerViewAdapter
import com.chocobi.groot.view.community.model.Articles
import com.chocobi.groot.view.community.model.CommunityArticleListResponse
import com.chocobi.groot.view.community.model.ArticleContent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CommunityTab4Fragment : Fragment() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var frameLayoutProgress: FrameLayout
    private lateinit var getData: CommunityArticleListResponse

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_community_tab4, container, false)
        findViews(view)
        setListeners()
        initList()
        reload()

        showProgress()

//                retrofit 객체 만들기
        var retrofit = RetrofitClient.getClient()!!


        var communityArticleListService = retrofit.create(CommunityArticleListService::class.java)
        var communityArticleCategory = "Tip"
        var communityArticlePage = 0
        var communityArticleSize = 10

        communityArticleListService.requestCommunityArticleList(communityArticleCategory,communityArticlePage,communityArticleSize).enqueue(object :
            Callback<CommunityArticleListResponse> {
            override fun onResponse(call: Call<CommunityArticleListResponse>, response: Response<CommunityArticleListResponse>) {
                if (response.code() == 200) {
                    Log.d("CommunityTab4Fragment", "성공")
                    val checkResponse =  response.body()?.articles?.content
                    getData = response.body()!!
                    Log.d("CommunityTab4Fragment", "$checkResponse")

                    val totalElements = getData.articles.total // 전체 데이터 수
                    val currentPage = communityArticlePage // 현재 페이지 번호
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
                    Log.d("CommunityTab4Fragment", "실패1")
                }
            }

            override fun onFailure(call: Call<CommunityArticleListResponse>, t: Throwable) {
                Log.d("CommunityTab4Fragment", "실패2")
            }

        })

        return view
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
        var retrofit = RetrofitClient.getClient()!!


        var communityArticleListService = retrofit.create(CommunityArticleListService::class.java)
        var communityArticleCategory = "Tip"
        var communityArticlePage = 0
        var communityArticleSize = 10

        communityArticleListService.requestCommunityArticleList(communityArticleCategory,communityArticlePage,communityArticleSize).enqueue(object :
            Callback<CommunityArticleListResponse> {
            override fun onResponse(call: Call<CommunityArticleListResponse>, response: Response<CommunityArticleListResponse>) {
                if (response.code() == 200) {
                    Log.d("CommunityTab4Fragment", "성공")
                    val checkResponse =  response.body()?.articles?.content
                    getData = response.body()!!
                    Log.d("CommunityTab4Fragment", "$checkResponse")

                    val list = createDummyData(0, 10)
                    ThreadUtil.startUIThread(1000) {
                        adapter.reload(list)
                        hideProgress()
                    }
                } else {
                    Log.d("CommunityTab4Fragment", "실패1")
                }
            }

            override fun onFailure(call: Call<CommunityArticleListResponse>, t: Throwable) {
                Log.d("CommunityTab4Fragment", "실패2")
            }

        })
    }


    private var communityArticlePage = 0 // 초기 페이지 번호를 0으로 설정합니다.

    private var isLastPage = false // 마지막 페이지인지 여부를 저장하는 변수입니다.

    private fun loadMore() {
        if (isLastPage) { // 마지막 페이지라면, 로딩을 멈춥니다.
            return
        }

        showProgress()

        // 페이지 번호를 1 증가시킵니다.
        communityArticlePage++

        // Retrofit을 사용하여 새로운 데이터를 받아옵니다.
        var retrofit = RetrofitClient.getClient()!!


        var communityArticleListService = retrofit.create(CommunityArticleListService::class.java)
        var communityArticleCategory = "Tip"
        var communityArticleSize = 10

        communityArticleListService.requestCommunityArticleList(communityArticleCategory, communityArticlePage, communityArticleSize).enqueue(object :
            Callback<CommunityArticleListResponse> {
            override fun onResponse(call: Call<CommunityArticleListResponse>, response: Response<CommunityArticleListResponse>) {
                if (response.code() == 200) {
                    Log.d("loadmore", "성공")
                    val checkResponse =  response.body()?.articles?.content
                    getData = response.body()!!
                    Log.d("loadmore", "$checkResponse")

                    val totalElements = getData.articles.total // 전체 데이터 수
                    val currentPage = communityArticlePage // 현재 페이지 번호
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

            override fun onFailure(call: Call<CommunityArticleListResponse>, t: Throwable) {
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

    private fun createDummyData(offset: Int, limit: Int): MutableList<CommunityArticleListResponse> {
        val list: MutableList<CommunityArticleListResponse> = mutableListOf()

        // API response를 이용하여 데이터 생성
        val contents = getData.articles.content
        for (i in offset until (offset + limit)) {
            if (i >= contents.size) {
                break
            }
            val article = contents[i]
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