package com.chocobi.groot.view.community.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.R
import java.lang.ref.WeakReference

class PopularTagAdapter(var items: List<String>) :
    RecyclerView.Adapter<PopularTagAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_community_popular_tag_item, parent, false)

        return ViewHolder(view)
    }

    interface ItemClick {
        fun onClick(view: View, position: Int, item: String)
    }

    var itemClick: ItemClick? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (itemClick != null) {
            holder.itemView.setOnClickListener { v ->
                itemClick?.onClick(v, position, items[position])
            }
        }
        holder.bindItems(items[position], position)
    }

    //    전체 리사이클러뷰의 개수
    override fun getItemCount(): Int {
        return items.size
    }

    fun setData(tagList: List<String>) {
        this.items = tagList
//        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var view: WeakReference<View> = WeakReference(itemView)

        fun bindItems(item: String, idx: Int) {
            val rankText = itemView.findViewById<TextView>(R.id.rankText)
            val tagNameText = itemView.findViewById<TextView>(R.id.tagNameText)

            rankText.text = (idx+1).toString()
            tagNameText.text = item
        }
    }
}