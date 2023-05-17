package com.chocobi.groot.view.community.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.view.chat.ChatFragment
import com.chocobi.groot.view.chat.model.ChatUserListResponse
import com.chocobi.groot.view.community.CommunityDetailFragment
import com.chocobi.groot.view.community.model.CommunityShareItemResponse
import java.lang.ref.WeakReference


class ShareItemAdapter(private val recyclerView: RecyclerView, private val fragmentManager: FragmentManager): RecyclerView.Adapter<ShareItemViewHolder>() {

    interface RecyclerViewAdapterDelegate {
        fun onLoadMore()
    }

    private var mutableList: MutableList<CommunityShareItemResponse> = mutableListOf()


    var delegate: RecyclerViewAdapterDelegate? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_community_user_share_item, parent, false)
        return ShareItemViewHolder(view, fragmentManager)
    }

    override fun onBindViewHolder(holder: ShareItemViewHolder, position: Int) {
        val item = mutableList[holder.adapterPosition]
        holder.communityShareItemResponse = item

        holder.delegate = object : ShareItemViewHolder.ShareItemViewHolderDelegate {
            override fun onItemViewClick(communityShareItemResponse: CommunityShareItemResponse) {
                val fragmentManager: FragmentManager =
                    (recyclerView.context as FragmentActivity).supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

                val communityDetailFragment = CommunityDetailFragment()
                val bundle = Bundle()
                bundle.putInt("articleId", communityShareItemResponse.articles[0].articleId)
                Log.d("받아온 데이터", bundle.toString())

                communityDetailFragment.arguments = bundle
                fragmentTransaction.replace(R.id.fl_container, communityDetailFragment).addToBackStack(null).commit()
            }
        }



        holder.itemView.setOnClickListener {
            holder.delegate?.onItemViewClick(item)
        }
        holder.updateView()

        if (position == mutableList.size - 1 && mutableList.size > 1) {
            delegate?.onLoadMore()
        }
    }

    override fun getItemCount(): Int {
        return mutableList.size
    }

    fun reload(mutableList: MutableList<CommunityShareItemResponse>) {
        this.mutableList.clear()
        this.mutableList.addAll(mutableList)
        notifyDataSetChanged()
    }

    fun loadMore(mutableList: MutableList<CommunityShareItemResponse>) {
        this.mutableList.addAll(mutableList)
        notifyItemRangeChanged(this.mutableList.size - mutableList.size, mutableList.size)
    }
}


class ShareItemViewHolder(itemView: View, private val fragmentManager: FragmentManager) : RecyclerView.ViewHolder(itemView) {

    interface ShareItemViewHolderDelegate {
        fun onItemViewClick(communityShareItemResponse: CommunityShareItemResponse)
    }


    private var view: WeakReference<View> = WeakReference(itemView)

    private lateinit var shareItemTitle: TextView
    private lateinit var shareItemImage: ImageView
    private var shareItemArticleId: Int = 0

    var delegate: ShareItemViewHolderDelegate? = null
    lateinit var communityShareItemResponse: CommunityShareItemResponse

    init {
        findView()
        setListener()
    }

    private fun findView() {
        view.get()?.let {
            shareItemTitle = it.findViewById(R.id.shareItemTitle)
            shareItemImage = it.findViewById(R.id.shareItemImage)
        }
    }

    private fun setListener() {
        view.get()?.setOnClickListener {
            delegate?.onItemViewClick(communityShareItemResponse)
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateView() {
        shareItemArticleId = communityShareItemResponse.articles[0].articleId
        shareItemTitle.text = communityShareItemResponse.articles[0].title
        shareItemImage.post {
            view.get()?.let {
                ThreadUtil.startThread {
                    val futureTarget: FutureTarget<Bitmap> = Glide.with(it.context)
                        .asBitmap()
                        .load(communityShareItemResponse.articles.getOrNull(0)?.img)
                        .submit(shareItemImage.width, shareItemImage.height)

                    val bitmap = futureTarget.get()

                    ThreadUtil.startUIThread(0) {
                        shareItemImage.setImageBitmap(bitmap)
                    }
                }
            }
        }

    }
}