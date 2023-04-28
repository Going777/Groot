package com.chocobi.groot.view.plant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.R

class PlantCollectionRVAdapter(val items: MutableList<String>): RecyclerView.Adapter<PlantCollectionRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantCollectionRVAdapter.ViewHolder {
        android.util.Log.d("로그", "PlantCollectionRVAdapter, $items")
        var view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_plant_collection_item, parent, false)

        return ViewHolder(view)
    }

//    interface ItemClick {
//        fun onClick(view: View, position: Int)
//    }
//    var itemClick : ItemClick? = null

    override fun onBindViewHolder(holder: PlantCollectionRVAdapter.ViewHolder, position: Int) {
//        if ( itemClick != null) {
//            holder.itemView.setOnClickListener {
//                    v ->
//                itemClick?.onClick(v, position)
//            }
//        }
        holder.bindItems(items[position])
    }

    //    전체 리사이클러뷰의 개수
    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item:String) {
            val rv_text = itemView.findViewById<TextView>(R.id.plant_name)
            rv_text.text = item

        }
    }


}