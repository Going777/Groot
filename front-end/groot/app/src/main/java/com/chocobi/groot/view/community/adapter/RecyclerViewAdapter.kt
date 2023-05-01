package com.chocobi.groot.view.community.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.R
import com.chocobi.groot.adapter.item.ItemBean
import com.chocobi.groot.adapter.item.ItemViewHolder
import com.chocobi.groot.data.ModelDiary
import com.chocobi.groot.view.community.CommunityDetailFragment


class RecyclerViewAdapter: RecyclerView.Adapter<ItemViewHolder>() {

    interface RecyclerViewAdapterDelegate {
        fun onLoadMore()
    }

    private var mutableList: MutableList<ItemBean> = mutableListOf()


    var delegate: RecyclerViewAdapterDelegate? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_community_tab2_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mutableList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.itemBean = mutableList[position]
        holder.delegate = object : ItemViewHolder.ItemViewHolderDelegate {
            override fun onItemViewClick(itemBean: ItemBean) {
                Log.d("??", "I click ${itemBean.title}")
                val context = holder.itemView.context
                if (context is FragmentActivity) {
                    val fragmentManager = context.supportFragmentManager
                    val communityDetailFragment = CommunityDetailFragment()
                    fragmentManager.beginTransaction()
                        .replace(R.id.fl_container, communityDetailFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        holder.updateView()

        if (position == mutableList.size - 1) {
            delegate?.onLoadMore()
        }

    }
    fun reload(mutableList: MutableList<ItemBean>) {
        this.mutableList.clear()
        this.mutableList.addAll(mutableList)
        notifyDataSetChanged()
    }

    fun loadMore(mutableList: MutableList<ItemBean>) {
        this.mutableList.addAll(mutableList)
        notifyItemRangeChanged(this.mutableList.size - mutableList.size + 1, mutableList.size)
    }

}