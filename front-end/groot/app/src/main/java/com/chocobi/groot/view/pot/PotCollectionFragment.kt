package com.chocobi.groot.view.pot

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.PERMISSION_CAMERA
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.mlkit.kotlin.ml.ArActivity
import com.chocobi.groot.view.pot.adapter.PotCollectionRVAdapter
import com.chocobi.groot.view.pot.model.Pot
import com.chocobi.groot.view.pot.model.PotListResponse
import com.chocobi.groot.view.pot.model.PotService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class PotCollectionFragment : Fragment() {
    private val TAG = "PotCollectionFragment"
    private lateinit var potCollectionRv: RecyclerView
    private var potRvAdapter : PotCollectionRVAdapter? = null
    private var potList: List<Pot>? = null
    private lateinit var potFirstView: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_pot_collection, container, false)
        val mActivity = activity as MainActivity
        potCollectionRv =
            rootView.findViewById(R.id.pot_collectioin_recycler_view)
        potFirstView = rootView.findViewById(R.id.firstView)
        getPotList(mActivity)

        potFirstView.setOnClickListener {
            var dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("새 화분 등록하기")
            val dialogArray = arrayOf("카메라로 등록", "검색으로 등록")

            dialog.setItems(dialogArray) { _, which ->
                when (which) {
                    0 -> {
                        mActivity.setCameraStatus("addPot")
                        mActivity.requirePermissions(
                            arrayOf(android.Manifest.permission.CAMERA),
                            PERMISSION_CAMERA
                        )
                    }

                    1 -> {
                        val plantBottomSheet = PlantBottomSheet(requireContext())
                        plantBottomSheet.show(
                            mActivity.supportFragmentManager,
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




        return rootView
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
                    potList = body.pots
                    setRecyclerView(potList!!, mActivity)

                    if(body.pots.size == 0) {
                        showFirstView()
                    } else {
                        hideFirstView()
                    }
                } else {
                    Log.d(TAG, "실패1")
                    showFirstView()
                }
            }

            override fun onFailure(call: Call<PotListResponse>, t: Throwable) {
                Log.d(TAG, "실패2")
            }
        })
    }

    fun setRecyclerView(potList:List<Pot>, mActivity:MainActivity) {

        potRvAdapter = PotCollectionRVAdapter(potList)
        potCollectionRv.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        potCollectionRv.adapter = potRvAdapter

        potRvAdapter?.setItemClickListener(object : PotCollectionRVAdapter.ItemClickListener {
            override fun onPostBtnClick(view: View, position: Int) {
                mActivity.setPotId(potList?.get(position)?.potId ?: 0)
                mActivity.setPotName(potList?.get(position)?.potName.toString())
                mActivity.setPotPlant(potList?.get(position)?.plantKrName.toString())
                mActivity.setPotCharImg(potList?.get(position)?.characterPNGPath.toString())
                mActivity.changeFragment("pot_diary_create")
            }

            override fun onScanBtnClick(view: View, position: Int) {
                val intent = Intent(context, ArActivity::class.java)
                intent.putExtra("GLBfile", potList?.get(position)?.characterGLBPath.toString())
                intent.putExtra("level", potList?.get(position)?.level.toString())
                intent.putExtra("potName", potList?.get(position)?.potName.toString())
                intent.putExtra("potPlant", potList?.get(position)?.plantKrName.toString())
                startActivity(intent)
            }

            override fun onDetailBtnClick(view: View, position: Int) {
                mActivity.setPotId(potList?.get(position)?.potId ?: 0)
                mActivity.changeFragment("pot_detail")
            }
        })
    }

    fun showFirstView() {
        potFirstView.visibility = View.VISIBLE
    }

    fun hideFirstView() {
        potFirstView.visibility = View.GONE
    }
}