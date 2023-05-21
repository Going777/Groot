package com.chocobi.groot.view.pot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.MainActivity
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


class PlantBottomSheet(context: Context, requestPage: String? = null, imageUri: String? = null) :
    BottomSheetDialogFragment() {

    private val REQUEST_PAGE = requestPage
    private val imageUri = imageUri
    private lateinit var plants: Array<PlantMetaData>
    private lateinit var rvAdapter: DictRVAdapter // rvAdapter를 클래스 멤버 변수로 이동
    private var plantName: String? = null

    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var dictRecyclerView: RecyclerView
    private lateinit var searchPlantBtn: ImageButton
    private lateinit var mActivity : MainActivity


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.bottom_sheet_plant, container, false)

//        mActivity = activity as MainActivity
        findView(view)
        setAutocompltete()
        searchPlant(view)

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

        rvAdapter = DictRVAdapter(ArrayList())
        dictRecyclerView.layoutManager = GridLayoutManager(activity, 3)
        dictRecyclerView.adapter = rvAdapter
    }

    private fun setAutocompltete() {
        val plantNames =
            GlobalVariables.prefs.getString("plant_names", "")?.split(", ") ?: emptyList()
        val items = plantNames.toTypedArray() // 괄호 제거하고 쉼표로 분리

        Log.d("PlantBottomSheet", "setAutocompltete() 자동완성 $plantNames")
        var adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun searchPlant(view: View) {
//        자동완성 클릭 했을 때
        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                autoCompleteTextView.clearFocus()
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(autoCompleteTextView.windowToken, 0)

                plantName = parent.getItemAtPosition(position).toString()
                requestSearchPlant(plantName)
            }

//        돋보기 버튼 클릭 했을 때
        searchPlantBtn.setOnClickListener {
//            키보드 내리기
            // bottomsheet 키보드 숨기기
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            autoCompleteTextView.clearFocus()
//            검색 api 요청
            plantName = autoCompleteTextView.text.toString()
            search(plantName)
        }

//        엔터키 눌렀을 때
        autoCompleteTextView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 검색 버튼 또는 Enter 키가 눌렸을 때의 동작을 여기에 작성합니다.

                // bottomsheet 키보드 숨기기
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                autoCompleteTextView.clearFocus()

//            검색 api 요청
                plantName = autoCompleteTextView.text.toString()
                search(plantName)
                true // true를 반환하여 텍스트를 유지합니다.
            } else {
                false
            }
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
                        rvAdapter.setData(ArrayList(plants.toList()))
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

//                카메라로 식물 식별 했지만 못찾았거나 이름으로 다시 검색하는 경우
                if (REQUEST_PAGE == "fail_serach") {
//                    mActivity.setPlantId(plants[position].plantId!!)
////            mActivity.setPlantImgUri(plant?.img.toString())
//                    mActivity.changeFragment("search_detail")

                    var intent = Intent(requireContext(), MainActivity::class.java)
                    intent.putExtra("toPage", "search_detail")
                    intent.putExtra("plant_id", plants[position].plantId.toString())
                    intent.putExtra("imageUri", imageUri)
                    startActivity(intent)


                }
//                홈화면에서 검색 진행하는 경우
                else {
                    val bundle = Bundle().apply {
                        putString("plant_id", plants[position].plantId.toString())
                    }
                    val passBundleFragment = SearchDetailFragment().apply {
                        arguments = bundle
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fl_container, passBundleFragment)
                        .commit()
                }

//                bottomsheet닫기
                dismiss()
            }
        }
    }

    private fun search(targetText: String?) {
        if (targetText == "") {
//            Toast.makeText(requireContext(), "전체 식물 데이터를 조회합니다", Toast.LENGTH_SHORT).show()
        }
        requestSearchPlant(targetText)

//        // 키보드 닫기
        GlobalVariables.hideKeyboard(requireActivity())
    }
}

