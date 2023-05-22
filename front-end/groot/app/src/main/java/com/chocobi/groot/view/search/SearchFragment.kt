package com.chocobi.groot.view.search

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.CustomAutoCompleteAdapter
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.PERMISSION_CAMERA
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.pot.PlantBottomSheet
import com.chocobi.groot.view.search.adapter.DictRVAdapter
import com.chocobi.groot.view.search.model.PlantMetaData
import com.chocobi.groot.view.search.model.PlantSearchResponse
import com.chocobi.groot.view.search.model.SearchService
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {
    private var requestPage = 0
    private var plantName: String? = null
    private var plants: ArrayList<PlantMetaData>? = null
    private lateinit var rvAdapter: DictRVAdapter // rvAdapter를 클래스 멤버 변수로 이동
    private lateinit var recmAdapter: DictRVAdapter // rvAdapter를 클래스 멤버 변수로 이동

    private lateinit var recmmText: TextView
    private lateinit var firstView: ConstraintLayout
    private lateinit var blankView: ConstraintLayout
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var difficultyChipGroup: ChipGroup
    private lateinit var luxChipGroup: ChipGroup
    private lateinit var growthChipGroup: ChipGroup
    private lateinit var rv: RecyclerView
    private lateinit var recmmView: MaterialCardView
    private lateinit var recmRv: RecyclerView
    private lateinit var contentScrollView: ScrollView
    private lateinit var difficultyEasy: Chip
    private lateinit var difficultyMedium: Chip
    private lateinit var difficultyHard: Chip
    private lateinit var luxLow: Chip
    private lateinit var luxMedium: Chip
    private lateinit var luxHigh: Chip
    private lateinit var growthStraight: Chip
    private lateinit var growthTree: Chip
    private lateinit var growthVine: Chip
    private lateinit var growthFleshy: Chip
    private lateinit var growthCrawl: Chip
    private lateinit var growthGrass: Chip
    private lateinit var youtubeViews: LinearLayout
    private lateinit var youtubePlayer1: YouTubePlayerView
    private lateinit var youtubePlayer2: YouTubePlayerView
    private lateinit var youtubePlayer3: YouTubePlayerView
    private lateinit var youtubePlayer4: YouTubePlayerView
    private lateinit var youtubePlayer5: YouTubePlayerView
    private lateinit var youtubePlayer6: YouTubePlayerView
    private lateinit var loadMoreBtn: MaterialButton

    private var difficulty1: String? = null
    private var difficulty2: String? = null
    private var difficulty3: String? = null
    private var lux1: String? = null
    private var lux2: String? = null
    private var lux3: String? = null
    private var growth1: String? = null
    private var growth2: String? = null
    private var growth3: String? = null
    private var growth4: String? = null
    private var growth5: String? = null
    private var growth6: String? = null

    private lateinit var mActivity: MainActivity

    private fun isAllBlank() =
        (plantName == null || plantName == "") && difficulty1 == null && difficulty2 == null && difficulty3 == null
                && lux1 == null && lux2 == null && lux3 == null &&
                growth1 == null && growth2 == null && growth3 == null && growth4 == null && growth5 == null && growth6 == null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_search, container, false)


//        Fragment 이동 조작
        mActivity = activity as MainActivity

        findView(rootView)
        recmmText.text = "\uD83D\uDCA1 ${UserData.getNickName()}님을 위한 AI 추천 식물"
//        추천 식물 받아오기
        if(isAllBlank()) {
            requestRecommendations()
        }
        filterChipGroup()

//        Camera 버튼 클릭
        val cameraBtn = rootView.findViewById<ImageButton>(R.id.cameraBtn)
        cameraBtn.setOnClickListener {
            var dialog = AlertDialog.Builder(requireContext())
            dialog.setMessage("카메라로 식물을 알아보시겠습니까?")
            dialog.setPositiveButton("확인") { dialog, which ->
                mActivity.setCameraStatus("searchPlant")
                mActivity.requirePermissions(
                    arrayOf(android.Manifest.permission.CAMERA),
                    PERMISSION_CAMERA
                )
            }
            dialog.setNegativeButton("취소") { dialog, which ->
                dialog.dismiss()
            }
            dialog.show()
        }

        recyclerViewSetting()

        // 자동완성으로 보여줄 내용들
        val plantNames =
            GlobalVariables.prefs.getString("plant_names", "")?.split(", ") ?: emptyList()
        val items = plantNames // 괄호 제거하고 쉼표로 분리

        autoCompleteTextView =
            rootView.findViewById(R.id.autoCompleteTextView)
        var adapter = CustomAutoCompleteAdapter(requireContext(), items)
        autoCompleteTextView.setAdapter(adapter)

