package com.chocobi.groot.view.community

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.community.adapter.PopularTagAdapter
import com.chocobi.groot.view.community.adapter.RecyclerViewAdapter
import com.chocobi.groot.view.community.model.Articles
import com.chocobi.groot.view.community.model.CommunityArticleListResponse
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

    private lateinit var mActivity: MainActivity
    private val CATEGORY = "나눔"
    private val REQUESTPAGESIZE = 10
    private val LIMITREGIONCNT = 3
    private var communityArticlePage = 0 // 초기 페이지 번호를 0으로 설정합니다.
    private var isLastPage = false // 마지막 페이지인지 여부를 저장하는 변수입니다.
    private var isChecked = false

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

    private var isFiltered = false // 필터가 걸려있는 상황인지 체크
    private var keyword: String = ""
    private var regionFilterList: ArrayList<String>? = null
    private var regionFullFilterList: ArrayList<String>? = null
    private lateinit var chipRegionGroup: ChipGroup
    private lateinit var checkBox: CheckBox

    private lateinit var noArticleSection: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_community_tab1, container, false)
        mActivity = activity as MainActivity

        /** 지역 필터 버튼 눌렀을 때 처리 **/
        val regionFilterBtn = view.findViewById<MaterialButton>(R.id.regionFilterBtn)
        regionFilterBtn.setOnClickListener {
            val keyword = getLatestKeyword() // 최신의 keyword 값을 가져옴
            GlobalVariables.prefs.setString("share_keyword", keyword) // 전역변수로 저장
            val regionFilterBottomSheet =
                RegionFilterBottomSheet(requireContext(), LIMITREGIONCNT)
            regionFilterBottomSheet.show(
                mActivity.supportFragmentManager,
                regionFilterBottomSheet.tag
            )
        }

        keyword = GlobalVariables.prefs.getString("share_keyword", "")
        GlobalVariables.prefs.setString("share_keyword", "")

//        뷰 초기화
        findViews(view)
//        태그 데이터 세팅
        createPopularTagData()
//        지역 필터 데이터 받아오기
        getFilterData()
//        서치뷰 제어
        searchViewListner()
        setListeners()
        initList()
//        reload()
        showProgress()
