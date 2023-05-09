package com.chocobi.groot.view.community

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.community.adapter.PopularTagAdapter
import com.chocobi.groot.view.community.adapter.RecyclerViewAdapter
import com.chocobi.groot.view.community.model.Articles
import com.chocobi.groot.view.community.model.CommunityArticleListResponse
import com.chocobi.groot.view.community.model.ArticleContent
import com.chocobi.groot.view.community.model.CommunityService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CommunityTab2Fragment : Fragment() {
    private val TAG = "CommunityTab2Fragment"

    private lateinit var communitySearchView: SearchView
    private lateinit var communityRecyclerView: RecyclerView
    private lateinit var popularTagAdapter: PopularTagAdapter
    private lateinit var popularTagSection: LinearLayout
    private lateinit var overlayView: View

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var frameLayoutProgress: FrameLayout
    private lateinit var getData: CommunityArticleListResponse
    private var communityArticlePage = 0 // 초기 페이지 번호를 0으로 설정합니다.
    private var isLastPage = false // 마지막 페이지인지 여부를 저장하는 변수입니다.

    private var keyword: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_community_tab2, container, false)
        Log.d("CommunityTab2Fragment", "onCreateView()")
        findViews(view)
        //        태그 데이터 세팅
        createPopularTagData()
//        서치뷰 제어
        searchViewListner()
        setListeners()
        initList()
//        reload()

        showProgress()

