package com.chocobi.groot.view.pot

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.search.SearchDetailFragment
import com.chocobi.groot.view.search.adapter.DictRVAdapter
import com.chocobi.groot.view.search.model.PlantMetaData
import com.chocobi.groot.view.search.model.PlantSearchResponse
import com.chocobi.groot.view.search.model.SearchService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create


class PlantBottomSheet(context: Context) : BottomSheetDialogFragment() {

    private lateinit var plants: Array<PlantMetaData>
    private lateinit var rvAdapter: DictRVAdapter // rvAdapter를 클래스 멤버 변수로 이동
    private var plantName: String? = null

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var dictRecyclerView: RecyclerView
    private lateinit var searchPlantBtn: ImageButton


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.bottom_sheet_plant, container, false)

        findView(view)
        setAutocompltete()
        searchPlant()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        clickItem()
    }

    private fun findView(view: View) {
        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView)
        dictRecyclerView = view.findViewById(R.id.dictRecyclerView)
        searchPlantBtn = view.findViewById(R.id.searchPlantBtn)

        rvAdapter = DictRVAdapter(emptyArray())
        dictRecyclerView.layoutManager = GridLayoutManager(activity, 3)
        dictRecyclerView.adapter = rvAdapter
    }

    private fun setAutocompltete() {
        val plantNames =
            GlobalVariables.prefs.getString("plant_names", "")?.split(", ") ?: emptyList()
        val items = plantNames.toTypedArray() // 괄호 제거하고 쉼표로 분리

        Log.d("PlantBottomSheet","setAutocompltete() 자동완성 $plantNames")
        var adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun searchPlant() {
//        자동완성 클릭 했을 때
        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                GlobalVariables.hideKeyboard(requireActivity())
                autoCompleteTextView.clearFocus()
                plantName = parent.getItemAtPosition(position).toString()
                requestSearchPlant(plantName)
            }

//        돋보기 버튼 클릭 했을 때
        searchPlantBtn.setOnClickListener {
//            키보드 내리기
            GlobalVariables.hideKeyboard(requireActivity())
            autoCompleteTextView.clearFocus()
//            검색 api 요청
            plantName = autoCompleteTextView.text.toString()
            search(plantName)
        }
    }

    private fun requestSearchPlant(plantName: String?) {
        val retrofit = RetrofitClient.basicClient()!!
        val plantSearchService = retrofit.create(SearchService::class.java)
        plantSearchService.requestSearchPlants(
            name = plantName
        ).enqueue(object : Callback<PlantSearchResponse> {
            override fun onResponse(
                call: Call<PlantSearchResponse>,
                response: Response<PlantSearchResponse>
            ) {
                if (response.code() == 200) {
                    val searchBody = response.body()
                    Log.d("SearchFragment", "requestSearchPlant() api 성공 $searchBody")
                    if (searchBody != null) {
                        plants = searchBody.plants
                        rvAdapter.setData(plants)
                    }

                } else {
                    Log.d("SearchFragment", "requestSearchPlant() api 실패1 ")
                }
            }
            override fun onFailure(call: Call<PlantSearchResponse>, t: Throwable) {
                Log.d("SearchFragment", "requestSearchPlant() api 실패2 ")
            }

        })
    }

    private fun clickItem() {
        rvAdapter.itemClick = object : DictRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val bundle = Bundle().apply {
                    putString("plant_id", plants[position].plantId.toString())
                }
                val passBundleFragment = SearchDetailFragment().apply {
                    arguments = bundle
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fl_container, passBundleFragment)
                    .commit()
                // 키보드 닫기
                GlobalVariables.hideKeyboard(requireActivity())

                // BottomSheet 닫기
                val parentFragment = parentFragment as? BottomSheetDialogFragment
                parentFragment?.dismiss()
            }
        }
    }

    private fun search(targetText: String?) {
        if (targetText == "") {
            Toast.makeText(requireContext(), "전체 식물 데이터를 조회합니다", Toast.LENGTH_SHORT).show()
        }
        requestSearchPlant(targetText)

        // BottomSheet 닫기
        val parentFragment = parentFragment as? BottomSheetDialogFragment
        parentFragment?.dismiss()
    }
}

