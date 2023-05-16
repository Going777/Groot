package com.chocobi.groot.view.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.R
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.chat.adapter.ChatMessageAdapter
import com.chocobi.groot.view.chat.model.Chat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatFragment : Fragment() {

    private lateinit var receiverRoom: String //받는 대화방
    private lateinit var senderRoom: String //보낸 대화방

    private lateinit var mDbRef: DatabaseReference

    private lateinit var messageList: ArrayList<ChatMessage>


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
            val messageObject = ChatMessage(message, senderUid)

            //데이터 저장
            mDbRef.child("chats").child(senderRoom).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    //저장 성공하면
                    mDbRef.child("chats").child(receiverRoom).child("messages").push()
                        .setValue(messageObject)

                }
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
                    }
                    //적용
                    chatMessageAdapter.notifyDataSetChanged()
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