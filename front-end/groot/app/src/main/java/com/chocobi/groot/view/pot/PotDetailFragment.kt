package com.chocobi.groot.view.pot


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.mlkit.kotlin.ml.ArActivity
import com.chocobi.groot.view.pot.model.ComingDate
import com.chocobi.groot.view.pot.model.DateTime
import com.chocobi.groot.view.pot.model.Plant
import com.chocobi.groot.view.pot.model.Pot
import com.chocobi.groot.view.pot.model.PotResponse
import com.chocobi.groot.view.pot.model.PotService
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.utils.Color
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Suppress("DEPRECATION")
class PotDetailFragment : Fragment(), PotBottomSheetListener {


    private val TAG = "PotDetailFragment"
    private var pot: Pot? = null
    private var plant: Plant? = null

    //    private var plan: List<Plan>? = null
    private lateinit var characterSceneView: SceneView
    private lateinit var potNameText: TextView
    private lateinit var potPlantText: TextView
    private lateinit var levelText: TextView
    private lateinit var potPlantImg: ImageView
    private lateinit var levelSection: LinearLayout
    private lateinit var progressBar: ProgressBar
    private var potId: Int = 0
    private var modelNode: ModelNode? = null
    private var toArBtn: Button? = null
    private var waterComingDate: ComingDate? = null
    private var nutrientComingDate: ComingDate? = null
    private var pruningComingDate: ComingDate? = null
    private lateinit var mActivity: MainActivity


