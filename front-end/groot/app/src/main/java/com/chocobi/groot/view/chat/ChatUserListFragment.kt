package com.chocobi.groot.view.chat

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.util.ThreadUtil
import com.chocobi.groot.util.RetrofitClient
import com.chocobi.groot.view.chat.model.Chat
import com.chocobi.groot.view.chat.model.ChatUserListResponse
import com.chocobi.groot.view.chat.model.ChatUserListService
import com.chocobi.groot.view.community.adapter.ChatUserAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChatUserListFragment : Fragment() {

    private val TAG = "ChatUserListFragment"

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatUserAdapter
    private lateinit var frameLayoutProgress: FrameLayout
    private lateinit var getData: ChatUserListResponse

    private lateinit var mActivity: MainActivity
    private lateinit var noChat: LinearLayout

    private lateinit var applicationContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        applicationContext = context.applicationContext
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_user_list, container, false)

        mActivity = activity as MainActivity
        val categoryNameTextView = view.findViewById<TextView>(R.id.categoryName)
        val categoryIcon = view.findViewById<ImageView>(R.id.categoryIcon)
        categoryNameTextView.text = "채팅방"
        categoryIcon.setImageResource(R.drawable.ic_chat)

//        ================================================================
//        ================================================================
//        뒤로 가기 버튼 처리해야 하는 곳
        val backBtn = view.findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
//        ================================================================
//        ================================================================

        findViews(view)
        setListeners()
//        initList()
        showProgress()

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById<RecyclerView>(R.id.chatUserList)
        frameLayoutProgress = view.findViewById(R.id.frameLayoutProgress)

        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        noChat = view.findViewById(R.id.noChat)

        val swipeHelperCallback = SwipeHelperCallback().apply{
            setClamp(200f)
        }
        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext)

            setOnTouchListener { _, _ ->
                swipeHelperCallback.removePreviousClamp(this)
                false
            }
        }

//        val cancelSwipeHelperCallback = SwipeHelperCallback().apply{
//            setClamp(-210f)
//        }
//        val cancelItemTouchHelper = ItemTouchHelper(cancelSwipeHelperCallback)
//        cancelItemTouchHelper.attachToRecyclerView(recyclerView)
//        recyclerView.apply {
//            layoutManager = LinearLayoutManager(applicationContext)
//            setOnTouchListener { _, event ->
//                cancelSwipeHelperCallback.removeCurrentClamp(this)
//                false
//            }
//        }

        adapter = ChatUserAdapter(recyclerView, mActivity)
        recyclerView.adapter = adapter

        getChatUser()
        return view
    }

    private fun findViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById<RecyclerView>(R.id.chatUserList)
        frameLayoutProgress = view.findViewById(R.id.frameLayoutProgress)


    }

    private fun setListeners() {
        swipeRefreshLayout.setOnRefreshListener {
            ThreadUtil.startUIThread(100) {
                val list = createDummyData()
                adapter.reload(list)
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun initList() {
//        adapter = ChatUserAdapter(recyclerView, mActivity)
//
//        recyclerView.adapter = adapter // RecyclerView에 Adapter 설정
        val size = adapter.itemCount
//        recyclerView.scrollToPosition(size - 1)

        adapter.delegate = object : ChatUserAdapter.RecyclerViewAdapterDelegate {
            override fun onLoadMore() {
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
        adapter.setItemClickListener(object : ChatUserAdapter.ItemClickListener {
            override fun onDeleteBtnClick(view: View, position: Int) {
                getChatUser()
            }
        })
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

    private fun getChatUser() {

        var retrofit = RetrofitClient.getClient()!!
        var chatUserListService = retrofit.create(ChatUserListService::class.java)
        chatUserListService.requestChatUserList().enqueue(object :
            Callback<ChatUserListResponse> {
            override fun onResponse(
                call: Call<ChatUserListResponse>,
                response: Response<ChatUserListResponse>
            ) {
                if (response.code() == 200) {
                    getData = response.body()!!
                    Log.d("chatUserList", getData.chatting.toString())
                    if (getData.chatting.size != 0) {
                        val list = createDummyData()
                        ThreadUtil.startUIThread(100) {
                            adapter.reload(list)
                            hideProgress()
                        }
                        Log.d("chattingList", getData.chatting.toString())

                        noChat.visibility = View.GONE
                        swipeRefreshLayout.visibility = View.VISIBLE
                    } else {
                        Log.d("chattingList", getData.chatting.toString())
                        noChat.visibility = View.VISIBLE
                        swipeRefreshLayout.visibility = View.GONE


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



    }

}