//                retrofit 객체 만들기
        var retrofit = RetrofitClient.getClient()!!
        var communityArticleListService = retrofit.create(CommunityArticleListService::class.java)
        var communityArticleCategory = "자유"
        var communityArticlePage = 0
        var communityArticleSize = 10

        communityArticleListService.requestCommunityArticleList(
            communityArticleCategory,
            communityArticlePage,
            communityArticleSize
        ).enqueue(object :
            Callback<CommunityArticleListResponse> {
            override fun onResponse(
                call: Call<CommunityArticleListResponse>,
                response: Response<CommunityArticleListResponse>
            ) {
                if (response.code() == 200) {
                    Log.d(TAG, "성공")
                    val checkResponse = response.body()?.articles?.content
                    getData = response.body()!!
                    Log.d(TAG, "$checkResponse")

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
                    Log.d(TAG, "실패1")
                }
            }

            override fun onFailure(call: Call<CommunityArticleListResponse>, t: Throwable) {
                Log.d(TAG, "실패2")
            }

        })

        return view
    }

    private fun findViews(view: View) {
        communitySearchView = view.findViewById(R.id.communitySearchView)
        communityRecyclerView = view.findViewById(R.id.communityRecyclerView)
        popularTagSection = view.findViewById(R.id.popularTagSection)
        popularTagAdapter = PopularTagAdapter(createPopularTagData())
        communityRecyclerView.adapter = popularTagAdapter
        overlayView = view.findViewById(R.id.overlayView)

        communityRecyclerView.setHasTransientState(true)
        communityRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerView)
        frameLayoutProgress = view.findViewById(R.id.frameLayoutProgress)
    }

    private fun setListeners() {
        Log.d("CommunityTab2Fragment", "setListeners()")
        swipeRefreshLayout.setOnRefreshListener {
            reload()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initList() {
        Log.d("CommunityTab2Fragment", "initList()")
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
        Log.d("CommunityTab2Fragment", "reload()")
        var retrofit = RetrofitClient.getClient()!!


        var communityArticleListService = retrofit.create(CommunityArticleListService::class.java)
        var communityArticleCategory = "자유"
        var communityArticlePage = 0
        var communityArticleSize = 10

        communityArticleListService.requestCommunityArticleList(
            communityArticleCategory,
            communityArticlePage,
            communityArticleSize
        ).enqueue(object :
            Callback<CommunityArticleListResponse> {
            override fun onResponse(
                call: Call<CommunityArticleListResponse>,
                response: Response<CommunityArticleListResponse>
            ) {
                if (response.code() == 200) {
                    Log.d(TAG, "성공")
                    val checkResponse = response.body()?.articles?.content
                    getData = response.body()!!
                    Log.d(TAG, "$checkResponse")

                    val list = createDummyData(0, 10)
                    ThreadUtil.startUIThread(1000) {
                        adapter.reload(list)
                        hideProgress()
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




    private fun loadMore() {
        Log.d("CommunityTab2Fragment", "loadMore()")
        if (isLastPage) { // 마지막 페이지라면, 로딩을 멈춥니다.
            return
        }

        showProgress()

        // 페이지 번호를 1 증가시킵니다.
        communityArticlePage++

        // Retrofit을 사용하여 새로운 데이터를 받아옵니다.
        var retrofit = RetrofitClient.getClient()!!


        var communityArticleListService = retrofit.create(CommunityArticleListService::class.java)
        var communityArticleCategory = "자유"
        var communityArticleSize = 10

        communityArticleListService.requestCommunityArticleList(
            communityArticleCategory,
            communityArticlePage,
            communityArticleSize
        ).enqueue(object :
            Callback<CommunityArticleListResponse> {
            override fun onResponse(
                call: Call<CommunityArticleListResponse>,
                response: Response<CommunityArticleListResponse>
            ) {
                if (response.code() == 200) {
                    Log.d("loadmore", "성공")
                    val checkResponse = response.body()?.articles?.content
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
        Log.d("CommunityTab2Fragment", "showProgress()")
        frameLayoutProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        Log.d("CommunityTab2Fragment", "hideProgress()")
        frameLayoutProgress.visibility = View.GONE
    }

    private fun createDummyData(
        offset: Int,
        limit: Int
    ): MutableList<CommunityArticleListResponse> {
        Log.d("CommunityTab2Fragment", "createDummyData()")
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

    private fun createPopularTagData(): List<String> {
        val popularTagList =
            GlobalVariables.prefs.getString("popular_tags", "")?.split(", ") ?: emptyList()
        return popularTagList
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun searchViewListner() {
        communitySearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d(
                    "CommunityTab1Fragment",
                    "onQueryTextSubmit() ${communitySearchView.query.toString()}"
                )
                if (communitySearchView.query.toString() == "") {
                    Log.d("CommunityTab1Fragment", "onQueryTextSubmit() 검색어가 비었어요")
                }

                keyword = communitySearchView.query.toString()
                overlayView.visibility = View.GONE
                popularTagSection.visibility = View.GONE
                communitySearchView.clearFocus()


//                if (isFiltered) {
//                val retrofit = RetrofitClient.getClient()!!
//                var communityArticlePage = 0
//                var communityArticleSize = 10
//                val regionFilterService = retrofit.create(CommunityService::class.java)
//                val regions = arrayListOf<String?>(null, null, null)
//
//                Log.d("CommunityTab1Fragment", "onCreateView() 넘기는 필터값 $regions")
//
//                regionFilterService.requestSearchArticle(
//                    category = "나눔",
//                    keyword = keyword,
//                    pageInput = communityArticlePage,
//                    sizeInput = communityArticleSize
//                ).enqueue(object :
//                    Callback<CommunityArticleListResponse> {
//                    override fun onResponse(
//                        call: Call<CommunityArticleListResponse>,
//                        response: Response<CommunityArticleListResponse>
//                    ) {
//                        if (response.code() == 200) {
//                            Log.d(TAG, "요청 성공")
//                            val checkResponse = response.body()?.articles?.content
//                            val checkTotal = response.body()?.articles?.total
//                            getData = response.body()!!
//                            Log.d(TAG, "$checkResponse")
//                            Log.d(TAG, "$checkTotal")
//
//                            val totalElements = getData.articles.total // 전체 데이터 수
//                            val currentPage = communityArticlePage // 현재 페이지 번호
//                            val pageSize = 10 // 페이지 당 아이템 수
//                            val isLast =
//                                (currentPage + 1) * pageSize >= totalElements // 마지막 페이지 여부를 판단합니다.
//
//                            if (isLast) { // 마지막 페이지라면, isLastPage를 true로 설정합니다.
//                                isLastPage = true
//                            }
//                            val list = createDummyData(0, 10)
//                            ThreadUtil.startUIThread(1000) {
//                                adapter.reload(list)
//                                hideProgress()
//                            }
//                        } else {
//                            Log.d(TAG, "실패1 $response")
//                        }
//                    }
//
//                    override fun onFailure(
//                        call: Call<CommunityArticleListResponse>,
//                        t: Throwable
//                    ) {
//                        Log.d("RegionFilterBottomSheet", "onFailure() 지역 필터링 게시글 요청 실패")
//                    }
//                })

//                }
//            else {
//                    Toast.makeText(requireContext(), "모든 게시글을 검색합니다", Toast.LENGTH_SHORT).show()
////                retrofit 객체 만들기
//                    var retrofit = RetrofitClient.getClient()!!
//
//
//                    var communityArticleListService =
//                        retrofit.create(CommunityArticleListService::class.java)
//                    var communityArticleCategory = "나눔"
//                    var communityArticlePage = 0
//                    var communityArticleSize = 10
//
//                    communityArticleListService.requestCommunityArticleList(
//                        communityArticleCategory,
//                        communityArticlePage,
//                        communityArticleSize
//                    ).enqueue(object :
//                        Callback<CommunityArticleListResponse> {
//                        override fun onResponse(
//                            call: Call<CommunityArticleListResponse>,
//                            response: Response<CommunityArticleListResponse>
//                        ) {
//                            if (response.code() == 200) {
//                                Log.d(TAG, "성공")
//                                val checkResponse = response.body()?.articles?.content
//                                getData = response.body()!!
//                                Log.d(TAG, "$checkResponse")
//
//
//                                val totalElements = getData.articles.total // 전체 데이터 수
//                                val currentPage = communityArticlePage // 현재 페이지 번호
//                                val pageSize = 10 // 페이지 당 아이템 수
//                                val isLast =
//                                    (currentPage + 1) * pageSize >= totalElements // 마지막 페이지 여부를 판단합니다.
//
//                                if (isLast) { // 마지막 페이지라면, isLastPage를 true로 설정합니다.
//                                    isLastPage = true
//                                }
//                                val list = createDummyData(0, 10)
//                                ThreadUtil.startUIThread(1000) {
//                                    adapter.reload(list)
//                                    hideProgress()
//                                }
//                            } else {
//                                Log.d(TAG, "실패1 $response")
//                            }
//                        }
//
//                        override fun onFailure(
//                            call: Call<CommunityArticleListResponse>,
//                            t: Throwable
//                        ) {
//                            Log.d(TAG, "실패2")
//                        }
//
//                    })
//                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                filterList(nextText)
                return true
            }
        })

        communitySearchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // input 창을 눌렀을 때의 동작 처리
                overlayView.visibility = View.VISIBLE
                popularTagSection.visibility = View.VISIBLE
            } else {
                // input 창에서 focus를 잃었을 때의 동작 처리
                overlayView.visibility = View.GONE
                popularTagSection.visibility = View.GONE
            }
        }

        overlayView.setOnTouchListener { _, _ ->
            overlayView.visibility = View.GONE
            popularTagSection.visibility = View.GONE
            communitySearchView.setQuery("", false)
            GlobalVariables.hideKeyboard(requireActivity())
            true
        }
    }
}