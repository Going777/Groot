package com.chocobi.groot.view.pot

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.ModelDiary
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.pot.adapter.PotDiaryListRVAdapter
import com.chocobi.groot.view.pot.adapter.PotListRVAdapter
import com.chocobi.groot.view.pot.model.Date
import com.chocobi.groot.view.pot.model.DateTime
import com.chocobi.groot.view.pot.model.Diaries
import com.chocobi.groot.view.pot.model.DiaryListResponse
import com.chocobi.groot.view.pot.model.Pot
import com.chocobi.groot.view.pot.model.PotListResponse
import com.chocobi.groot.view.pot.model.PotService
import com.chocobi.groot.view.pot.model.Time
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PotDiaryFragment : Fragment() {
    private val TAG = "PotDiaryFragment"
    private lateinit var mActivity: MainActivity
    private lateinit var getData: DiaryListResponse
    private var REQUESTPAGESIZE = 10
    private var diaryListPage = 0 // 초기 페이지 번호를 0으로 설정합니다.
    private var selectedPotId = 0
    private var isLastPage = false // 마지막 페이지인지 여부를 저장하는 변수입니다.
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var potListRV: RecyclerView
    private var potRvAdapter: PotListRVAdapter? = null
    private lateinit var adapter: PotDiaryListRVAdapter
    private lateinit var frameLayoutProgress: FrameLayout
    private lateinit var firstView: ConstraintLayout
    private var potList: MutableList<Pot>? = null
    private var potPosition: Int = 0
    private val firstItem: Pot = Pot(
        0,
        0,
        "",
        "",
        "",
        0,
        DateTime(Date(0, 0, 0), Time(0, 0, 0, 0)),
        null,
        null,
        null,
        false,
        0,
        0,
        "",
        "",
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_pot_diary, container, false)
        mActivity = activity as MainActivity
        findViews(rootView)

        selectedPotId = arguments?.getInt("detailPotId") ?: 0
//        if (detailPotId is Int) {
//            selectedPotId = detailPotId!!
//        }
        Log.d(TAG, "oncreateview$selectedPotId")
        setListeners()
        initList()
//        reload()
        showProgress()

//        상단 화분 목록
        getPotList(mActivity)



        return rootView
    }


    private fun findViews(view: View) {
        firstView = view.findViewById(R.id.firstView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerView)
        frameLayoutProgress = view.findViewById(R.id.frameLayoutProgress)
        potListRV = view.findViewById(R.id.potListRecyclerView)
    }

    private fun setListeners() {
        swipeRefreshLayout.setOnRefreshListener {
            reload()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initList() {
        adapter = PotDiaryListRVAdapter(requireContext(), mActivity)
        adapter.delegate = object : PotDiaryListRVAdapter.RecyclerViewAdapterDelegate {
            override fun onLoadMore() {
                loadMore()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.setItemClickListener(object : PotDiaryListRVAdapter.ItemClickListener {
            override fun onSpinnerBtnClick(view: View, position: Int) {
                Log.d(TAG, "spinner clicked ")
                reload()
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun reload() {
        requestDiaryList("reload")
    }

    private fun loadMore() {
        if (isLastPage) { // 마지막 페이지라면, 로딩을 멈춥니다.
            return
        }
        showProgress()
        requestDiaryList("loadMore")
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
    ): MutableList<DiaryListResponse> {
        val list: MutableList<DiaryListResponse> = mutableListOf()

        // API response를 이용하여 데이터 생성
        val contents = getData.diary.content
        for (i in offset until (offset + limit)) {
            if (i >= contents.size) {
                break
            }
            val diaryItem = contents[i]
            // 이미지가 있을 때는 visibility를 visible로 설정하고, 없을 때는 gone으로 설정합니다.
            val visibility = if (diaryItem.imgPath.isNullOrEmpty()) View.GONE else View.VISIBLE

            val diaryListResponse = DiaryListResponse(
                diary = Diaries(
                    content = listOf(
                        ModelDiary(
                            id = diaryItem.id,
                            potId = diaryItem.potId,
                            potName = diaryItem.potName,
                            userPK = diaryItem.userPK,
                            nickName = diaryItem.nickName,
                            createTime = diaryItem.createTime,
                            updateTime = diaryItem.updateTime,
                            imgPath = diaryItem.imgPath,
                            content = diaryItem.content,
                            water = diaryItem.water,
                            nutrients = diaryItem.nutrients,
                            pruning = diaryItem.pruning,
                            bug = diaryItem.bug,
                            sun = diaryItem.sun,
                            isPotLast = diaryItem.isPotLast,
                            isUserLast = diaryItem.isUserLast
                        )
                    ),
                    total = getData.diary.total,
                    pageable = getData.diary.pageable
                ),
                result = getData.result,
                msg = getData.msg
            )
            list.add(diaryListResponse)
        }
        return list
    }

    fun getPotList(mActivity: MainActivity) {
        var retrofit = RetrofitClient.getClient()!!
        var potService = retrofit.create(PotService::class.java)
        potService.getPotList().enqueue(object :
            Callback<PotListResponse> {
            override fun onResponse(
                call: Call<PotListResponse>,
                response: Response<PotListResponse>
            ) {
                val body = response.body()
                if (body != null && response.code() == 200) {
                    Log.d(TAG, "$body")
                    Log.d(TAG, "body: $body")
                    Log.d(TAG, "body: ${body.pots.size}")
                    potList = body.pots.toMutableList()
                    potList!!.add(0, firstItem)

//                    if (potPosition >= 0) {
//                        selectedPotId = potList!![potPosition].potId
//                    } else {
//                        selectedPotId = 0
//                    }

                    if (selectedPotId > 0) {
                        Log.d("PotListRVAdapter 1111", "체크")
                        for ((i, pot) in potList!!.withIndex()) {
                            if (pot.potId == selectedPotId) {
                                Log.d("PotListRVAdapter 1111", "$i")
                                potPosition = i
                            }
                        }
                    }



                    setRecyclerView(potList!!, mActivity)

                    //        다이어리 리스트 불러오기
                    requestDiaryList("load")


                } else {
                    Log.d(TAG, "실패1")
                }
            }

            override fun onFailure(call: Call<PotListResponse>, t: Throwable) {
                Log.d(TAG, "실패2")
            }
        })
    }

    fun setRecyclerView(potList: List<Pot>, mActivity: MainActivity) {
        Log.d("PotListRVAdapter", "$potPosition")
        potRvAdapter = PotListRVAdapter(potList, potPosition)
        potListRV.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        potListRV.adapter = potRvAdapter
        potRvAdapter!!.itemClick = object : PotListRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                clickDiaryPot(potList[position].potId)
            }
        }
    }

    private fun clickDiaryPot(potId: Int) {
        showProgress()
        selectedPotId = potId
        adapter.setIsZero(potId == 0)
//            화분 다이어리 조회
        requestDiaryList("load")
    }

    private fun requestDiaryList(usage: String) {
        if (usage == "loadMore") {
            diaryListPage++
        } else {
            diaryListPage = 0
        }

        var retrofit = RetrofitClient.getClient()!!
        var potService = retrofit.create(PotService::class.java)
        Log.d(TAG, "selectedPotId: $selectedPotId")

        potService.requestPotDiary(selectedPotId, diaryListPage, REQUESTPAGESIZE)
            .enqueue(object : Callback<DiaryListResponse> {
                override fun onResponse(
                    call: Call<DiaryListResponse>,
                    response: Response<DiaryListResponse>
                ) {
                    if (response.code() == 200) {
//                        adapter.setIsZero(selectedPotId == 0)
                        Log.d(TAG, "성공")
                        getData = response.body()!!
                        val list = createDummyData(0, REQUESTPAGESIZE)
                        if (usage != "reload") {
                            val totalElements = getData.diary.total // 전체 데이터 수
                            if (totalElements == 0) {
                                showFirstView()
                            } else {
                                hideFirstView()
                            }
                            val currentPage = diaryListPage // 현재 페이지 번호
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
                        if (diaryListPage == 0) {
                            showFirstView()
                        }
                        Log.d(TAG, "실패1")
                    }
                }

                override fun onFailure(call: Call<DiaryListResponse>, t: Throwable) {
                    Log.d(TAG, "실패2")
                }
            })
    }

    private fun showFirstView() {
        firstView.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun hideFirstView() {
        firstView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }
}