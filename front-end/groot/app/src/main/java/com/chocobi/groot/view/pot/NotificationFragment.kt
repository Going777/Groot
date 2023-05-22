package com.chocobi.groot.view.pot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.pot.adapter.NotificationRVAdapter
import com.chocobi.groot.view.pot.model.NotiMessage
import com.chocobi.groot.view.pot.model.NotiResponse
import com.chocobi.groot.view.pot.model.NotiService
import com.chocobi.groot.view.user.adapter.UserTab1RVAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotificationFragment : Fragment() {
    private lateinit var notiRecyclerView: RecyclerView
    private var notiRVAdapter: NotificationRVAdapter? = null
    private lateinit var firstView: LinearLayout
    private lateinit var mActivity: MainActivity

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
        firstView = view.findViewById(R.id.firstView)
        mActivity = activity as MainActivity
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
                    hideFirstView()
                    setRecyclerView(body.notification.content)
                } else {
                    showFirstView()
                }
            }

            override fun onFailure(call: Call<NotiResponse>, t: Throwable) {
                showFirstView()
            }
        })
    }

    private fun setRecyclerView(notiList: List<NotiMessage>) {
        notiRVAdapter = NotificationRVAdapter(notiList)
        notiRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        notiRecyclerView.adapter = notiRVAdapter

        notiRVAdapter?.itemClick = object : UserTab1RVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                if (notiList?.get(position)?.page == "article") {
                    mActivity.setCommunityArticleId(notiList?.get(position)?.contentId ?: 0)
                    mActivity.changeFragment("community_detail")
                } else if (notiList?.get(position)?.page == "main") {
                    mActivity.setPotId(notiList?.get(position)?.contentId ?: 0)
                    mActivity.changeFragment("pot_detail")
                } else {
//                    mActivity.setChatUserPK(chatUserListResponse.chatting[0].userPK.toString())
//                    mActivity.setChatPickNickName(chatUserListResponse.chatting[0].nickName)
//                    mActivity.setChatPickProfile(chatUserListResponse.chatting[0].profile)
                    mActivity.setChatRoomId(notiList?.get(position)?.chattingRoomId!!)

                    mActivity.changeFragment("chat")
                }
            }
        }
    }

    private fun showFirstView() {
        firstView.visibility = View.VISIBLE
    }

    private fun hideFirstView() {
        firstView.visibility = View.GONE
    }

}