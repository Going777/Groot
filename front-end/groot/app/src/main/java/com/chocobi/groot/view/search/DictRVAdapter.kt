package com.chocobi.groot.view.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.R

class DictRVAdapter(val items:MutableList<String>) : RecyclerView.Adapter<DictRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictRVAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.dict_rv_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DictRVAdapter.ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }

//    전체 리사이클러뷰의 개수
    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item:String) {
            val rv_text = itemView.findViewById<TextView>(R.id.dictRvItem)
            rv_text.text = item

        }
    }
}