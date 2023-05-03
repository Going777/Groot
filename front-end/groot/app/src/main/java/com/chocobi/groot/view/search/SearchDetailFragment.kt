package com.chocobi.groot.view.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.view.search.model.PlantDetailData
import com.chocobi.groot.view.search.model.PlantDetailResponse
import com.chocobi.groot.view.search.model.SearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var plantId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        plantId = arguments?.getString("plant_id")

        plantId?.let { getDetail(it.toInt()) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_search_detail, container, false)
        val mActivity = activity as MainActivity
        val againBtn = rootView.findViewById<Button>(R.id.againBtn)
        againBtn.setOnClickListener {
            mActivity.changeFragment("search")
        }
        // Inflate the layout for this fragment
        return rootView
    }

    private fun getDetail(plantId: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(GlobalVariables.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val plantSearchService = retrofit.create(SearchService::class.java)

        plantSearchService.getPlantDetail(plantId).enqueue(object : Callback<PlantDetailResponse> {
            override fun onResponse(
                call: Call<PlantDetailResponse>,
                response: Response<PlantDetailResponse>
            ) {
                if(response.code() == 200){
                    val res = response.body()
                    if(res != null) {
                        Log.d("로그", "$res: ");
                    }
                }
            }

            override fun onFailure(call: Call<PlantDetailResponse>, t: Throwable) {
                Log.d("로그", "정보조회 실패: $t");
            }

        })
    }

}