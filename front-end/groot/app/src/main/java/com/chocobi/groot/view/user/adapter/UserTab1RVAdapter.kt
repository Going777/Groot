package com.chocobi.groot.view.user.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.util.ThreadUtil
import com.chocobi.groot.view.pot.model.Date
import com.chocobi.groot.view.pot.model.Pot
import com.google.android.material.chip.Chip
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.ref.WeakReference

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
        private var view: WeakReference<View> = WeakReference(itemView)
        fun bindItems(item: Pot) {
            val potImg = itemView.findViewById<CircleImageView>(R.id.potImg)
            val potName = itemView.findViewById<TextView>(R.id.potName)
            val potPlant = itemView.findViewById<TextView>(R.id.potPlant)
            val potDate = itemView.findViewById<TextView>(R.id.potDate)
            val potDateChip = itemView.findViewById<Chip>(R.id.potDateChip)
            val cardView = itemView.findViewById<CardView>(R.id.cardView)
            potName.text = item.potName
            potPlant.text = item.plantKrName
            potDate.text = make00Date(item.createdTime.date)
            potDateChip.text = "D+" + item.dates.toString()
            if (item.survival == false) {
                cardView.setCardBackgroundColor(Color.parseColor("#ECECEC"))
                potDateChip.setChipBackgroundColorResource(R.color.dark_grey)
            }
            potImg.post {
                view.get()?.let {
                    ThreadUtil.startThread {
                        val futureTarget: FutureTarget<Bitmap> = Glide.with(it.context)
                            .asBitmap()
                            .load(item.imgPath)
                            .submit(potImg.width, potImg.height)

                        val bitmap = futureTarget.get()

                        ThreadUtil.startUIThread(0) {
                            potImg.setImageBitmap(bitmap)
                        }
                    }
                }
            }


        }
    }

    private fun make00Date(date: Date): String {
        val year = date.year.toString()
        val month = String.format("%02d", date.month)
        val day = String.format("%02d", date.day)
        return year + "-" + month + "-" + day
    }
}