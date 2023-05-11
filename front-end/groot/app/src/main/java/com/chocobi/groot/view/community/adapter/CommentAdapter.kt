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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.view.community.model.Comment
import com.chocobi.groot.view.community.model.CommunityCommentResponse
import com.chocobi.groot.view.community.model.CommunityShareItemResponse
import java.lang.ref.WeakReference


class CommentAdapter(private val recyclerView: RecyclerView): RecyclerView.Adapter<CommentItemViewHolder>() {

    interface RecyclerViewAdapterDelegate {
        fun onLoadMore()
    }

    private var mutableList: MutableList<CommunityCommentResponse> = mutableListOf()


    var delegate: RecyclerViewAdapterDelegate? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_community_comment_item, parent, false)
        return CommentItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentItemViewHolder, position: Int) {
        holder.communityCommentResponse = mutableList[position]

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


class CommentItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    interface CommentItemViewHolderDelegate {
        fun onItemViewClick(communityCommentResponse: CommunityCommentResponse) {
            Log.d("ShareItemViewHolder", "clicked")
        }
    }

    private var view: WeakReference<View> = WeakReference(itemView)

    private lateinit var commentId: TextView
    private lateinit var userPK: TextView
    private lateinit var content: TextView
    private lateinit var nickName: TextView
    private lateinit var createTime: TextView
    private lateinit var updateTime: TextView
    private lateinit var profile: ImageView


    var delegate: CommentItemViewHolderDelegate? = null
    lateinit var communityCommentResponse: CommunityCommentResponse

    init {
        findView()
        setListener()
    }

    private fun findView() {
        view.get()?.let {
            profile = it.findViewById(R.id.commentProfileImg)
            content = it.findViewById(R.id.commentContext)
            nickName = it.findViewById(R.id.commentNickname)
            createTime= it.findViewById(R.id.commentDate)

        }
    }

    private fun setListener() {
        view.get()?.setOnClickListener {
            delegate?.onItemViewClick(communityCommentResponse)
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateView() {
        nickName.text = communityCommentResponse.comment[0].nickName
        val koreahour = communityCommentResponse.comment[0].createTime.time.hour + 9
        createTime.text = communityCommentResponse.comment[0].createTime.date.year.toString() + '.'+ communityCommentResponse.comment[0].createTime.date.month.toString() + '.' + communityCommentResponse.comment[0].createTime.date.day.toString() + ' ' + communityCommentResponse.comment[0].createTime.time.hour + ':'+ communityCommentResponse.comment[0].createTime.time.minute.toString()
        content.text = communityCommentResponse.comment[0].content
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

    }


}