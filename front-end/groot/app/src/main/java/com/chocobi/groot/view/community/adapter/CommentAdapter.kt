package com.chocobi.groot.view.community.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.R
import com.chocobi.groot.view.community.model.CommunityCommentResponse


class CommentAdapter(private val commentList: ArrayList<CommunityCommentResponse>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_community_comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.commentProfileImg.text = commentList[position].ProfileImg
        holder.commentNickname.text = commentList[position].comment.content[0].nickName
        holder.commentDate.text = commentList[position].comment.content[0].createTime.toString()
        holder.commentContext.text = commentList[position].comment.content[0].content
    }

    override fun getItemCount(): Int {
        return commentList.count()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val commentProfileImg = itemView.findViewById<ImageView>(R.id.commentProfileImg)
        val commentNickname = itemView.findViewById<TextView>(R.id.commentNickname)
        val commentDate = itemView.findViewById<TextView>(R.id.commentDate)
        val commentContext = itemView.findViewById<TextView>(R.id.commentContext)
    }
}