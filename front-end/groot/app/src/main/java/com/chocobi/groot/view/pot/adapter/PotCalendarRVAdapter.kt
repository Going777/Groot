package com.chocobi.groot.view.pot.adapter


import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.view.pot.model.Diary
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.ref.WeakReference

class PotCalendarRVAdapter(val items: List<Diary>) :
    RecyclerView.Adapter<PotCalendarRVAdapter.ViewHolder>() {

    private val TAG = "PotCalendarRVAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_pot_calendar_item, parent, false)

        return ViewHolder(view)
    }

    interface ItemClickListener {
        fun onCheckClick(view: View, position: Int)
        fun onPotImgClick(view: View, position: Int)
    }

    private lateinit var checkClickListner: ItemClickListener
    private lateinit var potImgClickListner: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.checkClickListner = itemClickListener
        this.potImgClickListner = itemClickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindItems(items[position])

        val checkBox = holder.itemView.findViewById<CheckBox>(R.id.checkbox)
        val potImg = holder.itemView.findViewById<ImageView>(R.id.potImg)
        checkBox.setOnClickListener {
            checkClickListner.onCheckClick(it, position)
            Log.d(
                TAG,
                "potId:${items[position].potId} / code:${items[position].code} / isChecked:${checkBox.isChecked}"
            )
            if (checkBox.isChecked) {
//                다이어리 쓰기
            } else {
//                다이어리 삭제
            }
        }
        potImg.setOnClickListener {
            potImgClickListner.onPotImgClick(it, position)
        }
    }

    //    전체 리사이클러뷰의 개수
    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var view: WeakReference<View> = WeakReference(itemView)
        fun bindItems(item: Diary) {
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