package com.chocobi.groot.adapter.item

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import java.lang.ref.WeakReference

class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    interface ItemViewHolderDelegate {
        fun onItemViewClick(itemBean: ItemBean)
    }

    private var view: WeakReference<View> = WeakReference(itemView)

    private lateinit var imageView: ImageView
    private lateinit var textViewTitle: TextView
    private lateinit var textViewContent: TextView

    var delegate: ItemViewHolderDelegate? = null
    lateinit var itemBean: ItemBean

    init {
        findView()
        setListener()
    }

    private fun findView() {
        view.get()?.let {
            imageView = it.findViewById(R.id.imageView)
            textViewTitle = it.findViewById(R.id.textViewTitle)
            textViewContent = it.findViewById(R.id.textViewContent)
        }
    }

    private fun setListener() {
        view.get()?.setOnClickListener {
            delegate?.onItemViewClick(itemBean)
        }
    }

    fun updateView() {
        textViewTitle.text = itemBean.title
        textViewContent.text = itemBean.content

        imageView.post {

//            val imageUrl = "https://cdn.wallpapersafari.com/15/87/kp4wAJ.jpg"

            view.get()?.let {

                ThreadUtil.startThread {
                    val futureTarget: FutureTarget<Bitmap> = Glide.with(it.context)
                        .asBitmap()
                        .load(itemBean.imageUrl)
                        .submit(imageView.width, imageView.height)

                    val bitmap = futureTarget.get()

                    ThreadUtil.startUIThread(0) {
                        imageView.setImageBitmap(bitmap)
                    }
                }



            }
        }

    }

}