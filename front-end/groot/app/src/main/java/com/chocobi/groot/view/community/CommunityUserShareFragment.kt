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
import com.chocobi.groot.view.community.adapter.ShareItemAdapter
import com.chocobi.groot.view.community.model.CommunityShareItemResponse
import com.chocobi.groot.view.community.model.ShareArticles
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityUserShareFragment()  : Fragment() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShareItemAdapter
    private lateinit var frameLayoutProgress: FrameLayout
    private lateinit var getData: CommunityShareItemResponse
    private var articleId: Int = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_community_user_share, container, false)
        findViews(view)
        setListeners()
        initList()

        showProgress()

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById<RecyclerView>(R.id.shareItemList)
        frameLayoutProgress = view.findViewById(R.id.frameLayoutProgress)

        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        val args = arguments
        if (args != null) {
            articleId = args.getInt("articleId")
            Log.d("CommunityCommentFragment", "$articleId")
        }

        Log.d("CommunityUserShareFragmentArguments", "$arguments")

        Log.d("CommunityUserShareFragment", "$articleId")



        //                retrofit 객체 만들기
        var retrofit = RetrofitClient.getClient()!!


        var communityShareItemService = retrofit.create(CommunityShareItemService::class.java)
        var communityArticleId = 32

        communityShareItemService.requestCommunityShareItem(articleId).enqueue(object :
            Callback<CommunityShareItemResponse>  {
            override fun onResponse(call: Call<CommunityShareItemResponse>, response: Response<CommunityShareItemResponse>) {
                if (response.code() == 200) {
                    Log.d("CommunityUserShareFragment", "성공")
                    val checkResponse =  response.body()?.articles
                    getData = response.body()!!
                    Log.d("CommunityUserShareFragment", "$checkResponse")



                    val list = createDummyData()
                    ThreadUtil.startUIThread(1000) {
                        adapter.reload(list)
                        hideProgress()
                    }
                } else {
                    Log.d("CommunityUserShareFragment", "실패1")
                }
            }


            override fun onFailure(call: Call<CommunityShareItemResponse>, t: Throwable) {
                Log.d("CommunityUserShareFragment", "실패2")
            }

        })


        return view
    }

    private fun findViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById<RecyclerView>(R.id.shareItemList)
        frameLayoutProgress = view.findViewById(R.id.frameLayoutProgress)


    }

    private fun setListeners() {
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initList() {
        adapter = ShareItemAdapter(recyclerView)

        recyclerView.adapter = adapter // RecyclerView에 Adapter 설정
        val size = adapter.itemCount
        recyclerView.scrollToPosition(size - 1)

        adapter.delegate = object : ShareItemAdapter.RecyclerViewAdapterDelegate {
            override fun onLoadMore() {
            }

            fun reload(mutableList: MutableList<CommunityShareItemResponse>) {
                TODO("Not yet implemented")
            }

            fun loadMore(mutableList: MutableList<CommunityShareItemResponse>) {
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

    private fun createDummyData(): MutableList<CommunityShareItemResponse> {
        val list: MutableList<CommunityShareItemResponse> = mutableListOf()

        // API response를 이용하여 데이터 생성
        val articles = getData.articles
        for (article in articles) {
            val communityShareItemResponse = CommunityShareItemResponse(
                articles = listOf(
                    ShareArticles(
                        userPK = article.userPK ?: 0,
                        nickname = article.nickname ?: "",
                        articleId = article.articleId ?: 0,
                        title = article.title ?: "",
                        img = article.img ?: ""
                    )
                ),
                result = getData.result,
                msg = getData.msg
            )
            list.add(communityShareItemResponse)
        }
        Log.d("CommunityUserShareFragmentList", list.toString())
        return list
    }


}

