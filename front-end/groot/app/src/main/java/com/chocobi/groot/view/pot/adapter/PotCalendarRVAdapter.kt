package com.chocobi.groot.view.pot.adapter



import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.view.pot.model.Diary
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.ref.WeakReference

class PotCalendarRVAdapter(val items:List<Diary>) : RecyclerView.Adapter<PotCalendarRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_pot_calendar_item, parent, false)

        return ViewHolder(view)
    }

    interface ItemClick {
        fun onClick(view:View, position: Int)
    }
    var itemClick : ItemClick? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if ( itemClick != null) {
            holder.itemView.setOnClickListener {
                    v ->
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
        fun bindItems(item:Diary) {
            val potName = itemView.findViewById<TextView>(R.id.potName)
            val potMission = itemView.findViewById<TextView>(R.id.potMission)
            val potImg = itemView.findViewById<CircleImageView>(R.id.potImg)
            potName.text = item.potName
            if (item.code == 0) {
                potMission.text = "물 주기"
            } else {
                potMission.text = "영양제"
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
}