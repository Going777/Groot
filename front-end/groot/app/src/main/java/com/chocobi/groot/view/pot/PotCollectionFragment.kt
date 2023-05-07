package com.chocobi.groot.view.pot

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
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
        val mactivity = activity as MainActivity
        potCollectionRv =
            rootView.findViewById<RecyclerView>(R.id.pot_collectioin_recycler_view)

        getPotList()

        potRvAdapter?.setItemClickListener(object : PotCollectionRVAdapter.ItemClickListener {
            override fun onPostBtnClick(view: View, position: Int) {
                mactivity.changeFragment("pot_diary_create")
            }

            override fun onScanBtnClick(view: View, position: Int) {
                val intent = Intent(context, ArActivity::class.java)
                startActivity(intent)
            }

            override fun onDetailBtnClick(view: View, position: Int) {
                mactivity.changeFragment("pot_detail")
            }
        })

        return rootView
    }

    fun getPotList() {
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
                    setRecyclerView(potList!!)
                } else {
                    Log.d(TAG, "실패1")
                }
            }

            override fun onFailure(call: Call<PotListResponse>, t: Throwable) {
                Log.d(TAG, "실패2")
            }
        })
    }

    fun setRecyclerView(potList:List<Pot>) {
        potRvAdapter = PotCollectionRVAdapter(potList)
        potCollectionRv.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        potCollectionRv.adapter = potRvAdapter
    }
}