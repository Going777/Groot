package com.chocobi.groot.adapter.item

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.view.community.model.CommunityArticleListResponse
import org.w3c.dom.Text
import java.lang.ref.WeakReference

class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    interface ItemViewHolderDelegate {
        fun onItemViewClick(communityArticleListResponse: CommunityArticleListResponse) {
            Log.d("ItemViewHolder", "clicked")
        }
    }

    private var view: WeakReference<View> = WeakReference(itemView)

    private lateinit var imageView: ImageView
    private lateinit var textViewTitle: TextView
    private lateinit var textViewNickName: TextView
    private lateinit var textViewTag: TextView
    private lateinit var eyeCnt: TextView
    private lateinit var commentCnt: TextView
    private lateinit var booleanBookmark: TextView
    private lateinit var createTime: TextView
    private lateinit var img: ImageView

    var delegate: ItemViewHolderDelegate? = null
    lateinit var communityArticleListResponse: CommunityArticleListResponse

    init {
        findView()
        setListener()
    }

    private fun findView() {
        view.get()?.let {
            imageView = it.findViewById(R.id.imageView)
            textViewTitle = it.findViewById(R.id.textViewTitle)
            textViewNickName = it.findViewById(R.id.textViewWriter)
            textViewTag = it.findViewById(R.id.textViewTag)
            eyeCnt = it.findViewById(R.id.eyeCnt)
            commentCnt = it.findViewById(R.id.commentCnt)
            booleanBookmark= it.findViewById(R.id.booleanBookmark)
            createTime= it.findViewById(R.id.createTime)
            img = it.findViewById(R.id.imageView)

        }
    }

    private fun setListener() {
        view.get()?.setOnClickListener {
            delegate?.onItemViewClick(communityArticleListResponse)
        }
    }

    fun updateView() {
        textViewTitle.text = communityArticleListResponse.articles.content[0].title
        textViewNickName.text = communityArticleListResponse.articles.content[0].nickName
        textViewTag.text = communityArticleListResponse.articles.content[0].tags.toString()
        eyeCnt.text = communityArticleListResponse.articles.content[0].views.toString()
        commentCnt.text = communityArticleListResponse.articles.content[0].commentCnt.toString()
        booleanBookmark.text = communityArticleListResponse.articles.content[0].bookmark.toString()
        createTime.text = communityArticleListResponse.articles.content[0].createTime.toString()


        imageView.post {

            view.get()?.let {

                ThreadUtil.startThread {
                    val futureTarget: FutureTarget<Bitmap> = Glide.with(it.context)
                        .asBitmap()
                        .load(communityArticleListResponse.articles.content.getOrNull(0)?.img)
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