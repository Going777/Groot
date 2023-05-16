package com.chocobi.groot.view.community.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.view.chat.ChatFragment
import com.chocobi.groot.view.chat.model.ChatUserListResponse
import java.lang.ref.WeakReference


class ChatUserAdapter(private val recyclerView: RecyclerView) :
    RecyclerView.Adapter<ChatUserViewHolder>() {

    interface RecyclerViewAdapterDelegate {
        fun onLoadMore()
        fun onItemViewClick(chatUserListResponse: ChatUserListResponse)
    }

    private var mutableList: MutableList<ChatUserListResponse> = mutableListOf()


    var delegate: RecyclerViewAdapterDelegate? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_chat_user_list_item, parent, false)
        return ChatUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatUserViewHolder, position: Int) {
        val item = mutableList[holder.adapterPosition]
        holder.chatUserListResponse = item

        holder.delegate = object : ChatUserViewHolder.ChatUserViewHolderDelegate {
            override fun onItemViewClick(chatUserListResponse: ChatUserListResponse) {
                Log.d("ChatUserViewHolder", "clicked")
                val fragmentManager: FragmentManager =
                    (recyclerView.context as FragmentActivity).supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

                val chatFragment = ChatFragment()
                val bundle = Bundle()
                bundle.putString("userPK", chatUserListResponse.chatting[0].userPK.toString())
                bundle.putString("nickName", chatUserListResponse.chatting[0].nickName)
                bundle.putString("profile", chatUserListResponse.chatting[0].profile)
                bundle.putString("roomId", chatUserListResponse.chatting[0].roomId)
                Log.d("받아온 데이터", bundle.toString())

                chatFragment.arguments = bundle
                fragmentTransaction.replace(R.id.fl_container, chatFragment).addToBackStack(null).commit()
            }
        }

        holder.itemView.setOnClickListener {
            holder.delegate?.onItemViewClick(item)
        }
        holder.updateView()

        if (position == mutableList.size - 1 && mutableList.size > 1) {
            delegate?.onLoadMore()
        }
    }


    override fun getItemCount(): Int {
        return mutableList.size
    }

    fun reload(mutableList: MutableList<ChatUserListResponse>) {
        this.mutableList.clear()
        this.mutableList.addAll(mutableList)
        notifyDataSetChanged()
    }

    fun loadMore(mutableList: MutableList<ChatUserListResponse>) {
        this.mutableList.addAll(mutableList)
        notifyItemRangeChanged(this.mutableList.size - mutableList.size, mutableList.size)
    }
}


class ChatUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    interface ChatUserViewHolderDelegate {
        fun onItemViewClick(chatUserListResponse: ChatUserListResponse) {
            Log.d("ChatUserViewHolder", "clicked")
        }
    }

    private var view: WeakReference<View> = WeakReference(itemView)

    private lateinit var nickname: TextView
    private lateinit var profile: ImageView

    var delegate: ChatUserViewHolderDelegate? = null
    lateinit var chatUserListResponse: ChatUserListResponse

    init {
        findView()
        setListener()
    }

    private fun findView() {
        view.get()?.let {
            nickname = it.findViewById(R.id.name_text)
            profile = it.findViewById(R.id.profile_image)
        }
    }

    private fun setListener() {
        view.get()?.setOnClickListener {
            delegate?.onItemViewClick(chatUserListResponse)
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateView() {
        nickname.text = chatUserListResponse.chatting[0].nickName
        profile.post {
            view.get()?.let {
                ThreadUtil.startThread {
                    val futureTarget: FutureTarget<Bitmap> = Glide.with(it.context)
                        .asBitmap()
                        .load(chatUserListResponse.chatting.getOrNull(0)?.profile)
                        .submit(profile.width, profile.height)

                    val bitmap = futureTarget.get()

                    ThreadUtil.startUIThread(0) {
                        profile.setImageBitmap(bitmap)
                    }
                }
            }
        }

    }
}