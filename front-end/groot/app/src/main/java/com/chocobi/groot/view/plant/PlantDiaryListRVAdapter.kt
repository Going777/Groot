package com.chocobi.groot.view.plant

import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.adapter.RecyclerViewAdapter
import com.chocobi.groot.adapter.item.ItemBean
import com.chocobi.groot.adapter.item.ItemViewHolder
import com.chocobi.groot.data.ModelDiary
import com.chocobi.groot.view.community.CommunityDetailFragment
import java.lang.ref.WeakReference

class PlantDiaryListRVAdapter : RecyclerView.Adapter<PlantDiaryListRVAdapter.ViewHolder>() {

    interface RecyclerViewAdapterDelegate {
        fun onLoadMore()
    }

    private var items: MutableList<ModelDiary> = mutableListOf()
    var delegate: RecyclerViewAdapterDelegate? = null
//    private lateinit var postBtnClickListner: ItemClickListener
//    private lateinit var scanBtnClickListner: ItemClickListener
//    private lateinit var detailBtnClickListner: ItemClickListener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlantDiaryListRVAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_plant_diary_list_item, parent, false)
        return ViewHolder(view)
    }

    //    전체 리사이클러뷰의 개수
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PlantDiaryListRVAdapter.ViewHolder, position: Int) {
        holder.item = items[position]
//        holder.delegate = object : PlantDiaryListRVAdapter.ViewHolder.ItemViewHolderDelegate {
//            override fun onItemViewClick(diaryItem: ModelDiary) {
//                Log.d("??", "I click ${diaryItem.id}")
//                val context = holder.itemView.context
//                if (context is FragmentActivity) {
//                    val fragmentManager = context.supportFragmentManager
//                    val communityDetailFragment = CommunityDetailFragment()
//                    fragmentManager.beginTransaction()
//                        .replace(R.id.fl_container, communityDetailFragment)
//                        .addToBackStack(null)
//                        .commit()
//                }
//            }
//        }

        holder.updateView()

        if (position == items.size - 1) {
            delegate?.onLoadMore()
        }

    }

    fun reload(mutableList: MutableList<ModelDiary>) {
        this.items.clear()
        this.items.addAll(mutableList)
        notifyDataSetChanged()
    }

    fun loadMore(mutableList: MutableList<ModelDiary>) {
        this.items.addAll(mutableList)
        notifyItemRangeChanged(this.items.size - mutableList.size + 1, mutableList.size)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//        interface ItemViewHolderDelegate {
//            fun onItemViewClick(diaryItem: ModelDiary) {
//                Log.d("ItemViewHolder", "clicked")
//            }
//        }

        private var view: WeakReference<View> = WeakReference(itemView)

        private lateinit var plantNickname: TextView
        private lateinit var postedTime: TextView
        private lateinit var diaryPhoto: ImageView
        private lateinit var diaryContent: TextView
        private lateinit var detailOption: TextView
        private lateinit var waterBadge: ImageView
        private lateinit var potBadge: ImageView
        private lateinit var bugBadge: ImageView
        private lateinit var sunnnyBadge: ImageView
        private lateinit var pillBadge: ImageView

        //        var delegate: ItemViewHolderDelegate? = null
        lateinit var item: ModelDiary

        init {
            findView()
//            setListener()
        }

        private fun findView() {
            view.get()?.let {
                plantNickname = it.findViewById(R.id.plantNickname)
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

//        private fun setListener() {
//            view.get()?.setOnClickListener {
//                delegate?.onItemViewClick(item)
//                Log.d("??", "aaaaaaaaaaaaaaaaaaaaaaaaaa")
//            }
//        }

        fun updateView() {
            plantNickname.text = item.potName
            postedTime.text = item.createDate
            diaryPhoto.post {
                view.get()?.let {
                    ThreadUtil.startThread {
                        val futureTarget: FutureTarget<Bitmap> = Glide.with(it.context)
                            .asBitmap()
                            .load(item.image)
                            .submit(diaryPhoto.width, diaryPhoto.height)
                        val bitmap = futureTarget.get()
                        ThreadUtil.startUIThread(0) {
                            diaryPhoto.setImageBitmap(bitmap)
                        }
                    }
                }
            }
            diaryContent.text = item.content
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

            if (item.water) {
                waterBadge.visibility = View.VISIBLE
            }
            if (item.pruning) {
                potBadge.visibility = View.VISIBLE
            }
            if (item.bug) {
                bugBadge.visibility = View.VISIBLE
            }
            if (item.sun) {
                sunnnyBadge.visibility = View.VISIBLE
            }
            if (item.nutrients) {
                pillBadge.visibility = View.VISIBLE
            }
        }
    }
}