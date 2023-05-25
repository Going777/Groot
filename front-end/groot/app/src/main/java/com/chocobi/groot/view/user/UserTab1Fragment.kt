package com.chocobi.groot.view.user

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.PERMISSION_CAMERA
import com.chocobi.groot.util.RetrofitClient
import com.chocobi.groot.view.pot.PlantBottomSheet
import com.chocobi.groot.view.pot.model.Pot
import com.chocobi.groot.view.pot.model.PotListResponse
import com.chocobi.groot.view.pot.model.PotService
import com.chocobi.groot.view.user.adapter.UserTab1RVAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserTab1Fragment : Fragment() {
    private val TAG = "UserTab1Fragment"
    private var potList: List<Pot>? = null
    private lateinit var potFirstView: ConstraintLayout
    private lateinit var rv: RecyclerView
    private var rvAdapter : UserTab1RVAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_user_tab1, container, false)
        val mActivity = activity as MainActivity
        potFirstView = rootView.findViewById(R.id.firstView)
        potFirstView.setOnClickListener{
            var dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("새 화분 등록하기")
            val dialogArray = arrayOf("카메라로 등록", "식물 이름으로 등록")

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
        rv = rootView.findViewById(R.id.useTab1RecyclerView)

        getPotArchive(mActivity)

        return rootView
    }

    private fun getPotArchive(mActivity: MainActivity) {
        var retrofit = RetrofitClient.getClient()!!
        var potService = retrofit.create(PotService::class.java)
        potService.getPotArchive().enqueue(object :
            Callback<PotListResponse> {
            override fun onResponse(
                call: Call<PotListResponse>,
                response: Response<PotListResponse>
            ) {
                val body = response.body()
                if (body != null && response.code() == 200) {
                    potList = body.pots
                    setRecyclerView(potList!!, mActivity)

                    if(body.pots.size == 0) {
                        showFirstView()
                    } else {
                        hideFirstView()
                    }
                } else {
                    showFirstView()
                }
            }

            override fun onFailure(call: Call<PotListResponse>, t: Throwable) {
            }
        })

    }
    private fun showFirstView() {
        potFirstView.visibility = View.VISIBLE
    }

    private fun hideFirstView() {
        potFirstView.visibility = View.GONE
    }

    fun setRecyclerView(potList:List<Pot>, mActivity: MainActivity) {
        rvAdapter = UserTab1RVAdapter(potList)
        rv.layoutManager = LinearLayoutManager(activity)
        rv.adapter = rvAdapter

        rvAdapter?.itemClick = object : UserTab1RVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                mActivity.setPotId(potList?.get(position)?.potId ?: 0)
                mActivity.changeFragment("pot_detail")
            }
        }
    }
}