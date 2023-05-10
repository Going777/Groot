package com.chocobi.groot.view.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.addpot.Pot1Activity
import com.chocobi.groot.view.search.model.PlantDetailData
import com.chocobi.groot.view.search.model.PlantDetailResponse
import com.chocobi.groot.view.search.model.SearchService
import com.google.android.filament.ToneMapper.Linear
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
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
    private val TAG = "SearchDetailFragment"
    private var plantId: String? = null

    private var plant: PlantDetailData? = null

    private var levelLinearLayout: LinearLayout? = null
    private var docLinearLayout: LinearLayout? = null
    private var typeLinearLayout: LinearLayout? = null
    private var humidityLinearLayout: LinearLayout? = null
    private var tempLinearLayout: LinearLayout? = null
    private var tipLinearLayout: LinearLayout? = null
    private var placeLinearLayout: LinearLayout? = null
    private var insectLinearLayout: LinearLayout? = null

    private var plantImg: ImageView? = null
    private var plantKrName: TextView? = null
    private var plantEnName: TextView? = null
    private var mgmtLevel: Button? = null
    private var descriptionText: TextView? = null
    private var typeText: TextView? = null
    private var humidityText: TextView? = null
    private var tempText: TextView? = null
    private var tipText: TextView? = null
    private var placeText: TextView? = null
    private var insectInfoText: TextView? = null
    private lateinit var addPotBtn: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        plantId = arguments?.getString("plant_id")

        plantId?.let { getDetail(it.toInt()) }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_search_detail, container, false)
        val mActivity = activity as MainActivity

//        뒤로가기
        val backBtn = rootView.findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            mActivity.changeFragment("search")
        }

        findView(rootView)

        return rootView
    }

    private fun findView(rootView: View) {
        levelLinearLayout = rootView.findViewById(R.id.level)
        docLinearLayout = rootView.findViewById(R.id.doc)
        typeLinearLayout = rootView.findViewById(R.id.type)
        humidityLinearLayout = rootView.findViewById(R.id.humidity)
        tempLinearLayout = rootView.findViewById(R.id.temp)
        tipLinearLayout = rootView.findViewById(R.id.tip)
        placeLinearLayout = rootView.findViewById(R.id.place)
        insectLinearLayout = rootView.findViewById(R.id.insect)

        plantImg = rootView.findViewById(R.id.plantImage)
        plantKrName = rootView.findViewById(R.id.plantKrName)
        plantEnName = rootView.findViewById(R.id.plantEnName)
        mgmtLevel = rootView.findViewById(R.id.mgmtLevel)
        descriptionText = rootView.findViewById(R.id.descriptionText)
        typeText = rootView.findViewById(R.id.typeText)
        humidityText = rootView.findViewById(R.id.humidityText)
        tempText = rootView.findViewById(R.id.tempText)
        tipText = rootView.findViewById(R.id.tipText)
        placeText = rootView.findViewById(R.id.placeText)
        insectInfoText = rootView.findViewById(R.id.insectInfoText)
        addPotBtn = rootView.findViewById(R.id.addPotBtn)
    }

    private fun addPot() {
        addPotBtn.setOnClickListener {
//            var intent = Intent(requireContext(), Pot1Activity::class.java)
//            intent.putExtra("imageUri", plantImg)
//            intent.putExtra("plantName", plantKrName)
//            intent.putExtra("plantId", plantId)
//            intent.putExtra("growType", typeText)
//            intent.putExtra("mgmtLevel", mgmtLevel)
//            intent.putExtra("characterGlbPath", characterGlbPath)
//            startActivity(intent)
        }
    }

    private fun identifyPlant() {
        val retrofit = RetrofitClient.basicClient()!!
        val searchService = retrofit.create(SearchService::class.java)



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
                if (response.code() == 200) {
                    val res = response.body()
                    if (res != null) {
                        plant = res.plant
                        updateView()
                    }
                }
            }
            override fun onFailure(call: Call<PlantDetailResponse>, t: Throwable) {
                Log.d(TAG, "onFailure() 정보조회 실패")
            }
        })
    }

    private fun updateView() {
        val typeDesc = "${plant?.grwType?.replace(",",", ")!!} 형태로 자라는 식물이에요"
        val waterDesc = "${plant?.minHumidity}~${plant?.maxHumidity}% 환경에서 잘 자라요\n${plant?.waterCycle?.replace("함","해 주세요")}"!!
        val tempDesc = "${plant?.minGrwTemp}~${plant?.maxGrwTemp}°C 환경에서 잘 자라요"
        val placeDesc = plant?.place?.replace(",","\n")!!
        val insectDesc = "${plant?.insectInfo?.replace(",",", ")} 주의가 필요해요"!!

        plantKrName?.text = plant?.krName
        plantEnName?.text = plant?.sciName

        GlobalVariables.changeImgView(plantImg!!, plant!!.img, requireContext())
        isExist(plant?.mgmtLevel!!, levelLinearLayout!!, mgmtLevel!!)
        isExist(plant?.description!!, docLinearLayout!!, descriptionText!!)
        isExist(typeDesc, typeLinearLayout!!, typeText!!)
        isExist(waterDesc, humidityLinearLayout!!, humidityText!!)
        isExist(tempDesc, tempLinearLayout!!, tempText!!)
        isExist(plant?.mgmtTip!!, tipLinearLayout!!, tipText!!)
        isExist(placeDesc, placeLinearLayout!!, placeText!!)
        isExist(insectDesc, insectLinearLayout!!, insectInfoText!!)
    }

    private fun isExist(targetText: String, linearLayout: LinearLayout, textView: TextView) {
        if(targetText == "") {
            linearLayout!!.visibility = View.GONE
        }
        else {
            textView!!.text = targetText
        }
    }
}