package com.chocobi.groot.view.community.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
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


class TagAdapter : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    private val tagList = mutableListOf<String>()

    class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tagTextView: TextView = itemView.findViewById(R.id.tagTextView)

        fun bind(tag: String) {
            tagTextView.text = tag

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_community_post_tag_item, parent, false)
        return TagViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tagList[position]
        holder.bind(tag)

        holder.itemView.setOnClickListener {
            tagList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    fun addTag(tag: String) {
        tagList.add(tag)
        notifyItemInserted(tagList.size - 1)
    }

}

class TagHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val tagTextView: TextView = itemView.findViewById(R.id.tagTextView)

    fun bind(text: String) {
        tagTextView.text = text
        tagTextView.setOnEditorActionListener(null)
    }
}

