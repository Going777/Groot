package com.chocobi.groot.view.community.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.R


class TagAdapter : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    private val tagList = mutableListOf<String>()

    class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tagTextView: TextView = itemView.findViewById(R.id.tagTextView)

        fun bind(tag: String) {
            tagTextView.text = tag

        }
    }

    fun getItem(position: Int): String? {
        return tagList[position]
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_community_post_tag_item, parent, false)
        return TagViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
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

    fun containsTag(tag: String?): Boolean {
        return tagList.contains(tag)
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

