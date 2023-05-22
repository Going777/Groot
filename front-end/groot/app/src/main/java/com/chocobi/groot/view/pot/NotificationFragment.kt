package com.chocobi.groot.view.pot

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.R
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.mlkit.kotlin.ml.ArActivity
import com.chocobi.groot.view.pot.adapter.NotificationRVAdapter
import com.chocobi.groot.view.pot.adapter.PotCollectionRVAdapter
import com.chocobi.groot.view.pot.model.NotiMessage
import com.chocobi.groot.view.pot.model.NotiResponse
import com.chocobi.groot.view.pot.model.NotiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotificationFragment : Fragment() {
    private lateinit var notiRecyclerView: RecyclerView
    private var notiRVAdapter: NotificationRVAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        notiRecyclerView = view.findViewById(R.id.notiRecyclerView)

        getNotiList()


        val backBtn = view.findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        return view
    }

    private fun getNotiList() {
        val retrofit = RetrofitClient.getClient()!!
        val notiService = retrofit.create(NotiService::class.java)

        notiService.getNotiList(0, 100).enqueue(object : Callback<NotiResponse> {
            override fun onResponse(call: Call<NotiResponse>, response: Response<NotiResponse>) {
                val body = response.body()
                if (body?.notification?.content != null) {
                    setRecyclerView(body.notification.content)
                }
            }

            override fun onFailure(call: Call<NotiResponse>, t: Throwable) {

            }
        })
    }

    private fun setRecyclerView(notiList: List<NotiMessage>) {
        notiRVAdapter = NotificationRVAdapter(notiList)
        notiRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        notiRecyclerView.adapter = notiRVAdapter
    }

}