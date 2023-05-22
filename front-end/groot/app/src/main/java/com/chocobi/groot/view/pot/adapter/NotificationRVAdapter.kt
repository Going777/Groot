package com.chocobi.groot.view.pot.adapter

import android.content.Context
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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.view.pot.model.DateTime
import com.chocobi.groot.view.pot.model.NotiMessage
import com.chocobi.groot.view.pot.model.Pot
import com.chocobi.groot.view.sensor.SensorActivity
import com.chocobi.groot.view.user.adapter.UserTab1RVAdapter
import java.lang.ref.WeakReference

class NotificationRVAdapter(val context: Context, val items: List<NotiMessage>) :
    RecyclerView.Adapter<NotificationRVAdapter.ViewHolder>() {
    private val TAG = "NotificationRVAdapter"
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        Log.d(TAG, "onCreateViewHolder() $items")
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_notification_item, parent, false)

        return ViewHolder(view)
    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: UserTab1RVAdapter.ItemClick? = null


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

        fun bindItems(item: NotiMessage) {
            val notiContentText = itemView.findViewById<TextView>(R.id.notiContentText)
            val notiTimeText = itemView.findViewById<TextView>(R.id.notiTimeText)
            val notiImg = itemView.findViewById<ImageButton>(R.id.notiImg)
            notiContentText.text = item.content
            notiTimeText.text = changeDateFormat(item.createDate)
            if (item.isRead) {
                notiContentText.setTextColor(ContextCompat.getColor(context, R.color.grey))
            } else {
                notiContentText.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            when (item.page) {
                "article" -> {notiImg.setImageResource(R.drawable.ic_article)}
                "main" -> {notiImg.setImageResource(R.drawable.ic_pot)}
                else -> {notiImg.setImageResource(R.drawable.ic_chat)}
            }
        }
    }

    private fun changeDateFormat(date: DateTime): String {
        return date.date.year.toString() + "/" + date.date.month.toString() + "/" + date.date.day.toString() + " " +
                date.time.hour.toString() + ":" + date.time.minute.toString()
    }
}