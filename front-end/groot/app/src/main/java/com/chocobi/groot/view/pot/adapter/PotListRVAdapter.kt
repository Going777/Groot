package com.chocobi.groot.view.pot.adapter

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
import com.chocobi.groot.view.pot.model.Pot
import java.lang.ref.WeakReference

class PotListRVAdapter(val items: List<Pot>) :
    RecyclerView.Adapter<PotListRVAdapter.ViewHolder>() {

    private val TAG = "PotListRVAdapter"
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        Log.d(TAG, "onCreateViewHolder() $items")
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_pot_list_item, parent, false)

        return ViewHolder(view)
    }

    //    아이템 클릭 리스터 인터페이스
//        interface ItemClickListener {
//            fun onPostBtnClick(view: View, position: Int)
//            fun onScanBtnClick(view: View, position: Int)
//            fun onDetailBtnClick(view: View, position: Int)

//        private lateinit var postBtnClickListner: ItemClickListener
//        private lateinit var scanBtnClickListner: ItemClickListener
//        private lateinit var detailBtnClickListner: ItemClickListener
//        fun setItemClickListener(itemClickListener: ItemClickListener) {
//            this.postBtnClickListner = itemClickListener
//            this.scanBtnClickListner = itemClickListener
//            this.detailBtnClickListner = itemClickListener
//        }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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
            rv_text.text = item.potName
            val potImg = itemView.findViewById<ImageView>(R.id.potImage)
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
