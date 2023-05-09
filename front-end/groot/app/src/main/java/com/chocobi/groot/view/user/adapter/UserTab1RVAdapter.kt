package com.chocobi.groot.view.user.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.R
import com.chocobi.groot.view.pot.model.Pot

class UserTab1RVAdapter(val items: List<Pot>) :
    RecyclerView.Adapter<UserTab1RVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_user_tab1_item, parent, false)

        return ViewHolder(view)
    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (itemClick != null) {
            holder.itemView.setOnClickListener { v ->
                itemClick?.onClick(v, position)
            }
        }
        holder.bindItems(items[position])
    }

    //    전체 리사이클러뷰의 개수
    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: Pot) {
            val potName = itemView.findViewById<TextView>(R.id.potName)
            potName.text = item.potName

        }
    }
}