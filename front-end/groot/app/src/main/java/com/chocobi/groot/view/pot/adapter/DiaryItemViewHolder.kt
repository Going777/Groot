package com.chocobi.groot.view.pot.adapter

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.util.ThreadUtil
import com.chocobi.groot.view.pot.model.DiaryListResponse
import java.lang.ref.WeakReference

class DiaryItemViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private var isZero = false
    private val TAG = "DiaryItemViewHolder"
    private var dateList = mutableListOf<String>()

    fun setIsZero(flag: Boolean) {
        isZero = flag

    }

    interface ItemViewHolderDelegate {
        fun onItemViewClick(diaryListResponse: DiaryListResponse) {
            Log.d("ItemViewHolder", "clicked")
        }
    }

    private var view: WeakReference<View> = WeakReference(itemView)

    private lateinit var potNickname: TextView
    private lateinit var postedTime: TextView
    private lateinit var titleDateText: TextView
    private lateinit var diaryPhoto: ImageView
    private lateinit var diaryContent: TextView

    //    private lateinit var detailOption: TextView
    private lateinit var waterBadge: ImageView
    private lateinit var potBadge: ImageView
    private lateinit var bugBadge: ImageView
    private lateinit var sunnnyBadge: ImageView
    private lateinit var pillBadge: ImageView
    private lateinit var spinnerButton: ImageButton

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
            titleDateText = it.findViewById(R.id.titleDateText)
            diaryPhoto = it.findViewById(R.id.diaryPhoto)

            diaryContent = it.findViewById(R.id.diaryContent)
//            detailOption = it.findViewById(R.id.detailOption)

            waterBadge = it.findViewById(R.id.waterBadge)
            potBadge = it.findViewById(R.id.potBadge)
            bugBadge = it.findViewById(R.id.bugBadge)
            sunnnyBadge = it.findViewById(R.id.sunnnyBadge)
            pillBadge = it.findViewById(R.id.pillBadge)

            spinnerButton = it.findViewById(R.id.spinnerButton)
        }
    }

    private fun setListener() {
        view.get()?.setOnClickListener {
            delegate?.onItemViewClick(diaryListResponse)
            Log.d("??", "aaaaaaaaaaaaaaaaaaaaaaaaaa")
        }
    }

    fun updateView() {
        val diary = diaryListResponse.diary.content[0]
        potNickname.text = diary.potName
        val dateText =
            diary.createTime.date.year.toString() + "년 " + diary.createTime.date.month.toString() + "월 " + diary.createTime.date.day.toString() + "일"
//        if (dateList.size == 0) {
//            titleDateText.text = dateText
//            dateList.add(dateText)
//        } else if (dateList.last() != dateText) {
//            titleDateText.text = dateText
//            dateList.add(dateText)
//        } else {
//            titleDateText.visibility = View.GONE
//        }

        Log.d("wowwow", "$isZero")
        if (isZero) {
            if (diary.isUserLast) {
                titleDateText.visibility = View.VISIBLE
                titleDateText.text =
                    dateText
            } else {
                titleDateText.visibility = View.GONE
            }
        } else {
            if (diary.isPotLast) {
                titleDateText.visibility = View.VISIBLE
                titleDateText.text =
                    dateText
            } else {
                titleDateText.visibility = View.GONE
            }
        }
        var hourText = ""
        var minuteText = ""

        if (diary.createTime.time.hour < 10) {
            hourText = "0" + diary.createTime.time.hour.toString()
        } else {
            hourText = diary.createTime.time.hour.toString()
        }

        if (diary.createTime.time.minute < 10) {
            minuteText = "0" + diary.createTime.time.minute.toString()
        } else {
            minuteText = diary.createTime.time.minute.toString()
        }

        postedTime.text = hourText + ":" + minuteText

        if (diary.imgPath != null && diary.imgPath != "") {
            diaryPhoto.post {
                view.get()?.let {
                    ThreadUtil.startThread {
                        val futureTarget: FutureTarget<Bitmap> = Glide.with(it.context)
                            .asBitmap()
                            .load(diary.imgPath)
                            .submit(diaryPhoto.width, diaryPhoto.height)
                        val bitmap = futureTarget.get()
                        ThreadUtil.startUIThread(0) {
                            diaryPhoto.setImageBitmap(bitmap)
                        }
                    }
                }
            }
            diaryPhoto.visibility = View.VISIBLE
        } else {
            diaryPhoto.visibility = View.GONE
        }

        if (!diary.content.isNullOrBlank()) {
            diaryContent.visibility = View.VISIBLE
            diaryContent.text = diary.content
        } else {
            diaryContent.text = ""
        }
//        diaryContent.run {
//            doOnLayout {
//                val lineCount = lineCount
//                val maxLine = 2
//                Log.d(TAG, "$lineCount")
//                if (lineCount > maxLine) {
//                    maxLines = maxLine
//                    ellipsize = TextUtils.TruncateAt.END
//                    detailOption.visibility = View.VISIBLE
//                } else {
//                    detailOption.visibility = View.GONE
//                }
//            }
//        }
//        detailOption.setOnClickListener {
//            when (diaryContent.maxLines) {
//                2 -> {
//                    diaryContent.maxLines = 100
//                    detailOption.text = "간단히 보기"
//                }
//
//                else -> {
//                    diaryContent.maxLines = 2
//                    detailOption.text = "자세히 보기"
//                }
//            }
//        }

        if (diary.water) {
            waterBadge.visibility = View.VISIBLE
        } else {
            waterBadge.visibility = View.GONE
        }
        if (diary.pruning) {
            potBadge.visibility = View.VISIBLE
        } else {
            potBadge.visibility = View.GONE
        }
        if (diary.bug) {
            bugBadge.visibility = View.VISIBLE
        } else {
            bugBadge.visibility = View.GONE

        }
        if (diary.sun) {
            sunnnyBadge.visibility = View.VISIBLE
        } else {
            sunnnyBadge.visibility = View.GONE

        }
        if (diary.nutrients) {
            pillBadge.visibility = View.VISIBLE
        } else {
            pillBadge.visibility = View.GONE

        }
    }


}