    override fun onGetDetailRequested() {
        getPotDetail(potId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreate")
        var rootView = inflater.inflate(R.layout.fragment_pot_detail, container, false)
        mActivity = activity as MainActivity
        potId = arguments?.getInt("potId") ?: 0
        getPotDetail(potId)
        potPlantImg = rootView.findViewById(R.id.potPlantImg)
        characterSceneView = rootView.findViewById(R.id.characterSceneView)
        levelSection = rootView.findViewById(R.id.levelSection)
        levelText = rootView.findViewById(R.id.levelText)
        progressBar = rootView.findViewById(R.id.progressBar)
//        characterSceneView.visibility = View.GONE

        characterSceneView.setOnTouchListener { v, event ->
            when (event.action) {
//                부모뷰 스크롤 막기
                MotionEvent.ACTION_UP -> characterSceneView.parent.requestDisallowInterceptTouchEvent(
                    false
                )

                MotionEvent.ACTION_DOWN -> characterSceneView.parent.requestDisallowInterceptTouchEvent(
                    true
                )
            }
//            본인 뷰 이벤트는 적용
            false
        }

//        ================================================================
//        ================================================================
//        뒤로 가기 버튼 처리해야 하는 곳
        val backBtn = rootView.findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
//        ================================================================
//        ================================================================

        potNameText = rootView.findViewById(R.id.potName)
        potPlantText = rootView.findViewById(R.id.potPlant)


        val settingBtn = rootView.findViewById<ImageButton>(R.id.settingBtn)
        settingBtn.setOnClickListener {
            val potBottomSheet = PotBottomSheet(requireContext(), this)
            potBottomSheet.setPotId(potId)
            potBottomSheet.setPlantId(pot?.plantId ?: 0)
            potBottomSheet.setPotName(pot?.potName.toString())
            potBottomSheet.setPotPlant(pot?.plantKrName.toString())
            potBottomSheet.show(
                mActivity.supportFragmentManager,
                potBottomSheet.tag
            )
        }
//        다이어리 버튼 클릭시
        val potPostDiaryBtn = rootView.findViewById<FloatingActionButton>(R.id.potPostDiaryBtn)

        potPostDiaryBtn.setOnClickListener {

            if (potId is Int) {
                mActivity.setPotId(potId)
            }
            mActivity.setPotName(pot?.potName.toString())
            mActivity.setPotPlant(pot?.plantKrName.toString())
            mActivity.setPotCharImg(pot?.characterPNGPath.toString())
            mActivity.changeFragment("pot_diary_create")
        }

//        만나러가기 버튼 클릭시
//        dialog 띄우기
        val safeAlertDialog = AlertDialog.Builder(requireContext())

        safeAlertDialog.setMessage("AR 모드를 사용할 때는 주변이 안전한지 먼저 확인하세요.\n어린이의 경우 보호자와 함께 사용해주세요.")
        safeAlertDialog.setPositiveButton("OK") { dialog, which ->
            val intent = Intent(context, ArActivity::class.java)
            intent.putExtra("GLBfile", pot?.characterGLBPath.toString())
            intent.putExtra("level", pot?.level.toString())
            intent.putExtra("potName", pot?.potName.toString())
            intent.putExtra("potPlant", pot?.plantKrName.toString())
            startActivity(intent)
        }

        toArBtn = rootView.findViewById(R.id.toArBtn)

        toArBtn?.setOnClickListener {
            safeAlertDialog.create().show()
        }

        val toDiaryBtn = rootView.findViewById<Button>(R.id.toDiaryBtn)
        toDiaryBtn.setOnClickListener {
            mActivity.changeFragment("pot_diary")
        }


        // Inflate the layout for this fragment
        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        탭 조작

        var tabBtn1 = view.findViewById<Chip>(R.id.tabBtn1)
        var tabBtn2 = view.findViewById<Chip>(R.id.tabBtn2)
        var tabBtn3 = view.findViewById<Chip>(R.id.tabBtn3)
        var tabBtn4 = view.findViewById<Chip>(R.id.tabBtn4)
        var tabBtn5 = view.findViewById<Chip>(R.id.tabBtn5)
        tabBtn1.setOnClickListener {
            val bundle = Bundle().apply {
                putString("waterCycle", plant?.waterCycle)
                putInt("minHumidity", plant?.minHumidity ?: 0)
                putInt("maxHumidity", plant?.maxHumidity ?: 0)
                if (pot?.waterDate != null) {
                    putString("lastDate", changeDateFormat(pot?.waterDate!!))
                }
                if (waterComingDate != null) {
                    putString("comingDate", changeDateFormat(waterComingDate!!.dateTime))
                }
            }
            val tab1 = PotDetailTab1Fragment().apply {
                arguments = bundle
            }
            childFragmentManager.beginTransaction().replace(R.id.tab_container, tab1).commit()
        }
        tabBtn2.setOnClickListener {
            val bundle = Bundle().apply {
                putString("grwType", plant?.grwType)
                if (pot?.pruningDate != null) {
                    putString("lastDate", changeDateFormat(pot?.pruningDate!!))
                }
                if (pruningComingDate != null) {
                    putString("comingDate", changeDateFormat(pruningComingDate!!.dateTime))
                }
            }
            val tab2 = PotDetailTab2Fragment().apply {
                arguments = bundle
            }
            childFragmentManager.beginTransaction().replace(R.id.tab_container, tab2).commit()
        }
        tabBtn3.setOnClickListener {
            val bundle = Bundle().apply {
                putString("insectInfo", plant?.insectInfo)
            }
            val tab3 = PotDetailTab3Fragment().apply {
                arguments = bundle
            }
            childFragmentManager.beginTransaction().replace(R.id.tab_container, tab3).commit()
        }
        tabBtn4.setOnClickListener {
            val bundle = Bundle().apply {
                putString("place", plant?.place)
                putString("mgmtTip", plant?.mgmtTip)
                putInt("minGrwTemp", plant?.minGrwTemp ?: 0)
                putInt("maxGrwTemp", plant?.maxGrwTemp ?: 0)
            }
            val tab4 = PotDetailTab4Fragment().apply {
                arguments = bundle
            }
            childFragmentManager.beginTransaction().replace(R.id.tab_container, tab4).commit()
        }
        tabBtn5.setOnClickListener {
            val bundle = Bundle().apply {
                if (pot?.nutrientsDate != null) {
                    putString("lastDate", changeDateFormat(pot?.nutrientsDate!!))
                }
                if (nutrientComingDate != null) {
                    putString("comingDate", changeDateFormat(nutrientComingDate!!.dateTime))
                }
            }
            val tab5 = PotDetailTab5Fragment().apply {
                arguments = bundle
            }
            childFragmentManager.beginTransaction().replace(R.id.tab_container, tab5).commit()
        }
    }

    fun getPotDetail(potId: Int) {
        var retrofit = RetrofitClient.getClient()!!
        var potService = retrofit.create(PotService::class.java)
        potService.getPotDetail(potId).enqueue(object :
            Callback<PotResponse> {
            override fun onResponse(
                call: Call<PotResponse>,
                response: Response<PotResponse>
            ) {
                val body = response.body()
                if (body != null && response.code() == 200) {
                    Log.d(TAG, "$body")
                    Log.d(TAG, "body: $body")
                    pot = body.pot
                    Log.d(TAG, "pot: $pot")
                    plant = body.plant
                    waterComingDate = body.waterDate
                    nutrientComingDate = body.nutrientsDate
                    pruningComingDate = body.pruningDate
                    Log.d(TAG, "plant: $plant")
                    setCharacterSceneView()
                    setPlantContent()


                } else {
                    Log.d(TAG, "실패1")
                }
            }

            override fun onFailure(call: Call<PotResponse>, t: Throwable) {
                Log.d(TAG, "실패2")
            }
        })
    }

    fun setCharacterSceneView() {
        if (modelNode != null) {
            characterSceneView.removeChild(modelNode!!)
        }

        characterSceneView.backgroundColor = Color(255.0f, 255.0f, 255.0f, 0.0f)

        modelNode = ModelNode().apply {
            loadModelGlbAsync(
                glbFileLocation = pot?.characterGLBPath
                    ?: "https://groot-a303-s3.s3.ap-northeast-2.amazonaws.com/assets/unicorn_2.glb",
                autoAnimate = false,
                scaleToUnits = 0.8f,
                centerOrigin = Position(x = 0f, y = 0f, z = 0f),
            )
        }
        if (modelNode != null) {
            characterSceneView.addChild(modelNode!!)
        }
    }


    fun setPlantContent() {
        potNameText.text = pot?.potName
        potPlantText.text = pot?.plantKrName!!.replace(" (", "\n(").replace(" ‘", "\n‘")
        potPlantText.setOnClickListener {
            mActivity.setPlantId(pot?.plantId!!)
//            mActivity.setPlantImgUri(plant?.img.toString())
            mActivity.changeFragment("search_detail")
        }
        val expCount = (pot?.experience?.div(pot?.level!!) ?: 0)
        progressBar.progress = (expCount * 10)
        levelText.text = pot?.level.toString()
        GlobalVariables.changeImgView(potPlantImg, pot?.imgPath.toString(), requireContext())
        if (pot?.survival == false) {
            toArBtn?.visibility = View.GONE
        }

//        레벨 설정
        val layoutParams = LinearLayout.LayoutParams(
            dpToPx(20),
            dpToPx(20)
        )

        val redCount =
            pot?.characterGLBPath!!.substringAfterLast("_").substringBefore(".glb").toInt() + 1
        var heartCnt = redCount
        repeat(redCount) {
            var heart = ImageView(context)
            heart.setImageResource(R.drawable.ic_heart)


            heart.layoutParams = layoutParams

            levelSection.addView(heart)
        }

        if (heartCnt < 3) {
            if (pot?.level!! > (redCount - 1) * 5 + 1) {
                var greyHeart = ImageView(context)
                greyHeart.setImageResource(R.drawable.ic_heart_half)
                greyHeart.layoutParams = layoutParams
                levelSection.addView(greyHeart)
                heartCnt += 1
            }

            repeat(3 - heartCnt) {
                var greyHeart = ImageView(context)
                greyHeart.setImageResource(R.drawable.ic_heart_grey)
                greyHeart.layoutParams = layoutParams
                levelSection.addView(greyHeart)
            }
        }


        val bundle = Bundle().apply {
            putString("waterCycle", plant?.waterCycle)
            putInt("minHumidity", plant?.minHumidity ?: 0)
            putInt("maxHumidity", plant?.maxHumidity ?: 0)
            if (pot?.waterDate != null) {
                putString("lastDate", changeDateFormat(pot?.waterDate!!))
            }
            if (waterComingDate != null) {
                putString("comingDate", changeDateFormat(waterComingDate!!.dateTime))
            }

        }
        val tab1 = PotDetailTab1Fragment().apply {
            arguments = bundle
        }

        childFragmentManager.beginTransaction().replace(R.id.tab_container, tab1).commit()
    }

    private fun changeDateFormat(date: DateTime): String {
        return date.date.year.toString() + "년 " + date.date.month.toString() + "월 " + date.date.day.toString() + "일"
    }

    private fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }


}

interface PotBottomSheetListener {
    fun onGetDetailRequested()
}