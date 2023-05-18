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
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.chat.ChatFragment
import com.chocobi.groot.view.chat.model.ChatMessage
import com.chocobi.groot.view.chat.model.ChatUserListResponse
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.ref.WeakReference


class ChatUserAdapter(private val recyclerView: RecyclerView, private val mActivity: MainActivity) :
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

                mActivity.setChatUserPK(chatUserListResponse.chatting[0].userPK.toString())
                mActivity.setChatPickNickName(chatUserListResponse.chatting[0].nickName)
                mActivity.setChatPickProfile(chatUserListResponse.chatting[0].profile)
                mActivity.setChatRoomId(chatUserListResponse.chatting[0].roomId)

                mActivity.changeFragment("chat")


//                val fragmentManager: FragmentManager =
//                    (recyclerView.context as FragmentActivity).supportFragmentManager
//                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
//
//                val chatFragment = ChatFragment()
//                val bundle = Bundle()
//                bundle.putString("userPK", chatUserListResponse.chatting[0].userPK.toString())
//                bundle.putString("nickName", chatUserListResponse.chatting[0].nickName)
//                bundle.putString("profile", chatUserListResponse.chatting[0].profile)
//                bundle.putString("roomId", chatUserListResponse.chatting[0].roomId)
//                Log.d("받아온 데이터", bundle.toString())
//
//                chatFragment.arguments = bundle
//                fragmentTransaction.replace(R.id.fl_container, chatFragment).addToBackStack("chat")
////                    .commit()
//                    .commitAllowingStateLoss()
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

    private var fireStore: FirebaseFirestore? = null


    private var view: WeakReference<View> = WeakReference(itemView)
    private var findUserPK: Int = 0
    private lateinit var nickname: TextView
    private lateinit var profile: ImageView
    private lateinit var dateText: TextView
    private lateinit var lastMessageText: TextView

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
            dateText = it.findViewById(R.id.dateText)
            lastMessageText = it.findViewById(R.id.lastMessageText)
        }
    }

    private fun setListener() {
        view.get()?.setOnClickListener {
            delegate?.onItemViewClick(chatUserListResponse)
        }
    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    fun updateView() {
        findUserPK = chatUserListResponse.chatting[0].userPK
        if (findUserPK != 0) {

            nickname.text = chatUserListResponse.chatting[0].nickName
            val roomId: String =
                changeRoomNumber(UserData.getUserPK().toString(), findUserPK.toString())
            Log.d("findUserPK", findUserPK.toString())
            Log.d("findUserPKRoomId", roomId.toString())
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
            fireStore = FirebaseFirestore.getInstance()
            Log.d(
                "lastmessage가져오는 중",
                fireStore!!.collection("chats").document(roomId).get().toString()
            )

            data class firebaseResponse(
                val lastMessage: ChatMessage
            )

            fireStore!!.collection("chats").document(roomId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val data = documentSnapshot.data as HashMap<*, *>
                        val lastMessage = data["lastMessage"]
                        val saveTime = data["saveTime"]
                        val receiverRoom = data["receiverRoom"]

                        Log.d("receiverRoom", receiverRoom.toString())
                        Log.d("lastMessage", lastMessage.toString())
                        val messageIdMatch = Regex("""message=(\w+)""").find(lastMessage.toString())
                        val messageIdValue = messageIdMatch?.groupValues?.get(1)

                        val sendIdMatch = Regex("""sendId=(\d+)""").find(lastMessage.toString())
                        val sendIdValue = sendIdMatch?.groupValues?.get(1)

                        val saveTimeMatch =
                            Regex("""saveTime=([\p{IsHangul}\s\d:]+)""").find(lastMessage.toString())
                        val saveTimeValue = saveTimeMatch?.groupValues?.get(1)

                        var receiverId: Int = 0

                            Log.d("receiverRoom", receiverRoom.toString())

                            if (receiverRoom.toString().takeLast(6)
                                    .toInt() != UserData.getUserPK()
                            ) {
                                receiverId = receiverRoom.toString().takeLast(6).toInt()
                            } else {
                                receiverId = receiverRoom.toString().take(6).toInt()
                            }

                            Log.d("lastmessage data", receiverId.toString())
                            Log.d("lastmessage data", messageIdValue.toString())
                            Log.d("lastmessage data", sendIdValue.toString())
                            Log.d("lastmessage data", saveTimeValue.toString())

                            if (findUserPK == receiverId) {
                                Log.d("receiverRoomId", receiverId.toString())
                                Log.d("receiverRoomSaveTime", saveTimeValue.toString())
                                Log.d("receiverRoomLast", messageIdValue.toString())

                                if (saveTimeValue == null) {
                                    dateText.text = saveTime.toString()
                                    lastMessageText.text = lastMessage.toString()
                                } else {
                                    dateText.text = saveTimeValue
                                    lastMessageText.text = messageIdValue
                                }
                            }

                        }
                }
        }

    }

    private fun changeRoomNumber(senderNumber: String?, receiverNumber: String?): String {
        val senderRoomNumber = senderNumber?.padStart(6, '0')
        val receiverRoomNumber = receiverNumber?.padStart(6, '0')

        if (senderRoomNumber == null || receiverRoomNumber == null) {
            return ""
        }

        val formattedNumber = senderRoomNumber + receiverRoomNumber
        return formattedNumber
    }


}