//        자동완성된 필터 클릭 -> 검색
        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
//                키보드 내리기
                GlobalVariables.hideKeyboard(requireActivity())
                autoCompleteTextView.clearFocus()

                // 클릭한 아이템에 대한 처리를 여기에 작성합니다.
                plantName = parent.getItemAtPosition(position).toString()

                // 예를 들어 선택된 아이템에 대한 처리를 하거나, 선택한 항목을 다른 뷰에 보여주는 등의 작업을 할 수 있습니다.
                requestSearchPlant()
            }

//        엔터키 클릭 -> 검색
        autoCompleteTextView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 검색 버튼 또는 Enter 키가 눌렸을 때의 동작을 여기에 작성합니다.
                //            키보드 내리기
                GlobalVariables.hideKeyboard(requireActivity())
                autoCompleteTextView.clearFocus()

//            검색 api 요청
                plantName = autoCompleteTextView.text.toString()
                search()
                true // true를 반환하여 텍스트를 유지합니다.
            } else {
                false
            }
        }

//        돋보기 버튼 클릭 -> 검색
        val searchPlantBtn = rootView.findViewById<ImageButton>(R.id.searchPlantBtn)
        searchPlantBtn.setOnClickListener {
//            키보드 내리기
            GlobalVariables.hideKeyboard(requireActivity())
            autoCompleteTextView.clearFocus()

//            검색 api 요청
            plantName = autoCompleteTextView.text.toString()
            search()
        }

        addPot(mActivity)
        loadMore()

        return rootView
    }

    private fun findView(view: View) {
        recmmText = view.findViewById(R.id.recmmText)
        firstView = view.findViewById(R.id.firstView)
        blankView = view.findViewById(R.id.blankView)
        difficultyChipGroup = view.findViewById(R.id.difficultyChipGroup)
        luxChipGroup = view.findViewById(R.id.luxChipGroup)
        growthChipGroup = view.findViewById(R.id.growthChipGroup)
        rv = view.findViewById(R.id.dictRecyclerView)
        loadMoreBtn = view.findViewById(R.id.loadMoreBtn)
        recmmView = view.findViewById(R.id.recmmView)
        recmRv = view.findViewById(R.id.recmRecyclerView)
        contentScrollView = view.findViewById(R.id.contentScrollView)

        difficultyEasy = view.findViewById(R.id.difficultyEasy)
        difficultyMedium = view.findViewById(R.id.difficultyMedium)
        difficultyHard = view.findViewById(R.id.difficultyHard)
        luxLow = view.findViewById(R.id.luxLow)
        luxMedium = view.findViewById(R.id.luxMidium)
        luxHigh = view.findViewById(R.id.luxHigh)
        growthStraight = view.findViewById(R.id.growthStraight)
        growthTree = view.findViewById(R.id.growthTree)
        growthVine = view.findViewById(R.id.growthVine)
        growthFleshy = view.findViewById(R.id.growthFleshy)
        growthCrawl = view.findViewById(R.id.growthCrawl)
        growthGrass = view.findViewById(R.id.growthGrass)

        youtubeViews = view.findViewById(R.id.youtubeViews)
        youtubePlayer1 = view.findViewById(R.id.youtubePlayer1)
        youtubePlayer2 = view.findViewById(R.id.youtubePlayer2)
        youtubePlayer3 = view.findViewById(R.id.youtubePlayer3)
        youtubePlayer4 = view.findViewById(R.id.youtubePlayer4)
        youtubePlayer5 = view.findViewById(R.id.youtubePlayer5)
        youtubePlayer6 = view.findViewById(R.id.youtubePlayer6)
    }

    private fun recyclerViewSetting() {
//        검색 관련 리사이클러뷰 어댑터 설정
        rvAdapter = DictRVAdapter(ArrayList())
        rv.layoutManager = GridLayoutManager(activity, 3)
        rv.adapter = rvAdapter

        rvAdapter.itemClick = object : DictRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                mActivity.setPlantId(rvAdapter.items[position].plantId!!)
                mActivity.changeFragment("search_detail")
            }
        }

