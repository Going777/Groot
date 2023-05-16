package com.chocobi.groot.view.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.chat.model.Chat
import com.chocobi.groot.view.chat.model.ChatUserListResponse
import com.chocobi.groot.view.chat.model.ChatUserListService
import com.chocobi.groot.view.community.CommunityShareItemService
import com.chocobi.groot.view.community.adapter.ChatUserAdapter
import com.chocobi.groot.view.community.adapter.ShareItemAdapter
import com.chocobi.groot.view.community.model.CommunityShareItemResponse
import com.chocobi.groot.view.community.model.ShareArticles
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChatUserListFragment : Fragment() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatUserAdapter
    private lateinit var frameLayoutProgress: FrameLayout
    private lateinit var getData: ChatUserListResponse


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_user_list, container, false)
        findViews(view)
        setListeners()
        initList()
        showProgress()

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById<RecyclerView>(R.id.chatUserList)
        frameLayoutProgress = view.findViewById(R.id.frameLayoutProgress)

        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)


        var retrofit = RetrofitClient.getClient()!!
        var chatUserListService = retrofit.create(ChatUserListService::class.java)
        chatUserListService.requestChatUserList().enqueue(object :
            Callback<ChatUserListResponse> {
                override fun onResponse(call: Call<ChatUserListResponse>, response: Response<ChatUserListResponse>) {
                    if (response.code() == 200) {
                        getData = response.body()!!
                        Log.d("chatUserList", getData.chatting.toString())
                        val list = createDummyData()
                        ThreadUtil.startUIThread(1000) {
                            adapter.reload(list)
                            hideProgress()
                        }
                    } else {
                        Log.d("ChatUserListFragment", "실패 1")
                    }
                }

            override fun onFailure(call: Call<ChatUserListResponse>, t: Throwable) {
                Log.d("ChatUserListFragment", "실패2")

            }
            }

        )



        return view
    }
    private fun findViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById<RecyclerView>(R.id.chatUserList)
        frameLayoutProgress = view.findViewById(R.id.frameLayoutProgress)


    }

    private fun setListeners() {
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initList() {
        adapter = ChatUserAdapter(recyclerView)

        recyclerView.adapter = adapter // RecyclerView에 Adapter 설정
        val size = adapter.itemCount
//        recyclerView.scrollToPosition(size - 1)

        adapter.delegate = object : ChatUserAdapter.RecyclerViewAdapterDelegate {
            override fun onLoadMore() {
            }

            override fun onItemViewClick(chatUserListResponse: ChatUserListResponse) {
                TODO("Not yet implemented")
            }

            fun reload(mutableList: MutableList<ChatUserListResponse>) {
                TODO("Not yet implemented")
            }

            fun loadMore(mutableList: MutableList<ChatUserListResponse>) {
                TODO("Not yet implemented")
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }


    private fun showProgress() {
        frameLayoutProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        frameLayoutProgress.visibility = View.GONE
    }

    private fun createDummyData(): MutableList<ChatUserListResponse> {
        val list: MutableList<ChatUserListResponse> = mutableListOf()

        // API response를 이용하여 데이터 생성
        val chattingItems = getData.chatting
        for (chattingItem in chattingItems) {
            val chatUserListResponse = ChatUserListResponse(
                chatting = listOf(
                    Chat(
                        userPK = chattingItem.userPK ?: 0,
                        nickName = chattingItem.nickName ?: "",
                        roomId = (chattingItem.roomId ?: 0) as String,
                        profile = chattingItem.profile ?: ""
                    )
                ),
                result = getData.result,
                msg = getData.msg
            )
            list.add(chatUserListResponse)
        }
        return list
    }

}