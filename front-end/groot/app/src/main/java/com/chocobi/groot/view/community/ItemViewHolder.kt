package com.chocobi.groot.adapter.item

import android.annotation.SuppressLint
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
    private lateinit var createTime: TextView
    private lateinit var bookmarkLine: ImageView

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
            createTime= it.findViewById(R.id.createTime)
            bookmarkLine = it.findViewById(R.id.bookmarkLine)
        }
    }

    private fun setListener() {
        view.get()?.setOnClickListener {
            delegate?.onItemViewClick(communityArticleListResponse)
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateView() {
        textViewTitle.text = communityArticleListResponse.articles.content[0].title
        textViewNickName.text = communityArticleListResponse.articles.content[0].nickName
        textViewTag.text = communityArticleListResponse.articles.content[0].tags.toString()
        eyeCnt.text = communityArticleListResponse.articles.content[0].views.toString()
        commentCnt.text = communityArticleListResponse.articles.content[0].commentCnt.toString()
        val koreahour = communityArticleListResponse.articles.content[0].createTime.time.hour + 9
        createTime.text = communityArticleListResponse.articles.content[0].createTime.date.year.toString() + '.'+ communityArticleListResponse.articles.content[0].createTime.date.month.toString() + '.' + communityArticleListResponse.articles.content[0].createTime.date.day.toString() + ' ' + koreahour + ':'+ communityArticleListResponse.articles.content[0].createTime.time.minute.toString()

        // 북마크 여부에 따라 아이콘 변경
        if (communityArticleListResponse.articles.content[0].bookmark) {
            bookmarkLine.setImageResource(R.drawable.ic_bookmark_fill)
        } else {
            bookmarkLine.setImageResource(R.drawable.ic_bookmark)
        }
        bookmarkLine.setColorFilter(itemView.context.getColor(android.R.color.darker_gray))

//        이미지 없으면 공간차지 X
        if (communityArticleListResponse.articles.content[0].img.isNullOrEmpty()) {
            imageView.visibility = View.GONE
        } else {
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
}
