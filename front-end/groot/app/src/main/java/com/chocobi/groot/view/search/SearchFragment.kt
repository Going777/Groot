package com.chocobi.groot.view.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.PERMISSION_CAMERA
import com.chocobi.groot.view.search.adapter.DictRVAdapter
import com.chocobi.groot.view.search.model.PlantMetaData
import com.chocobi.groot.view.search.model.PlantSearchResponse
import com.chocobi.groot.view.search.model.SearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var plants: Array<PlantMetaData>
    private lateinit var rvAdapter: DictRVAdapter // rvAdapter를 클래스 멤버 변수로 이동


    private fun setupRecyclerView() {
        // RecyclerView 설정
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_search, container, false)

//        Fragment 이동 조작
        val mActivity = activity as MainActivity

//        Camera 버튼 클릭
        val cameraBtn = rootView.findViewById<ImageButton>(R.id.cameraBtn)
        cameraBtn.setOnClickListener {
            mActivity.setCameraStatus("searchPlant")
            mActivity.requirePermissions(
                arrayOf(android.Manifest.permission.CAMERA),
                PERMISSION_CAMERA
            )
        }

        val rv = rootView.findViewById<RecyclerView>(R.id.dictRecyclerView)

        rvAdapter = DictRVAdapter(emptyArray())
        rv.layoutManager = GridLayoutManager(activity, 3)
        rv.adapter = rvAdapter

        rvAdapter.itemClick = object : DictRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val bundle = Bundle().apply {
                    putString("plant_id", plants[position].plantId.toString())
                }

                val passBundleBFragment = SearchDetailFragment().apply {
                    arguments = bundle
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fl_container, passBundleBFragment)
                    .commit()
            }
        }

        // 자동완성으로 보여줄 내용들
        val plantNames =
            GlobalVariables.prefs.getString("plant_names", "")?.split(", ") ?: emptyList()
        val items = plantNames.toTypedArray() // 괄호 제거하고 쉼표로 분리

        var autoCompleteTextView =
            rootView.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
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

                // 클릭한 아이템에 대한 처리를 여기에 작성합니다.
                val selectedItem = parent.getItemAtPosition(position).toString()
                // 예를 들어 선택된 아이템에 대한 처리를 하거나, 선택한 항목을 다른 뷰에 보여주는 등의 작업을 할 수 있습니다.
                requestSearchPlant(selectedItem)
            }

//        엔터키 클릭 -> 검색
        autoCompleteTextView.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                // 엔터 눌렀을때 행동
                // 엔터 눌렀을 때 필터 닫기
                autoCompleteTextView.dismissDropDown()
                // 키보드 내리기
                GlobalVariables.hideKeyboard(requireActivity())
                // 검색 api 요청
                val inputText = autoCompleteTextView.text.toString()
                search(inputText)
            }
            true
        }

//        돋보기 버튼 클릭 -> 검색
        val searchPlantBtn = rootView.findViewById<ImageButton>(R.id.searchPlantBtn)
        searchPlantBtn.setOnClickListener {
//            키보드 내리기
            GlobalVariables.hideKeyboard(requireActivity())

//            검색 api 요청
            val inputText = autoCompleteTextView.text.toString()
            search(inputText)
        }

        return rootView
    }

    private fun requestSearchPlant(plantName: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(GlobalVariables.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val plantSearchService = retrofit.create(SearchService::class.java)

        plantSearchService.searchPlants(name = plantName)
            .enqueue(object : Callback<PlantSearchResponse> {
                override fun onResponse(
                    call: Call<PlantSearchResponse>,
                    response: Response<PlantSearchResponse>
                ) {
                    if (response.code() == 200) {
                        val searchBody = response.body()
                        if (searchBody != null) {
                            plants = searchBody.plants
                            rvAdapter.setData(plants)
                        }

                    }
                }
                override fun onFailure(call: Call<PlantSearchResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun search(targetText: String) {
        if (targetText == "") {
            Toast.makeText(requireContext(), "전체 식물 데이터를 조회합니다", Toast.LENGTH_SHORT).show()
        }
            requestSearchPlant(targetText)
    }
}