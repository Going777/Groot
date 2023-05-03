package com.chocobi.groot.view.search.adapter

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.view.search.model.PlantMetaData
import java.lang.ref.WeakReference

class DictRVAdapter(var items: Array<PlantMetaData>) :
    RecyclerView.Adapter<DictRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_search_result_item, parent, false)

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

    fun setData(plants: Array<PlantMetaData>) {
        this.items = plants
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var view: WeakReference<View> = WeakReference(itemView)

        fun bindItems(item: PlantMetaData) {
            val rv_img = itemView.findViewById<ImageView>(R.id.dictRvItemImage)
            val rv_text = itemView.findViewById<TextView>(R.id.dictRvItemName)
            rv_text.text = item.krName

            rv_img.post {
                view.get()?.let {
                    ThreadUtil.startThread {
                        val futureTarget: FutureTarget<Bitmap> = Glide.with(it.context)
                            .asBitmap()
                            .load(item.img)
                            .submit(rv_img.width, rv_img.height)

                        val bitmap = futureTarget.get()

                        ThreadUtil.startUIThread(0) {
                            rv_img.setImageBitmap(bitmap)
                        }
                    }
                }
            }
        }
    }
}