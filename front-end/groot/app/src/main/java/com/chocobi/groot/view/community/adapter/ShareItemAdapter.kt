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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.view.community.model.CommunityCommentResponse
import com.chocobi.groot.view.community.model.CommunityShareItemResponse
import java.lang.ref.WeakReference


class ShareItemAdapter(private val recyclerView: RecyclerView): RecyclerView.Adapter<ShareItemViewHolder>() {

    interface RecyclerViewAdapterDelegate {
        fun onLoadMore()
    }

    private var mutableList: MutableList<CommunityShareItemResponse> = mutableListOf()


    var delegate: RecyclerViewAdapterDelegate? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_community_user_share_item, parent, false)
        return ShareItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShareItemViewHolder, position: Int) {
        holder.communityShareItemResponse = mutableList[position]

        holder.delegate = object : ShareItemViewHolder.ShareItemViewHolderDelegate {

        }

        holder.updateView()

        if (position == mutableList.size - 1) {
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


class ShareItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    interface ShareItemViewHolderDelegate {
        fun onItemViewClick(communityShareItemResponse: CommunityShareItemResponse) {
            Log.d("ShareItemViewHolder", "clicked")
        }
    }

    private var view: WeakReference<View> = WeakReference(itemView)

    private lateinit var shareItemTitle: TextView
    private lateinit var shareItemImage: ImageView

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