package com.chocobi.groot.view.chat

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.chat.adapter.ChatMessageAdapter
import com.chocobi.groot.view.chat.model.AddChatRoomService
import com.chocobi.groot.view.chat.model.ChatMessage
import com.chocobi.groot.view.chat.model.ChatRoomRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ChatFragment : Fragment() {

    private lateinit var receiverRoom: String //받는 대화방
    private lateinit var senderRoom: String //보낸 대화방

    private lateinit var mDbRef: DatabaseReference
    private var fireStore: FirebaseFirestore? = null

    private lateinit var messageList: ArrayList<ChatMessage>
    private lateinit var lastMessage: String
    private lateinit var lastTime: String
    private var firstMessage: Boolean = true
    private lateinit var inputLayout: CardView
    private lateinit var chatRecyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)


        Log.d("받아온", arguments.toString())

        val chatUserPK = arguments?.getString("userPK")
        val chatNickName = arguments?.getString("nickName")
        val chatProfile = arguments?.getString("profile")
        val roomId = arguments?.getString("roomId")
        inputLayout = view.findViewById(R.id.inputLayout)

        Log.d("받아온 데이터", chatUserPK.toString())
        Log.d("받아온 데이터", chatNickName.toString())
        Log.d("받아온 데이터", chatProfile.toString())
        Log.d("받아온 데이터", roomId.toString())
        messageList = ArrayList()
        val chatMessageAdapter: ChatMessageAdapter =
            ChatMessageAdapter(requireContext(), messageList)
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
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
        if (!chatProfile.isNullOrBlank()) {

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




        mDbRef = Firebase.database.reference
        fireStore = FirebaseFirestore.getInstance()

        var senderUid = changeRoomNumber(UserData.getUserPK().toString())
        var receiverUid = changeRoomNumber(chatUserPK)

//        보낸이방
        senderRoom = receiverUid + senderUid
//        받는이방
        receiverRoom = senderUid + receiverUid

        val messageEdit = view.findViewById<EditText>(R.id.messageEdit)

        // 메시지 전송
        val sendBtn = view.findViewById<AppCompatButton>(R.id.sendBtn)
        sendBtn.setOnClickListener {
            val createdTime: LocalDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("a h:mm")
            val saveTime = createdTime.format(formatter)

            val message = messageEdit.text.toString()
            val messageObject = ChatMessage(message, senderUid, saveTime)



            // 첫번째 메세지일 때 채팅 목록에 추가
            if (firstMessage == true) {

                // 채팅 목록에 추가하는 api
                val retrofit = RetrofitClient.getClient()!!
                val addChatRoomService = retrofit.create(AddChatRoomService::class.java)

                addChatRoomService.requestAddChatRoom(
                    ChatRoomRequest(
                        userPK = chatUserPK,
                        roomId = receiverRoom

                    )
                ).enqueue(object : Callback<BasicResponse> {
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {
                        if (response.code() == 200) {
                            Log.d("ChatFragment", "채팅 목록에 추가 성공")
                        } else {
                            Log.d("ChatFragment", "채팅 목록에 추가 실패1")
                        }
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                        Log.d("ChatFragment", "채팅 목록에 추가 실패2")
                    }
                })

                lastMessage = message

                fireStore?.collection("chats")?.document(senderRoom)?.set(mapOf("lastMessage" to lastMessage, "saveTime" to saveTime, "receiverRoom" to receiverRoom))?.addOnSuccessListener {
                    fireStore?.collection("chats")?.document(receiverRoom)?.set(mapOf("lastMessage" to lastMessage, "saveTime" to saveTime, "receiverRoom" to receiverRoom))
                }
                firstMessage = false
            }
            //데이터 저장
            mDbRef.child("chats").child(senderRoom).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    //저장 성공하면
                    mDbRef.child("chats").child(receiverRoom).child("messages").push()
                        .setValue(messageObject)

                }


            Log.d("scroll", messageList.size.toString())
            chatRecyclerView.scrollToPosition(messageList.size - 1)

            lastMessage = message
            lastTime = saveTime
            Log.d("lastMessage", lastMessage)

////                파이어베이스에 마지막 메세지 저장
//            Log.d("fireStore", fireStore.toString())
//            fireStore?.collection("chats")?.document(senderRoom)?.delete()?.addOnSuccessListener {
//                fireStore?.collection("chats")?.document(receiverRoom)?.delete()
//            }
            fireStore?.collection("chats")?.document(senderRoom)?.set(mapOf("lastMessage" to lastMessage, "saveTime" to saveTime, "receiverRoom" to receiverRoom))?.addOnSuccessListener {
                fireStore?.collection("chats")?.document(receiverRoom)?.set(mapOf("lastMessage" to lastMessage, "saveTime" to saveTime, "receiverRoom" to receiverRoom))
            }



            messageEdit.text.clear()
        }

        // 메시지 가져오기
        mDbRef.child("chats").child(senderRoom).child("messages")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for (postSnapshot in snapshot.children) {

                        val message = postSnapshot.getValue(ChatMessage::class.java)
                        messageList.add(message!!)
                        lastMessage = message.toString()
                    }
                    //적용
                    chatMessageAdapter.notifyDataSetChanged()
                    Log.d("scroll", messageList.size.toString())
                    chatRecyclerView.scrollToPosition(messageList.size - 1)

                    if (messageList.size != 0) {
                        Log.d("lastMessage", lastMessage)
                    }


//                    알람 처리
                    if (snapshot.hasChildren()) {
                        // 마지막 메시지
                        val lastMessageSnapshot = snapshot.children.last()
                        val lastMessage = lastMessageSnapshot.getValue(ChatMessage::class.java)
                        if (lastMessage != null) {
                            // 여기에 알람을 처리하는 로직을 추가
                            val sender = lastMessage.sendId
                            val message = lastMessage.message
//                            val toastMessage = "새로운 메시지 도착\n보낸이: $sender\n메시지: $message"
//                            Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
                        }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 키보드가 올라올 때 이벤트를 처리하는 리스너 등록
        val activityRootView = requireActivity().window.decorView.findViewById<View>(android.R.id.content)
        activityRootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            activityRootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = activityRootView.rootView.height
            val keyboardHeight = screenHeight - rect.bottom
            if (keyboardHeight > dpToPx(requireContext(), 200)) { // 키보드 높이가 200dp 이상인 경우
                scrollToBottom()
            }
        }
    }

    private fun scrollToBottom() {
        chatRecyclerView.postDelayed({
            if (messageList.size > 2) {
                chatRecyclerView.smoothScrollToPosition(messageList.size - 1)
            }
        }, 0) // 200ms 후에 스크롤 이동 (필요에 따라 조정 가능)
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}