//        checkbox제어
        controlCheckBox()

        requestSearchArticle("load")
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        clickItem()
    }

    private fun controlCheckBox() {
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            this.isChecked = isChecked
            requestSearchArticle("load")
        }
    }

    //        필터 데이터 받기
    private fun getFilterData() {
        regionFilterList = arguments?.getStringArrayList("region_list")
        regionFullFilterList = arguments?.getStringArrayList("region_full_list")
//        필터 모드인지 아닌지 구분
        isFiltered = !regionFilterList.isNullOrEmpty()
        if (isFiltered) {
            for (idx in 0..regionFilterList!!.count() - 1) {
                chipRegionGroup.addView(
                    Chip(
                        requireContext(),
                        null,
                        R.style.REGION_CHIP_ICON
                    ).apply {
                        text = regionFilterList!![idx]
                        isCloseIconVisible = false
//                        isCloseIconVisible = true
//                        setOnCloseIconClickListener {
//                            chipRegionGroup.removeView(this) // X버튼 누르면 chip 없어지게 하기
//                            regionFullFilterList!![idx] = ""
//                            view?.post { // 다음 UI 갱신 주기에 실행되도록 예약
//                                requestSearchAricle("load")
//                            }
//                        }
                    })
            }
        }
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
        chipRegionGroup = view.findViewById(R.id.chipRegionGroup)
        noArticleSection = view.findViewById(R.id.noArticleSection)

        checkBox = view.findViewById(R.id.checkBox)
    }

    private fun setListeners() {
        swipeRefreshLayout.setOnRefreshListener {
            reload()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initList() {
        adapter = RecyclerViewAdapter(mActivity)
        adapter.delegate = object : RecyclerViewAdapter.RecyclerViewAdapterDelegate {
            override fun onLoadMore() {
                loadMore()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun reload() {
        requestSearchArticle("reload")
    }


    private fun loadMore() {
        if (isLastPage) { // 마지막 페이지라면, 로딩을 멈춥니다.
            return
        }
        showProgress()
        requestSearchArticle("loadMore")
    }

    private fun showProgress() {
        frameLayoutProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        frameLayoutProgress.visibility = View.GONE
    }

    private fun requestSearchArticle(usage: String) {
        Log.d("CommunityTab1Fragment", "requestSearchAricle() 게시글을 받아옵니다")
        if (usage == "loadMore") {
            communityArticlePage++
        } else if (usage == "load") {
            isLastPage = false
            communityArticlePage = 0
        } else {
            communityArticlePage = 0
        }
        val retrofit = RetrofitClient.getClient()!!
        val regionFilterService = retrofit.create(CommunityService::class.java)
        val regions = arrayListOf("", null, null)
        Log.d(
            "CommunityTab1Fragment",
            "requestSearchAricle() 넘기게 될 지역 데이터 //$regionFullFilterList//"
        )
        if (regionFullFilterList != null) {
            for (idx in 0..regionFullFilterList!!.count() - 1) {
                regions[idx] = regionFullFilterList!![idx]
            }
        }
        Log.d("CommunityTab1Fragment", "requestSearchAricle() 보내는 ㄷ이터 지역1 ///${regions[0]}///")
        Log.d("CommunityTab1Fragment", "requestSearchAricle() 보내는 ㄷ이터 지역2 ///${regions[1]}///")
        Log.d("CommunityTab1Fragment", "requestSearchAricle() 보내는 ㄷ이터 지역3 ///${regions[2]}///")
        Log.d("CommunityTab1Fragment", "requestSearchAricle() 보내는 ㄷ이터 나눔 여부 ///${isChecked}///")

        regionFilterService.requestSearchArticle(
            category = CATEGORY,
            region1 = regions[0],
            region2 = regions[1],
            region3 = regions[2],
            keyword = keyword,
            shareStatus = isChecked,
            pageInput = communityArticlePage,
            sizeInput = REQUESTPAGESIZE
        ).enqueue(object :
            Callback<CommunityArticleListResponse> {
            override fun onResponse(
                call: Call<CommunityArticleListResponse>,
                response: Response<CommunityArticleListResponse>
            ) {
                if (response.code() == 200) {
                    getData = response.body()!!
                    if (getData.articles.total != 0) {
                        noArticleSection.visibility = View.GONE
                    } else {
                        noArticleSection.visibility = View.VISIBLE
                    }
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
                        ThreadUtil.startUIThread(100) {
                            adapter.loadMore(list)
                            hideProgress()
                        }
                    } else {
                        ThreadUtil.startUIThread(100) {
                            adapter.reload(list)
                            hideProgress()
                        }

                    }
                } else {
                    Log.d(TAG, "실패1 $response")
                }
            }

            override fun onFailure(call: Call<CommunityArticleListResponse>, t: Throwable) {
                Log.d("RegionFilterBottomSheet", "onFailure() 지역 필터링 게시글 요청 실패")
            }
        })
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

    private fun createPopularTagData(): List<String> {
        val popularTagList =
            GlobalVariables.prefs.getString("popular_tags_share", "")?.split(", ") ?: emptyList()
        return popularTagList
    }

    private fun clickItem() {
        popularTagAdapter.itemClick = object : PopularTagAdapter.ItemClick {
            override fun onClick(view: View, position: Int, item: String) {
                communitySearchView.setQuery(item, true)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun searchViewListner() {
        Log.d("CommunityTab1Fragment", "searchViewListner()")
        if (keyword != null) {
            communitySearchView.setQuery(keyword, false)
        }

        communitySearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d(
                    "CommunityTab1Fragment",
                    "onQueryTextSubmit() ${communitySearchView.query.toString()}"
                )
                isFiltered = true
                if (communitySearchView.query.toString() == "") {
                    Log.d("CommunityTab1Fragment", "onQueryTextSubmit() 검색어가 비었어요")
                }

                keyword = communitySearchView.query.toString()
                overlayView.visibility = View.GONE
                popularTagSection.visibility = View.GONE
                communitySearchView.clearFocus()

                requestSearchArticle("load")
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

//        검색창 제외 구간 클릭했을 때
        overlayView.setOnTouchListener { _, _ ->
            overlayView.visibility = View.GONE
            popularTagSection.visibility = View.GONE
            communitySearchView.setQuery("", false)
            communitySearchView.clearFocus()
            GlobalVariables.hideKeyboard(requireActivity())
            true
        }
    }

    private fun getLatestKeyword(): String {
        return keyword
    }
}