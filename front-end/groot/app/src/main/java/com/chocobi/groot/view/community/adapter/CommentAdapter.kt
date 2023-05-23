package com.chocobi.groot.view.community.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.community.CommunityCommentPostService
import com.chocobi.groot.view.community.model.CommunityCommentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference


class CommentAdapter(
    private val recyclerView: RecyclerView,
    private val context: Context, private val mActivity: MainActivity
) :
    RecyclerView.Adapter<CommentItemViewHolder>() {

    interface RecyclerViewAdapterDelegate {
        fun onLoadMore()
    }

    interface ItemClickListener {
        fun onDeleteBtnClick(view: View, position: Int)
    }

    private lateinit var deleteBtnClickListner: CommentAdapter.ItemClickListener

    fun setItemClickListener(itemClickListener: CommentAdapter.ItemClickListener) {
        this.deleteBtnClickListner = itemClickListener
    }

    private var mutableList: MutableList<CommunityCommentResponse> = mutableListOf()


    var delegate: RecyclerViewAdapterDelegate? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_community_comment_item, parent, false)
        return CommentItemViewHolder(view, mActivity)
    }

    override fun onBindViewHolder(holder: CommentItemViewHolder, position: Int) {
        holder.communityCommentResponse = mutableList[position]

        val deleteButton = holder.itemView.findViewById<ImageButton>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("댓글 삭제")
            dialog.setMessage("댓글을 삭제하시겠습니까?")
            dialog.setNegativeButton(
                "취소",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
            dialog.setPositiveButton(
                "확인",
                DialogInterface.OnClickListener { dialog, which ->
                    holder.deleteComment(holder.communityCommentResponse.comment[0].id)
                    Handler(Looper.getMainLooper()).postDelayed({
                        deleteBtnClickListner.onDeleteBtnClick(it, position)
                    }, 500)
                    dialog.dismiss()
                })
            dialog.show()
        }

        holder.delegate = object : CommentItemViewHolder.CommentItemViewHolderDelegate {

        }

        holder.updateView()


        if (position == mutableList.size - 1) {
            delegate?.onLoadMore()
        }
    }

    override fun getItemCount(): Int {
        return mutableList.size
    }

    fun reload(mutableList: MutableList<CommunityCommentResponse>) {
        this.mutableList.clear()
        this.mutableList.addAll(mutableList)
        notifyDataSetChanged()
    }

    fun loadMore(mutableList: MutableList<CommunityCommentResponse>) {
        this.mutableList.addAll(mutableList)
        notifyItemRangeChanged(this.mutableList.size - mutableList.size, mutableList.size)
    }

    fun addComment(comment: CommunityCommentResponse) {
        mutableList.add(comment)
        notifyItemInserted(mutableList.size - 1)
    }
}


