package com.chocobi.groot.view.community.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.R
import com.chocobi.groot.view.community.model.TagItem
import java.lang.ref.WeakReference


class ArticleTagAdapter(private val items: List<String>) : RecyclerView.Adapter<ArticleTagAdapter.ViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = items[position]
        holder.bind(tag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_community_article_tag_item, parent, false)
        return ViewHolder(itemView)
    }

    // 각 항목에 필요한 기능을 구현
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
//        private var view: View = v
//        fun bind(listener: View.OnClickListener, item: Tags) {
//            view.tagListItem = item.tag
//            view.setOnClickListener(listener)
//        }

        interface ItemViewHolderDelegate {
            fun onItemViewClick(tags: TagItem) {
                Log.d("ItemViewHolder", "clicked")
            }
        }

        private var view: WeakReference<View> = WeakReference(itemView)

        private lateinit var tag: TextView

        var delegate: ItemViewHolderDelegate? = null
        lateinit var tags: TagItem

        init {
            findView()
        }

        private fun findView() {
            view.get()?.let {
                tag = it.findViewById(R.id.tagListItem)
            }
        }
        private val tagTextView: TextView = itemView.findViewById(R.id.tagListItem)

        fun bind(tag: String) {
            tagTextView.text = tag
        }
    }
}