package com.chocobi.groot.view.community.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.chat.model.ChatUserListResponse
import com.chocobi.groot.view.chat.model.DeleteRoomService
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import io.github.sceneview.utils.TAG
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ChatUserAdapter(
    private val recyclerView: RecyclerView,
    private val mActivity: MainActivity) :
    RecyclerView.Adapter<ChatUserViewHolder>() {


    interface RecyclerViewAdapterDelegate {
        fun onLoadMore()
    }

    interface ItemClickListener {
        fun onDeleteBtnClick(view: View, position: Int)
    }

    private var mutableList: MutableList<ChatUserListResponse> = mutableListOf()
    private var deleteBtnClickListner: ItemClickListener? = null

    var delegate: RecyclerViewAdapterDelegate? = null
    private lateinit var context: Context
    lateinit var mDatabase: FirebaseDatabase
    lateinit var dataRef: DatabaseReference

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.deleteBtnClickListner = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_chat_user_list_item, parent, false)
        mDatabase = FirebaseDatabase.getInstance()
        return ChatUserViewHolder(view)
    }
    private fun deleteChatUser(position: Int) {
        val roomId = mutableList[position].chatting[0].roomId

        val retrofit = RetrofitClient.getClient()!!
        val deleteRoomService = retrofit.create(DeleteRoomService::class.java)
        deleteRoomService.requestDeleteRoom(roomId)
            .enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "채팅방 삭제 성공")

                        mutableList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, mutableList.size)

                        val databaseReference = mDatabase.reference
                        val chatsReference = databaseReference.child("chats")
                        val roomReference = chatsReference.child(roomId)
                        roomReference.removeValue()
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Log.d(TAG, "채팅방 삭제 실패")
                }
            })
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
            }
        }

        holder.itemView.setOnClickListener {
            holder.delegate?.onItemViewClick(item)
        }
        holder.updateView()

        if (position == mutableList.size - 1 && mutableList.size > 1) {
            delegate?.onLoadMore()
        }


        // 채팅방 삭제
        val chatRoomDeleteBtn = holder.itemView.findViewById<ImageButton>(R.id.chatRoomDeleteBtn)
        chatRoomDeleteBtn.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("채팅방 나가기")
            dialog.setMessage("채팅방을 나가시겠습니까?")
            dialog.setNegativeButton(
                "취소",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
            dialog.setPositiveButton(
                "확인",
                DialogInterface.OnClickListener { dialog, which ->
                    deleteChatUser(holder.adapterPosition)
                })
            dialog.show()
        }
        holder.updateView()
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

    private lateinit var swipeView: RelativeLayout
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
            swipeView = it.findViewById(R.id.swipeView)
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
            if (chatUserListResponse.chatting[0].profile != null) {
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
            } else {
                profile.setImageResource(R.drawable.basic_profile)
            }

            fireStore = FirebaseFirestore.getInstance()
            Log.d(
                "lastmessage가져오는 중",
                fireStore!!.collection("chats").document(roomId).get().toString()
            )


            fireStore!!.collection("chats").document(roomId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val data = documentSnapshot.data as HashMap<*, *>
                        val lastMessage = data["lastMessage"]
                        var saveTime = data["saveTime"]
                        val receiverRoom = data["receiverRoom"]

                        val pattern = Regex("message=(.*?)(?=\\n|, sendId)")
                        val matchResult = pattern.find(lastMessage.toString())
                        val messageIdValue = matchResult?.groupValues?.get(1)

                        val sendIdMatch = Regex("""sendId=(\d+)""").find(lastMessage.toString())
                        val sendIdValue = sendIdMatch?.groupValues?.get(1)

                        val saveTimeMatch =
                            Regex("saveTime=(\\d{4}-\\d{2}-\\d{2} (?:오후|오전) \\d{2}:\\d{2})").find(lastMessage.toString())
                        var saveTimeValue = saveTimeMatch?.groupValues?.get(1)
                        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                        var receiverId: Int = 0

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

                                if (messageIdValue != null) {
                                    val saveTimeFormatted = saveTime.toString().let { value ->
                                        val regex = Regex("(\\d{4}-\\d{2}-\\d{2}) (오전|오후) (\\d{1,2}:\\d{2})")
                                        val matchResult = regex.find(value)
                                        val (date, amPm, time) = matchResult?.destructured ?: return@let null

                                        if (date == currentDate) "$amPm $time" else date
                                    }
                                    dateText.text = saveTimeFormatted.toString()
                                    lastMessageText.text = messageIdValue
                                    Log.d("saveTime", saveTime.toString())
                                    Log.d("messageIdValue", lastMessage.toString())


                                } else {
                                    val saveTimeFormatted = saveTime.toString().let { value ->
                                        val regex = Regex("(\\d{4}-\\d{2}-\\d{2}) (오전|오후) (\\d{1,2}:\\d{2})")
                                        val matchResult = regex.find(value)
                                        val (date, amPm, time) = matchResult?.destructured ?: return@let null

                                        if (date == currentDate) "$amPm $time" else date
                                    }

                                    Log.d("messageIdValue", messageIdValue.toString())
                                    dateText.text = saveTimeFormatted
                                    lastMessageText.text = lastMessage.toString()
                                    Log.d("saveTime", saveTimeValue.toString())

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