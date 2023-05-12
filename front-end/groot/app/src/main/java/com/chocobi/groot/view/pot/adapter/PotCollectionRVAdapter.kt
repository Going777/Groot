package com.chocobi.groot.view.pot.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.view.pot.model.Pot
import com.chocobi.groot.view.sensor.SensorActivity
import java.lang.ref.WeakReference

class PotCollectionRVAdapter(val items: List<Pot>) :
    RecyclerView.Adapter<PotCollectionRVAdapter.ViewHolder>() {
    private val TAG = "PotCollectionRVAdapter"
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        Log.d(TAG, "onCreateViewHolder() $items")
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_pot_collection_item, parent, false)

        return ViewHolder(view)
    }

    //    아이템 클릭 리스터 인터페이스
    interface ItemClickListener {
        fun onPostBtnClick(view: View, position: Int)
        fun onScanBtnClick(view: View, position: Int)
        fun onDetailBtnClick(view: View, position: Int)
    }

    private lateinit var postBtnClickListner: ItemClickListener
    private lateinit var scanBtnClickListner: ItemClickListener
    private lateinit var detailBtnClickListner: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.postBtnClickListner = itemClickListener
        this.scanBtnClickListner = itemClickListener
        this.detailBtnClickListner = itemClickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(items[position])

        val postBtn = holder.itemView.findViewById<ImageButton>(R.id.potPostDiaryBtn)
        val scanBtn = holder.itemView.findViewById<ImageButton>(R.id.potScanBtn)
        val detailBtn = holder.itemView.findViewById<Button>(R.id.potDetailBtn)

        postBtn.setOnClickListener {
            postBtnClickListner.onPostBtnClick(it, position)
        }

        scanBtn.setOnClickListener {
            val safeAlertDialog = AlertDialog.Builder(it.context)
            safeAlertDialog.setMessage("AR 모드를 사용할 때는 주변이 안전한지 먼저 확인하세요.\n어린이의 경우 보호자와 함께 사용해주세요.")
            safeAlertDialog.setPositiveButton("OK") { dialog, which ->
                scanBtnClickListner.onScanBtnClick(it, position)
            }
            safeAlertDialog.create().show()

        }

        detailBtn.setOnClickListener {
            detailBtnClickListner.onDetailBtnClick(it, position)
        }
    }

    //    전체 리사이클러뷰의 개수
    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var view: WeakReference<View> = WeakReference(itemView)

        fun bindItems(item: Pot) {
            val potNameText = itemView.findViewById<TextView>(R.id.potName)
            val characterImg = itemView.findViewById<ImageView>(R.id.characterImage)
            val potImg = itemView.findViewById<ImageView>(R.id.potImage)
            val potMeetDayText = itemView.findViewById<TextView>(R.id.potMeetDay)
            potNameText.text = item.potName
            potMeetDayText.text = item.dates.toString()

            characterImg.post {
                view.get()?.let {
                    ThreadUtil.startThread {
                        val futureTarget: FutureTarget<Bitmap> = Glide.with(it.context)
                            .asBitmap()
                            .load(item.characterPNGPath)
                            .submit(characterImg.width, characterImg.height)

                        val bitmap = futureTarget.get()

                        ThreadUtil.startUIThread(0) {
                            characterImg.setImageBitmap(bitmap)
                        }
                    }
                }
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