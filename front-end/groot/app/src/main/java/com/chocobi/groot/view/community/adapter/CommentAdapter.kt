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
import com.chocobi.groot.view.community.model.CommunityCommentResponse
import java.lang.ref.WeakReference


class CommentAdapter: RecyclerView.Adapter<ItemViewHolder>() {

    interface RecyclerViewAdapterDelegate {
        fun onLoadMore()
    }

    private var mutableList: MutableList<CommunityCommentResponse> = mutableListOf()


    var delegate: RecyclerViewAdapterDelegate? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_community_comment_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.communityCommentResponse = mutableList[position]

        holder.delegate = object : ItemViewHolder.ItemViewHolderDelegate {

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

}


class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    interface ItemViewHolderDelegate {
        fun onItemViewClick(communityCommentResponse: CommunityCommentResponse) {
            Log.d("ItemViewHolder", "clicked")
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

    var delegate: ItemViewHolderDelegate? = null
    lateinit var communityCommentResponse: CommunityCommentResponse

    init {
        findView()
        setListener()
    }

    private fun findView() {
        view.get()?.let {
//            commentId = it.findViewById(R.id.commentId)
//            userPK = it.findViewById(R.id.userPK)
            content = it.findViewById(R.id.commentContext)
            nickName = it.findViewById(R.id.commentNickname)
            createTime= it.findViewById(R.id.commentDate)
            profile = it.findViewById(R.id.commentProfileImg)
        }
    }

    private fun setListener() {
        view.get()?.setOnClickListener {
            delegate?.onItemViewClick(communityCommentResponse)
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateView() {
        content.text = communityCommentResponse.comment.content[0].content
        nickName.text = communityCommentResponse.comment.content[0].nickName
//        profile.text = communityCommentResponse.comment.content[0].commentCnt.toString()
        val koreahour = communityCommentResponse.comment.content[0].createTime.time.hour + 9
        createTime.text = communityCommentResponse.comment.content[0].createTime.date.year.toString() + '.'+ communityCommentResponse.comment.content[0].createTime.date.month.toString() + '.' + communityCommentResponse.comment.content[0].createTime.date.day.toString() + ' ' + koreahour + ':'+ communityCommentResponse.comment.content[0].createTime.time.minute.toString()

    }
}