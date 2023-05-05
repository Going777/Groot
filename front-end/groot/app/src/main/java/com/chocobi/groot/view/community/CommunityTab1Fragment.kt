package com.chocobi.groot.view.community

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.community.adapter.RecyclerViewAdapter
import com.chocobi.groot.view.community.model.Articles
import com.chocobi.groot.view.community.model.CommunityArticleListResponse
import com.chocobi.groot.view.user.ProfileBottomSheet
import com.google.android.material.button.MaterialButton
import com.chocobi.groot.view.community.model.ArticleContent
import com.chocobi.groot.view.community.model.CommunityService
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityTab1Fragment : Fragment() {

    private val TAG = "CommunityTab1Fragment"

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var frameLayoutProgress: FrameLayout
    private lateinit var getData: CommunityArticleListResponse

    private var isFiltered = false // 필터가 걸려있는 상황인지 체크
    private var regionFilterList: ArrayList<String>? = null
    private var regionFullFilterList: ArrayList<String>? = null
    private lateinit var chipRegionGroup: ChipGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_community_tab1, container, false)
        val mActivity = activity as MainActivity

/** 지역 필터 버튼 눌렀을 때 처리 **/
        val regionFilterBtn = view.findViewById<MaterialButton>(R.id.regionFilterBtn)
        val regionFilterBottomSheet = RegionFilterBottomSheet(requireContext())
        regionFilterBtn.setOnClickListener {
            regionFilterBottomSheet.show(
                mActivity.supportFragmentManager,
                regionFilterBottomSheet.tag
            )
        }

//        뷰 초기화
        findViews(view)
//        지역 필터 데이터 받아오기
        getFilterData()
        setListeners()
        initList()
//        reload()
        showProgress()

        if (isFiltered) {
            val retrofit = RetrofitClient.getClient()!!
            var communityArticlePage = 0
            var communityArticleSize = 10
            val regionFilterService = retrofit.create(CommunityService::class.java)
            val regions = arrayListOf<String?>(null, null, null)
            for (idx in 0..regionFullFilterList!!.count() - 1) {
                regions[idx] = regionFullFilterList!![idx]
            }
            Log.d("CommunityTab1Fragment", "onCreateView() 넘기는 필터값 $regions")

            regionFilterService.requestRegionFilter(
                region1 = regions[0],
                region2 = regions[1],
                region3 = regions[2],
                pageInput = communityArticlePage,
                sizeInput = communityArticleSize
            ).enqueue(object :
                Callback<CommunityArticleListResponse> {
                override fun onResponse(
                    call: Call<CommunityArticleListResponse>,
                    response: Response<CommunityArticleListResponse>
                ) {
                    if (response.code() == 200) {
                        Log.d(TAG, "성공")
                        val checkResponse = response.body()?.articles?.content
                        val checkTotal = response.body()?.articles?.total
                        getData = response.body()!!
                        Log.d(TAG, "$checkResponse")
                        Log.d(TAG, "$checkTotal")

                        val totalElements = getData.articles.total // 전체 데이터 수
                        val currentPage = communityArticlePage // 현재 페이지 번호
                        val pageSize = 10 // 페이지 당 아이템 수
                        val isLast =
                            (currentPage + 1) * pageSize >= totalElements // 마지막 페이지 여부를 판단합니다.

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
                    Log.d("RegionFilterBottomSheet", "onFailure() 지역 필터링 게시글 요청 실패")
                }
            })

        } else {
            Toast.makeText(requireContext(), "모든 게시글을 검색합니다", Toast.LENGTH_SHORT).show()
//                retrofit 객체 만들기
            var retrofit = RetrofitClient.getClient()!!


            var communityArticleListService =
                retrofit.create(CommunityArticleListService::class.java)
            var communityArticleCategory = "나눔"
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
                        val isLast =
                            (currentPage + 1) * pageSize >= totalElements // 마지막 페이지 여부를 판단합니다.

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
        }

        return view
    }

    //        필터 데이터 받기
    private fun getFilterData() {
        regionFilterList = arguments?.getStringArrayList("region_list")
        regionFullFilterList = arguments?.getStringArrayList("region_full_list")
//        필터 모드인지 아닌지 구분
        isFiltered = !regionFilterList.isNullOrEmpty()
        if (isFiltered) {
            for (item in regionFilterList!!) {
                chipRegionGroup.addView(Chip(requireContext(), null, R.style.REGION_CHIP_ICON).apply {
                    text = item
                    isCloseIconVisible = false
                })
            }
            Log.d("CommunityTab1Fragment", "onViewCreated()/필터 값: $regionFullFilterList")
        }
    }

    private fun findViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerView)
        frameLayoutProgress = view.findViewById(R.id.frameLayoutProgress)
        chipRegionGroup = view.findViewById(R.id.chipRegionGroup)
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
        if (isFiltered) {
            val retrofit = RetrofitClient.getClient()!!
            var communityArticlePage = 0
            var communityArticleSize = 10
            val regionFilterService = retrofit.create(CommunityService::class.java)
            val regions = arrayListOf<String?>(null, null, null)
            for (idx in 0..regionFullFilterList!!.count() - 1) {
                regions[idx] = regionFullFilterList!![idx]
            }

            regionFilterService.requestRegionFilter(
                region1 = regions[0],
                region2 = regions[1],
                region3 = regions[2],
                pageInput = communityArticlePage,
                sizeInput = communityArticleSize
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
                    Log.d("RegionFilterBottomSheet", "onFailure() 지역 필터링 게시글 요청 실패")
                }
            })
        } else {

            var retrofit = RetrofitClient.getClient()!!
            var communityArticleListService =
                retrofit.create(CommunityArticleListService::class.java)
            var communityArticleCategory = "나눔"
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

        if (isFiltered) {
            Log.d("CommunityTab1Fragment", "loadMore() $isFiltered 필터있을 때 실행")
            // Retrofit을 사용하여 새로운 데이터를 받아옵니다.
            var retrofit = RetrofitClient.getClient()!!

            val regionFilterService = retrofit.create(CommunityService::class.java)
            val regions = arrayListOf<String?>(null, null, null)
            for (idx in 0..regionFullFilterList!!.count() - 1) {
                regions[idx] = regionFullFilterList!![idx]
            }
            var communityArticleSize = 10

            regionFilterService.requestRegionFilter(
                region1 = regions[0],
                region2 = regions[1],
                region3 = regions[2],
                pageInput = communityArticlePage,
                sizeInput = communityArticleSize
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
                        val isLast =
                            (currentPage + 1) * pageSize >= totalElements // 마지막 페이지 여부를 판단합니다.

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

            }
            )
        } else {
            Log.d("CommunityTab1Fragment", "loadMore() $isFiltered 필터없을 때 실행")

            // Retrofit을 사용하여 새로운 데이터를 받아옵니다.
            var retrofit = RetrofitClient.getClient()!!

            var communityArticleListService =
                retrofit.create(CommunityArticleListService::class.java)
            var communityArticleCategory = "나눔"
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
                        val isLast =
                            (currentPage + 1) * pageSize >= totalElements // 마지막 페이지 여부를 판단합니다.

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