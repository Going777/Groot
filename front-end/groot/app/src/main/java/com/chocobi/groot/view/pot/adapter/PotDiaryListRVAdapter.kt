package com.chocobi.groot.view.pot.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.R
import com.chocobi.groot.view.pot.model.DiaryListResponse

class PotDiaryListRVAdapter : RecyclerView.Adapter<DiaryItemViewHolder>() {

    interface RecyclerViewAdapterDelegate {
        fun onLoadMore()
    }

    private var mutableList: MutableList<DiaryListResponse> = mutableListOf()

    var delegate: RecyclerViewAdapterDelegate? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DiaryItemViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_pot_diary_list_item, parent, false)
        return DiaryItemViewHolder(view)
    }

    //    전체 리사이클러뷰의 개수
    override fun getItemCount(): Int {
        return mutableList.size
    }

    override fun onBindViewHolder(holder: DiaryItemViewHolder, position: Int) {
        holder.diaryListResponse = mutableList[position]


        holder.delegate = object : DiaryItemViewHolder.ItemViewHolderDelegate {
            override fun onItemViewClick(diaryListResponse: DiaryListResponse) {
                val context = holder.itemView.context
//                if (context is FragmentActivity) {
//                    val fragmentManager = context.supportFragmentManager
//                    val communityDetailFragment = CommunityDetailFragment()
//
//                    // articleId 값을 CommunityDetailFragment에 전달하기 위해 인수(bundle)를 설정합니다.
//                    val args = Bundle()
//                    args.putInt("articleId", diaryListResponse.articles.content[0].articleId)
//                    communityDetailFragment.arguments = args
//                    Log.d("CommunityDetailFragmentArticleId", communityDetailFragment.arguments.toString())
//
//                    fragmentManager.beginTransaction()
//                        .replace(R.id.fl_container, communityDetailFragment)
//                        .addToBackStack(null)
//                        .commit()
//                }
            }

        }


        holder.updateView()

        if (position == mutableList.size - 1) {
            delegate?.onLoadMore()
        }

    }

    fun reload(mutableList: MutableList<DiaryListResponse>) {
        this.mutableList.clear()
        this.mutableList.addAll(mutableList)
        notifyDataSetChanged()
    }

    fun loadMore(mutableList: MutableList<DiaryListResponse>) {
        this.mutableList.addAll(mutableList)
        notifyItemRangeChanged(this.mutableList.size - mutableList.size + 1, mutableList.size)
    }

}