//        추천 관련 리사이클러뷰 어댑터 설정
        recmAdapter = DictRVAdapter(ArrayList())
        recmRv.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recmRv.adapter = recmAdapter

        recmAdapter.itemClick = object: DictRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                mActivity.setPlantId(recmAdapter.items[position].plantId!!)
                mActivity.changeFragment("search_detail")
            }
        }
    }

    private fun loadMore() {
        loadMoreBtn.setOnClickListener {
            requestPage ++
            ThreadUtil.startUIThread(1000) {
                requestSearchPlant("loadMore")
            }
        }
    }

    private fun filterChipGroup() {
        difficultyEasy.setOnCheckedChangeListener { buttonView, isChecked ->
            difficulty1 = if (isChecked) buttonView.text.toString() else null
            search()
        }
        difficultyMedium.setOnCheckedChangeListener { buttonView, isChecked ->
            difficulty2 = if (isChecked) buttonView.text.toString() else null
            search()
        }
        difficultyHard.setOnCheckedChangeListener { buttonView, isChecked ->
            difficulty3 = if (isChecked) buttonView.text.toString() else null
            search()
        }
        luxLow.setOnCheckedChangeListener { buttonView, isChecked ->
            lux1 = if (isChecked) buttonView.text.toString() else null
            search()
        }
        luxMedium.setOnCheckedChangeListener { buttonView, isChecked ->
            lux2 = if (isChecked) buttonView.text.toString() else null
            search()
        }
        luxHigh.setOnCheckedChangeListener { buttonView, isChecked ->
            lux3 = if (isChecked) buttonView.text.toString() else null
            search()
        }
        growthStraight.setOnCheckedChangeListener { buttonView, isChecked ->
            growth1 = if (isChecked) buttonView.text.toString() else null
            search()
        }
        growthTree.setOnCheckedChangeListener { buttonView, isChecked ->
            growth2 = if (isChecked) buttonView.text.toString() else null
            search()
        }
        growthVine.setOnCheckedChangeListener { buttonView, isChecked ->
            growth3 = if (isChecked) buttonView.text.toString() else null
            search()
        }
        growthFleshy.setOnCheckedChangeListener { buttonView, isChecked ->
            growth4 = if (isChecked) buttonView.text.toString() else null
            search()
        }
        growthCrawl.setOnCheckedChangeListener { buttonView, isChecked ->
            growth5 = if (isChecked) buttonView.text.toString() else null
            search()
        }
        growthGrass.setOnCheckedChangeListener { buttonView, isChecked ->
            growth6 = if (isChecked) buttonView.text.toString() else null
            search()
        }
    }

    private fun requestSearchPlant(option: String? = null) {
        if(option != "loadMore") {
            // 요청할 때마다 제일 최상단으로 스크롤 위치 이동
            contentScrollView.scrollTo(0, 0)
            requestPage = 0
        }
        plantName = autoCompleteTextView.text.toString()
        val retrofit = RetrofitClient.basicClient()!!
        val plantSearchService = retrofit.create(SearchService::class.java)
        plantSearchService.requestSearchPlants(
            plantName,
            difficulty1,
            difficulty2,
            difficulty3,
            lux1,
            lux2,
            lux3,
            growth5, // 다육형
            growth1,
            growth2,
            growth3,
            growth4,
            growth6,
            requestPage
        )
            .enqueue(object : Callback<PlantSearchResponse> {
                override fun onResponse(
                    call: Call<PlantSearchResponse>,
                    response: Response<PlantSearchResponse>
                ) {
                    if (response.code() == 200) {
                        val searchBody = response.body()
                        Log.d("SearchFragment", "requestSearchPlant() api 성공 $searchBody")
                        if (searchBody != null) {
                            plants = ArrayList(searchBody.plants.toList())
                            Log.d("SearchFragment", "onResponse() 요청된 것 보기 $plants")
                            recmmView.visibility = View.GONE
                            youtubeViews.visibility = View.GONE
                            firstView.visibility = View.GONE
                            blankView.visibility = View.GONE
                            rv.visibility = View.VISIBLE

                            if(option == "loadMore") {
                                ThreadUtil.startUIThread(100) {
                                    rvAdapter.loadMore(plants!!)
                                }
                            } else {
                                rvAdapter.setData(plants!!)
                            }

                            if (plants!!.size % 30 == 0) {
                                loadMoreBtn.visibility = View.VISIBLE
                            } else {
                                loadMoreBtn.visibility = View.GONE
                            }
                        }
                    } else {
                        Log.d("SearchFragment", "onResponse() 아무것도 값이 없어요")
                        rv.visibility = View.GONE
                        recmmView.visibility = View.GONE
                        youtubeViews.visibility = View.GONE
                        firstView.visibility = View.GONE
                        blankView.visibility = View.VISIBLE
                        loadMoreBtn.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<PlantSearchResponse>, t: Throwable) {
                    Log.d("SearchFragment", "requestSearchPlant() api 실패2 ")
                }
            })
    }

    private fun search() {
        if (isAllBlank()) {
            requestRecommendations()
        } else {
            requestSearchPlant()
        }
    }

    private fun requestRecommendations() {
        // 요청할 때마다 제일 최상단으로 스크롤 위치 이동
        contentScrollView.scrollTo(0, 0)
        Log.d("SearchFragment", "requestRecommendations() 추천 요청 보내기")
        val retrofit = RetrofitClient.basicClient()!!
        val searchService = retrofit.create(SearchService::class.java)
        searchService.getRR(UserData.getUserPK())
            .enqueue(object : Callback<PlantSearchResponse> {
                override fun onResponse(
                    call: Call<PlantSearchResponse>,
                    response: Response<PlantSearchResponse>
                ) {
                    if (response.code() == 200) {
                        val searchBody = response.body()
                        Log.d("SearchFragment", "requestRecommendations() api 성공 $searchBody")
                        if (searchBody != null) {
                            plants = ArrayList(searchBody.plants.toList())
                            if (plants == null) {
                                rv.visibility = View.GONE
                                recmmView.visibility = View.GONE
                                youtubeViews.visibility = View.VISIBLE
                                firstView.visibility = View.VISIBLE
                                blankView.visibility = View.GONE
                            } else {
                                rv.visibility = View.GONE
                                recmmView.visibility = View.VISIBLE
                                youtubeViews.visibility = View.VISIBLE
                                firstView.visibility = View.GONE
                                blankView.visibility = View.GONE
                                recmAdapter.setData(plants!!)
                            }
                        }
                    } else {
                        Log.d("SearchFragment", "requestRecommendations() api 실패1 $response")
                        rv.visibility = View.GONE
                        recmmView.visibility = View.GONE
                        youtubeViews.visibility = View.VISIBLE
                        firstView.visibility = View.VISIBLE
                        blankView.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<PlantSearchResponse>, t: Throwable) {
                    Log.d("SearchFragment", "requestRecommendations() api 실패2")
                }
            })
    }

    private fun addPot(activity: MainActivity) {
        firstView.setOnClickListener {
            var dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("새 화분 등록하기")
            val dialogArray = arrayOf("카메라로 등록", "식물 이름으로 등록")

            dialog.setItems(dialogArray) { _, which ->
                when (which) {
                    0 -> {
                        activity.setCameraStatus("addPot")
                        activity.requirePermissions(
                            arrayOf(android.Manifest.permission.CAMERA),
                            PERMISSION_CAMERA
                        )
                    }

                    1 -> {
                        val plantBottomSheet = PlantBottomSheet(requireContext())
                        plantBottomSheet.show(
                            activity.supportFragmentManager,
                            plantBottomSheet.tag
                        )
                    }
                }
            }
            dialog.setNegativeButton(
                "취소",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
            dialog.show()
        }
    }
}