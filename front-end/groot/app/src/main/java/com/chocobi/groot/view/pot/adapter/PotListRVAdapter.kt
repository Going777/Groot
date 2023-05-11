package com.chocobi.groot.view.pot.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
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
import com.chocobi.groot.view.pot.model.Date
import com.chocobi.groot.view.pot.model.DateTime
import com.chocobi.groot.view.pot.model.Pot
import com.chocobi.groot.view.pot.model.Time
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.ref.WeakReference

class PotListRVAdapter(val items: List<Pot>, val position: Int?) :
    RecyclerView.Adapter<PotListRVAdapter.ViewHolder>() {

    private val TAG = "PotListRVAdapter"
    private var selectedPosition = 0
    private var isFirst : Boolean = true


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        Log.d(TAG, "onCreateViewHolder() $items")
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_pot_list_item, parent, false)

        if (position is Int && isFirst) {
            selectedPosition = position
            isFirst = false
        }
        Log.d(TAG, "oncreateviewholder $selectedPosition")
        Log.d(TAG, "oncreateviewholder $isFirst")

        return ViewHolder(view)
    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null


    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        if (selectedPosition == position) {
            holder.itemView.findViewById<CircleImageView>(R.id.potImage).borderWidth = 10
        } else {
            holder.itemView.findViewById<CircleImageView>(R.id.potImage).borderWidth = 0
        }

        if (itemClick != null) {
//            리스너와 연결
            holder.itemView.setOnClickListener { v ->
                itemClick?.onClick(v, position)

//                border 설정
                holder.itemView.findViewById<CircleImageView>(R.id.potImage).borderWidth = 10
                if (selectedPosition != position && selectedPosition != -1) {
                    notifyItemChanged(selectedPosition)
                }
                selectedPosition = position
                Log.d(TAG, "$selectedPosition")
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
            val rv_text = itemView.findViewById<TextView>(R.id.potName)
            val potImg = itemView.findViewById<CircleImageView>(R.id.potImage)

            if (item.potId == 0) {
                rv_text.text = ""
                potImg.setImageResource(R.drawable.all_button)
            } else {
                rv_text.text = item.potName

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
    }
}
