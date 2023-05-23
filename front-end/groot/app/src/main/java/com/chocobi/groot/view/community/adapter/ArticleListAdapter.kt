package com.chocobi.groot.view.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.view.community.model.CommunityArticleListResponse
import com.chocobi.groot.adapter.item.ItemViewHolder


class RecyclerViewAdapter(private val mActivity: MainActivity): RecyclerView.Adapter<ItemViewHolder>() {

    interface RecyclerViewAdapterDelegate {
        fun onLoadMore()
    }

    private var mutableList: MutableList<CommunityArticleListResponse> = mutableListOf()


    var delegate: RecyclerViewAdapterDelegate? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_community_tab2_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mutableList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.communityArticleListResponse = mutableList[position]

        holder.delegate = object : ItemViewHolder.ItemViewHolderDelegate {
            override fun onItemViewClick(communityArticleListResponse: CommunityArticleListResponse) {
                mActivity.setCommunityArticleId(communityArticleListResponse.articles.content[0].articleId)
                mActivity.changeFragment("community_detail")
//                val context = holder.itemView.context
//                if (context is FragmentActivity) {
//                    val fragmentManager = context.supportFragmentManager
//                    val communityDetailFragment = CommunityDetailFragment()
//
//                    // articleId 값을 CommunityDetailFragment에 전달하기 위해 인수(bundle)를 설정합니다.
//                    val args = Bundle()
//                    args.putInt("articleId", communityArticleListResponse.articles.content[0].articleId)
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
    fun reload(mutableList: MutableList<CommunityArticleListResponse>) {
        this.mutableList.clear()
        this.mutableList.addAll(mutableList)
        notifyDataSetChanged()
    }

    fun loadMore(mutableList: MutableList<CommunityArticleListResponse>) {
        this.mutableList.addAll(mutableList)
        notifyItemRangeChanged(this.mutableList.size - mutableList.size, mutableList.size)
    }



}

