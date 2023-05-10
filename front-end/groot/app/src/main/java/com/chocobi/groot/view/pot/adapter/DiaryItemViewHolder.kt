package com.chocobi.groot.view.pot.adapter

import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.view.pot.model.DiaryListResponse
import java.lang.ref.WeakReference

class DiaryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    interface ItemViewHolderDelegate {
        fun onItemViewClick(diaryListResponse: DiaryListResponse) {
            Log.d("ItemViewHolder", "clicked")
        }
    }

    private var view: WeakReference<View> = WeakReference(itemView)

    private lateinit var potNickname: TextView
    private lateinit var postedTime: TextView
    private lateinit var diaryPhoto: ImageView
    private lateinit var diaryContent: TextView
    private lateinit var detailOption: TextView
    private lateinit var waterBadge: ImageView
    private lateinit var potBadge: ImageView
    private lateinit var bugBadge: ImageView
    private lateinit var sunnnyBadge: ImageView
    private lateinit var pillBadge: ImageView

    var delegate: ItemViewHolderDelegate? = null
    lateinit var diaryListResponse: DiaryListResponse

    init {
        findView()
        setListener()
    }

    private fun findView() {
        view.get()?.let {
            potNickname = it.findViewById(R.id.potNickname)
            postedTime = it.findViewById(R.id.postedTime)
            diaryPhoto = it.findViewById(R.id.diaryPhoto)

            diaryContent = it.findViewById(R.id.diaryContent)
            detailOption = it.findViewById(R.id.detailOption)

            waterBadge = it.findViewById(R.id.waterBadge)
            potBadge = it.findViewById(R.id.potBadge)
            bugBadge = it.findViewById(R.id.bugBadge)
            sunnnyBadge = it.findViewById(R.id.sunnnyBadge)
            pillBadge = it.findViewById(R.id.pillBadge)
        }
    }

    private fun setListener() {
        view.get()?.setOnClickListener {
            delegate?.onItemViewClick(diaryListResponse)
            Log.d("??", "aaaaaaaaaaaaaaaaaaaaaaaaaa")
        }
    }

    fun updateView() {
        potNickname.text = diaryListResponse.diary.content[0].potName
        postedTime.text =
            diaryListResponse.diary.content[0].createTime.date.year.toString() + "-" + diaryListResponse.diary.content[0].createTime.date.month.toString()
        diaryPhoto.post {
            view.get()?.let {
                ThreadUtil.startThread {
                    val futureTarget: FutureTarget<Bitmap> = Glide.with(it.context)
                        .asBitmap()
                        .load(diaryListResponse.diary.content[0].imgPath)
                        .submit(diaryPhoto.width, diaryPhoto.height)
                    val bitmap = futureTarget.get()
                    ThreadUtil.startUIThread(0) {
                        diaryPhoto.setImageBitmap(bitmap)
                    }
                }
            }
        }
        diaryContent.text = diaryListResponse.diary.content[0].content
        diaryContent.run {
            doOnLayout {
                val lineCount = lineCount
                val maxLine = 2
                if (lineCount > maxLine) {
                    maxLines = maxLine
                    ellipsize = TextUtils.TruncateAt.END
                    detailOption.visibility = View.VISIBLE
                } else {
                    detailOption.visibility = View.GONE
                }
            }
        }
        detailOption.setOnClickListener {
            when (diaryContent.maxLines) {
                2 -> {
                    diaryContent.maxLines = 100
                    detailOption.text = "간단히 보기"
                }

                else -> {
                    diaryContent.maxLines = 2
                    detailOption.text = "자세히 보기"
                }
            }
        }

        if (diaryListResponse.diary.content[0].water) {
            waterBadge.visibility = View.VISIBLE
        }
        if (diaryListResponse.diary.content[0].pruning) {
            potBadge.visibility = View.VISIBLE
        }
        if (diaryListResponse.diary.content[0].bug) {
            bugBadge.visibility = View.VISIBLE
        }
        if (diaryListResponse.diary.content[0].sun) {
            sunnnyBadge.visibility = View.VISIBLE
        }
        if (diaryListResponse.diary.content[0].nutrients) {
            pillBadge.visibility = View.VISIBLE
        }
    }


}