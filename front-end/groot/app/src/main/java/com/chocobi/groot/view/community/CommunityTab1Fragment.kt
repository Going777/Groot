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
import com.chocobi.groot.view.community.adapter.RecyclerViewAdapter
import com.chocobi.groot.view.community.model.Articles
import com.chocobi.groot.view.community.model.CommunityArticleListResponse
import com.chocobi.groot.view.community.model.Content
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CommunityTab1Fragment : Fragment() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var frameLayoutProgress: FrameLayout
    private lateinit var getData: CommunityArticleListResponse

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_community_tab2, container, false)
        findViews(view)
        setListeners()
        initList()
        reload()

        //        retrofit 객체 만들기
        var retrofit = Retrofit.Builder()
            .baseUrl(GlobalVariables.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var communityArticleListService = retrofit.create(CommunityArticleListService::class.java)
        var communityArticleCategory = "자유"
        var communityArticlePage = 0
        var communityArticleSize = 10

        communityArticleListService.requestCommunityArticleList(communityArticleCategory,communityArticlePage,communityArticleSize).enqueue(object :
            Callback<CommunityArticleListResponse> {

            override fun onResponse(
                call: Call<CommunityArticleListResponse>,
                response: Response<CommunityArticleListResponse>
            ) {
                if (response.code() == 200) {
                    Log.d("CommunityTab2Fragment", "성공")
                    val checkResponse =  response.body()?.articles
                    getData = response.body()!!
                    Log.d("CommunityTab2Fragment", "$checkResponse")
                } else {
                    Log.d("CommunityTab2Fragment", "실패1")
                }
            }

            override fun onFailure(call: Call<CommunityArticleListResponse>, t: Throwable) {
                Log.d("CommunityTab2Fragment", "실패2")
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
        showProgress()

        // get data from server

        ThreadUtil.startThread {
            Log.d("???", "reload 10 items")
            val list = createDummyData(0, 10)
            ThreadUtil.startUIThread(1000) {
                adapter.reload(list)
                hideProgress()

            }
        }
    }

    private fun loadMore() {

        showProgress()

        // get data from server

        ThreadUtil.startThread {
            Log.d("???", "reload 10 items")

            val list = createDummyData(adapter.itemCount, getData.articles.content.size)
            ThreadUtil.startUIThread(1000) {
                adapter.loadMore(list)
                hideProgress()
            }
        }

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
        for (i in offset until (offset + limit)) {
            val article = getData.articles.content[i - offset]
            val communityArticleListResponse = CommunityArticleListResponse(
                articles = Articles(
                    content = listOf(
                        Content(
                            articleId = article.articleId,
                            category = article.category,
                            userPK = article.userPK,
                            img = article.img,
                            nickName = article.nickName,
                            title = article.title,
                            tags = article.tags,
                            views = article.views,
                            commentCnt = article.commentCnt,
                            bookmark = article.bookmark,
                            createTime = article.createTime,
                            updateTime = article.updateTime
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
