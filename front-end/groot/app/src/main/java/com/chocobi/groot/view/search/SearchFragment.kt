package com.chocobi.groot.view.search

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.PERMISSION_CAMERA
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.pot.PlantBottomSheet
import com.chocobi.groot.view.search.adapter.DictRVAdapter
import com.chocobi.groot.view.search.model.PlantMetaData
import com.chocobi.groot.view.search.model.PlantSearchResponse
import com.chocobi.groot.view.search.model.SearchService
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchFragment : Fragment() {
    private var plantName: String? = null
    private var plants: Array<PlantMetaData>? = null
    private lateinit var rvAdapter: DictRVAdapter // rvAdapter를 클래스 멤버 변수로 이동

    private lateinit var firstView: ConstraintLayout
    private lateinit var blankView: ConstraintLayout
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var difficultyChipGroup: ChipGroup
    private lateinit var luxChipGroup: ChipGroup
    private lateinit var growthChipGroup: ChipGroup
    private lateinit var rv: RecyclerView
    private lateinit var recmmView: MaterialCardView
    private lateinit var recmRv: RecyclerView
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

    private fun isAllBlank() =
        (plantName == null || plantName == "") && difficulty1 == null && difficulty2 == null && difficulty3 == null
                && lux1 == null && lux2 == null && lux3 == null &&
                growth1 == null && growth2 == null && growth3 == null && growth4 == null && growth5 == null && growth6 == null


    private fun setupRecyclerView() {
        // RecyclerView 설정
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 맨 처음 시작했을 때 추천 받아오기 뜨기
//        requestRecommendations()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_search, container, false)

//        Fragment 이동 조작
        val mActivity = activity as MainActivity

        findView(rootView)
//        추천 식물 받아오기
        requestRecommendations()
        filterChipGroup()


//        Camera 버튼 클릭
        val cameraBtn = rootView.findViewById<ImageButton>(R.id.cameraBtn)
        cameraBtn.setOnClickListener {
            mActivity.setCameraStatus("searchPlant")
            mActivity.requirePermissions(
                arrayOf(android.Manifest.permission.CAMERA),
                PERMISSION_CAMERA
            )
        }

        recyclerViewSetting()

//        if(plants == null) {
//            firstView.visibility = View.VISIBLE
//        } else {
//            firstView.visibility = View.GONE
//        }

        // 자동완성으로 보여줄 내용들
        val plantNames =
            GlobalVariables.prefs.getString("plant_names", "")?.split(", ") ?: emptyList()
        val items = plantNames.toTypedArray() // 괄호 제거하고 쉼표로 분리

        autoCompleteTextView =
            rootView.findViewById(R.id.autoCompleteTextView)
        var adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
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
                search(plantName)
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
            search(plantName)
        }

        addPot(mActivity)

        return rootView
    }

    private fun findView(view: View) {
        firstView = view.findViewById(R.id.firstView)
        blankView = view.findViewById(R.id.blankView)
        difficultyChipGroup = view.findViewById(R.id.difficultyChipGroup)
        luxChipGroup = view.findViewById(R.id.luxChipGroup)
        growthChipGroup = view.findViewById(R.id.growthChipGroup)
        rv = view.findViewById(R.id.dictRecyclerView)
        recmmView = view.findViewById(R.id.recmmView)
        recmRv = view.findViewById(R.id.recmRecyclerView)

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
    }

    private fun recyclerViewSetting() {
        rvAdapter = DictRVAdapter(emptyArray())
        rv.layoutManager = GridLayoutManager(activity, 3)
        rv.adapter = rvAdapter
        recmRv.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recmRv.adapter = rvAdapter

        rvAdapter.itemClick = object : DictRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val bundle = Bundle().apply {
                    putString("plant_id", plants!![position].plantId.toString())
                }

                val passBundleBFragment = SearchDetailFragment().apply {
                    arguments = bundle
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fl_container, passBundleBFragment)
                    .commit()
            }
        }
    }

    private fun filterChipGroup() {
        difficultyEasy.setOnCheckedChangeListener { buttonView, isChecked ->
            difficulty1 = if (isChecked) buttonView.text.toString() else null
            if (isAllBlank()) {
                requestRecommendations()
            } else {
                requestSearchPlant()
            }
        }
        difficultyMedium.setOnCheckedChangeListener { buttonView, isChecked ->
            difficulty2 = if (isChecked) buttonView.text.toString() else null
            if (isAllBlank()) {
                requestRecommendations()
            } else {
                requestSearchPlant()
            }
        }
        difficultyHard.setOnCheckedChangeListener { buttonView, isChecked ->
            difficulty3 = if (isChecked) buttonView.text.toString() else null
            if (isAllBlank()) {
                requestRecommendations()
            } else {
                requestSearchPlant()
            }
        }
        luxLow.setOnCheckedChangeListener { buttonView, isChecked ->
            lux1 = if (isChecked) buttonView.text.toString() else null
            if (isAllBlank()) {
                requestRecommendations()
            } else {
                requestSearchPlant()
            }
        }
        luxMedium.setOnCheckedChangeListener { buttonView, isChecked ->
            lux2 = if (isChecked) buttonView.text.toString() else null
            if (isAllBlank()) {
                requestRecommendations()
            } else {
                requestSearchPlant()
            }
        }
        luxHigh.setOnCheckedChangeListener { buttonView, isChecked ->
            lux3 = if (isChecked) buttonView.text.toString() else null
            if (isAllBlank()) {
                requestRecommendations()
            } else {
                requestSearchPlant()
            }
        }
        growthStraight.setOnCheckedChangeListener { buttonView, isChecked ->
            growth1 = if (isChecked) buttonView.text.toString() else null
            if (isAllBlank()) {
                requestRecommendations()
            } else {
                requestSearchPlant()
            }
        }
        growthTree.setOnCheckedChangeListener { buttonView, isChecked ->
            growth2 = if (isChecked) buttonView.text.toString() else null
            if (isAllBlank()) {
                requestRecommendations()
            } else {
                requestSearchPlant()
            }
        }
        growthVine.setOnCheckedChangeListener { buttonView, isChecked ->
            growth3 = if (isChecked) buttonView.text.toString() else null
            if (isAllBlank()) {
                requestRecommendations()
            } else {
                requestSearchPlant()
            }
        }
        growthFleshy.setOnCheckedChangeListener { buttonView, isChecked ->
            growth4 = if (isChecked) buttonView.text.toString() else null
            if (isAllBlank()) {
                requestRecommendations()
            } else {
                requestSearchPlant()
            }
        }
        growthCrawl.setOnCheckedChangeListener { buttonView, isChecked ->
            growth5 = if (isChecked) buttonView.text.toString() else null
            if (isAllBlank()) {
                requestRecommendations()
            } else {
                requestSearchPlant()
            }
        }
        growthGrass.setOnCheckedChangeListener { buttonView, isChecked ->
            growth6 = if (isChecked) buttonView.text.toString() else null
            if (isAllBlank()) {
                requestRecommendations()
            } else {
                requestSearchPlant()
            }
        }
    }

    private fun requestSearchPlant() {
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
                            plants = searchBody.plants
                            Log.d("SearchFragment", "onResponse() 요청된 것 보기 $plants")
                            recmmView.visibility = View.GONE
                            firstView.visibility = View.GONE
                            blankView.visibility = View.GONE
                            rv.visibility = View.VISIBLE
                            rvAdapter.setData(plants!!)
                        }
                    } else {
                        Log.d("SearchFragment", "onResponse() 아무것도 값이 없어요")
                        rv.visibility = View.GONE
                        recmmView.visibility = View.GONE
                        firstView.visibility = View.GONE
                        blankView.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<PlantSearchResponse>, t: Throwable) {
                    Log.d("SearchFragment", "requestSearchPlant() api 실패2 ")
                }
            })
    }

    private fun search(targetText: String?) {
        if (isAllBlank() || targetText == "") {
            requestRecommendations()
        } else {
            requestSearchPlant()
        }
    }

    private fun requestRecommendations() {
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
                            plants = searchBody.plants
                            if (plants == null) {
                                rv.visibility = View.GONE
                                recmmView.visibility = View.GONE
                                firstView.visibility = View.VISIBLE
                                blankView.visibility = View.GONE
                            } else {
                                rv.visibility = View.GONE
                                recmmView.visibility = View.VISIBLE
                                firstView.visibility = View.GONE
                                blankView.visibility = View.GONE
                                rvAdapter.setData(plants!!)
                            }
                        }
                    } else
                        Log.d("SearchFragment", "requestRecommendations() api 실패1")
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