package com.chocobi.groot.view.chat

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.chat.adapter.ChatMessageAdapter
import com.chocobi.groot.view.chat.model.Chat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatFragment : Fragment() {

    private lateinit var receiverRoom: String //받는 대화방
    private lateinit var senderRoom: String //보낸 대화방

    private lateinit var mDbRef: DatabaseReference

    private lateinit var messageList: ArrayList<ChatMessage>
    private lateinit var lastMessage: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        Log.d("받아온", arguments.toString())

        val chatUserPK = arguments?.getString("userPK")
        val chatNickName = arguments?.getString("nickName")
        val chatProfile = arguments?.getString("profile")
        val roomId = arguments?.getString("roomId")

        Log.d("받아온 데이터", chatUserPK.toString())
        Log.d("받아온 데이터", chatNickName.toString())
        Log.d("받아온 데이터", chatProfile.toString())
        Log.d("받아온 데이터", roomId.toString())
        messageList = ArrayList()
        val chatMessageAdapter: ChatMessageAdapter = ChatMessageAdapter(requireContext(), messageList)
        val chatRecyclerView = view.findViewById<RecyclerView>(R.id.chatRecyclerView)
        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatRecyclerView.adapter = chatMessageAdapter

        val categoryNameTextView = view.findViewById<TextView>(R.id.categoryName)
        val categoryIcon = view.findViewById<ImageView>(R.id.categoryIcon)
        val categoryProfileImg = view.findViewById<CircleImageView>(R.id.categoryProfileImg)
        categoryNameTextView
        categoryNameTextView.text = chatNickName
        categoryNameTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        categoryIcon.visibility = View.GONE
        categoryProfileImg.visibility = View.VISIBLE
        if(!chatProfile.isNullOrBlank()) {

        categoryProfileImg.post {
            ThreadUtil.startThread {
                val futureTarget: FutureTarget<Bitmap> = Glide.with(requireContext())
                    .asBitmap()
                    .load(chatProfile)
                    .submit(categoryProfileImg.width, categoryProfileImg.height)

                val bitmap = futureTarget.get()

                ThreadUtil.startUIThread(0) {
                    categoryProfileImg.setImageBitmap(bitmap)
                }
            }
        }
        }

//        ================================================================
//        ================================================================
//        뒤로 가기 버튼 처리해야 하는 곳
        val backBtn = view.findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
//        ================================================================
//        ================================================================


        val createdTime: LocalDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("a h:mm")
        val saveTime = createdTime.format(formatter)

        mDbRef = Firebase.database.reference

        var senderUid = changeRoomNumber(UserData.getUserPK().toString()).toString()
        var receiverUid = changeRoomNumber(chatUserPK).toString()
//        보낸이방
        senderRoom = receiverUid + senderUid
//        받는이방
        receiverRoom = senderUid + receiverUid


        // 메시지 전송
        val sendBtn = view.findViewById<AppCompatButton>(R.id.sendBtn)
        sendBtn.setOnClickListener {
            val messageEdit = view.findViewById<EditText>(R.id.messageEdit)
            val message = messageEdit.text.toString()
            val messageObject = ChatMessage(message, senderUid, saveTime)

            //데이터 저장
            mDbRef.child("chats").child(senderRoom).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    //저장 성공하면
                    mDbRef.child("chats").child(receiverRoom).child("messages").push()
                        .setValue(messageObject)

                }
            lastMessage = messageEdit.toString()
            Log.d("lastMessage", lastMessage)

            messageEdit.text.clear()
        }
        // 메시지 가져오기
        mDbRef.child("chats").child(senderRoom).child("messages")
            .addValueEventListener(object: ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for(postSnapshot in snapshot.children){

                        val message = postSnapshot.getValue(ChatMessage::class.java)
                        messageList.add(message!!)
                        lastMessage = message.toString()
                    }
                    //적용
                    chatMessageAdapter.notifyDataSetChanged()

                    if (messageList.size != 0) {
                        Log.d("lastMessage", lastMessage)
                    }

                }
                override fun onCancelled(error: DatabaseError) {

                }
            })


        return view
    }


    private fun changeRoomNumber(number: String?): String {
        val formattedNumber = number?.padStart(6, '0')
        return formattedNumber ?: ""
    }


}