class CommentItemViewHolder(itemView: View, private val mActivity: MainActivity) :
    RecyclerView.ViewHolder(itemView) {

    interface CommentItemViewHolderDelegate {
        fun onItemViewClick(communityCommentResponse: CommunityCommentResponse) {
            Log.d("ShareItemViewHolder", "clicked")
        }
    }

    private val TAG = "CommentItemViewHolder"

    private var view: WeakReference<View> = WeakReference(itemView)

    private lateinit var commentProfileData: LinearLayout
    private lateinit var commentId: TextView
    private var userPK: String = "0"
    private var pickNickName: String = ""
    private var pickProfile: String = ""
    private lateinit var content: TextView
    private lateinit var nickName: TextView
    private lateinit var createTime: TextView
    private lateinit var updateTime: TextView
    private lateinit var profile: ImageView
    private lateinit var deleteButton: ImageButton


    var delegate: CommentItemViewHolderDelegate? = null
    lateinit var communityCommentResponse: CommunityCommentResponse

    init {
        findView()
        setListener()
    }

    private fun findView() {
        view.get()?.let {
            commentProfileData = it.findViewById(R.id.commentProfileData)
            profile = it.findViewById(R.id.commentProfileImg)
            content = it.findViewById(R.id.commentContext)
            nickName = it.findViewById(R.id.commentNickname)
            createTime = it.findViewById(R.id.commentDate)
            deleteButton = it.findViewById(R.id.deleteButton)
        }
    }

    private fun setListener() {
        commentProfileData.setOnClickListener {
            val moveChatDialog = AlertDialog.Builder(it.context)
            moveChatDialog.setMessage("$pickNickName 님과 채팅을 하시겠습니까?")
            moveChatDialog.setPositiveButton("OK") { dialog, which ->

                val roomId =
                    changeRoomNumber(UserData.getUserPK().toString(), userPK.toString())
                mActivity.setChatUserPK(userPK)
                mActivity.setChatPickNickName(pickNickName)
                mActivity.setChatPickProfile(pickProfile)
                mActivity.setChatRoomId(roomId)

                mActivity.changeFragment("chat")


//                val fragmentActivity = it.context as? FragmentActivity
//                if (fragmentActivity != null) {
//                    val fragmentManager: FragmentManager = fragmentActivity.supportFragmentManager
//                    val fragmentTransaction: FragmentTransaction =
//                        fragmentManager.beginTransaction()
//
//                    val chatFragment = ChatFragment()
//                    val bundle = Bundle()
//                    bundle.putString("userPK", userPK)
//                    bundle.putString("nickName", pickNickName)
//                    bundle.putString("profile", pickProfile)
//                    bundle.putString("roomId", roomId)
//                    Log.d("받아온 데이터", bundle.toString())
//
//                    chatFragment.arguments = bundle
//                    fragmentTransaction.replace(R.id.fl_container, chatFragment)
//                        .addToBackStack(null).commit()
//                }
            }
            moveChatDialog.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
            moveChatDialog.create().show()
        }

    }

    @SuppressLint("SetTextI18n")
    fun updateView() {
        var comment = communityCommentResponse.comment[0]
        userPK = comment.userPK.toString()
        pickNickName = comment.nickName
        pickProfile = comment.profile.toString()
        nickName.text = comment.nickName
        createTime.text =
            comment.createTime.date.year.toString() + '.' + comment.createTime.date.month.toString() + '.' + comment.createTime.date.day.toString() + ' ' + comment.createTime.time.hour + ':' + comment.createTime.time.minute.toString()

        if (comment.userPK == UserData.getUserPK()) {
            deleteButton.visibility = View.VISIBLE
        } else {
            deleteButton.visibility = View.GONE
        }
//        deleteButton.setOnClickListener {
//            deleteComment(comment.id)
//        }
        nickName.text = comment.nickName
        val koreahour = comment.createTime.time.hour + 9
        createTime.text =
            comment.createTime.date.year.toString() + '.' + comment.createTime.date.month.toString() + '.' + comment.createTime.date.day.toString() + ' ' + comment.createTime.time.hour + ':' + comment.createTime.time.minute.toString()
        content.text = comment.content

        if (comment.profile != null && comment.profile != "") {
            profile.post {
                view.get()?.let {
                    ThreadUtil.startThread {
                        val futureTarget: FutureTarget<Bitmap> = Glide.with(it.context)
                            .asBitmap()
                            .load(communityCommentResponse.comment.getOrNull(0)?.profile)
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


    }

    private fun changeRoomNumber(senderNumber: String?, receiverNumber: String?): String {
        val senderRoomNumber = senderNumber?.padStart(6, '0')
        val receiverRoomNumber = receiverNumber?.padStart(6, '0')
        val formattedNumber = senderRoomNumber + receiverRoomNumber

        return formattedNumber ?: ""
    }

    fun deleteComment(commentId: Int) {
        val userPK = UserData.getUserPK()
        var retrofit = RetrofitClient.getClient()!!
        val communityCommentPostService = retrofit.create(CommunityCommentPostService::class.java)

        communityCommentPostService.requestCommentDelete(commentId, userPK)
            .enqueue(object : Callback<CommunityCommentResponse> {
                override fun onResponse(
                    call: Call<CommunityCommentResponse>,
                    response: Response<CommunityCommentResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "댓글 삭제 성공")
                    }

                }

                override fun onFailure(call: Call<CommunityCommentResponse>, t: Throwable) {
                    Log.d(TAG, "댓글 삭제 실패")

                }
            })

    }
}

