package com.chocobi.groot.view.community

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.chocobi.groot.R
import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.community.adapter.ArticleTagAdapter
import com.chocobi.groot.view.community.model.Article
import com.chocobi.groot.view.community.model.BookmarkResponse
import com.chocobi.groot.view.community.model.CommunityArticleDetailResponse
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityDetailFragment : Fragment() {
    private lateinit var bookmarkButton: ImageButton
    private val TAG = "CommunityDetailFragment"
    private lateinit var postCommentBtn: Button
    private lateinit var postCommentInput: EditText
    private lateinit var recyclerView: RecyclerView
    private var tagList: List<String> = emptyList()


//    private var commentList = arrayListOf<CommunityCommentResponse>()

    val commentFragment = CommunityCommentFragment()

    private val imagesList:MutableList<String?> = arrayListOf()

    private lateinit var getData: CommunityArticleDetailResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_community_detail, container, false)
        val articleId = arguments?.getInt("articleId")
        Log.d("CommunityDetailFragmentArticleId", articleId.toString())

        val args = Bundle()
        if (articleId != null) {
            args.putInt("articleId", articleId)

        }
        val communityCommentFragment = CommunityCommentFragment()
        val communityUserShareFragment = CommunityUserShareFragment()

        Log.d("CommunityCommentFragment", "$args")
        communityCommentFragment.arguments = args
        communityUserShareFragment.arguments = args
        childFragmentManager.beginTransaction()
            .add(R.id.communityUserShareFragment, communityUserShareFragment)
            .commit()
        childFragmentManager.beginTransaction()
            .add(R.id.communityCommentFragment, communityCommentFragment)
            .commit()


        var detailCategory = view.findViewById<TextView>(R.id.detailCategory)
        var detailTitle = view.findViewById<TextView>(R.id.detailTitle)
        var detailNickName = view.findViewById<TextView>(R.id.detailNickName)
        var detailViews = view.findViewById<TextView>(R.id.detailViews)
        var detailCreateTime = view.findViewById<TextView>(R.id.detailCreateTime)
        var bookmarkLine = view.findViewById<ImageButton>(R.id.bookmarkLine)
        var detailContent = view.findViewById<TextView>(R.id.detailContent)
        var detailCommentCnt = view.findViewById<TextView>(R.id.detailCommentCnt)
        var bookmarkStatus = false
        var postCommentBtn = view.findViewById<Button>(R.id.postCommentBtn)
        var postCommentInput = view.findViewById<EditText>(R.id.postCommentInput)
        var sharePosition = view.findViewById<TextView>(R.id.sharePosition)

        var sharePositionSection = view.findViewById<LinearLayoutCompat>(R.id.sharePositionSection)
        var shareStateSection = view.findViewById<LinearLayoutCompat>(R.id.shareStateSection)
        var shareSection = view.findViewById<LinearLayoutCompat>(R.id.shareSection)
        var commentSection = view.findViewById<LinearLayoutCompat>(R.id.commentSection)
        var commentInputSection = view.findViewById<CardView>(R.id.commentInputSection)


        postCommentBtn.setOnClickListener {

            var content =  postCommentInput?.text.toString()
            if (articleId != null) {
                postComment(articleId, content)
            }

            Log.d("CommunityDetailFragmentArticleId", articleId.toString())
            Log.d("CommunityDetailFragmentArticleId", content.toString())


            // 입력창 리셋 및 키보드 닫기
            postCommentInput?.setText("")
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                view?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )

        }

//                retrofit 객체 만들기
        val retrofit = RetrofitClient.getClient()!!

        val communityArticleDetailService = retrofit.create(CommunityArticleDetailService::class.java)

        communityArticleDetailService.requestCommunityArticleDetail(articleId!!).enqueue(object :
            Callback<CommunityArticleDetailResponse> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<CommunityArticleDetailResponse>, response: Response<CommunityArticleDetailResponse>) {
                    if (response.code() == 200) {
                        Log.d(TAG, "성공")
                        val responseData =  response.body()?.article
                        getData = response.body()!!
                        Log.d(TAG, "$responseData")

                        val article = getData.article
                        val articleDetailData = CommunityArticleDetailResponse(
                            article = Article(
                                category = article.category,
                                imgs = article.imgs,
                                userPK = article.userPK,
                                nickName = article.nickName,
                                title = article.title,
                                tags = article.tags,
                                views = article.views,
                                commentCnt = article.commentCnt,
                                bookmark = article.bookmark,
                                shareRegion = article.shareRegion,
                                content = article.content,
                                shareStatus = article.shareStatus,
                                createTime = article.createTime,
                                updateTime = article.updateTime
                            )
                        )
                        detailCategory.text = articleDetailData.article.category
                        detailTitle.text = articleDetailData.article.title
                        detailNickName.text = articleDetailData.article.nickName
                        detailViews.text = articleDetailData.article.views.toString()
                        val koreahour = articleDetailData.article.createTime.time.hour + 9
                        detailCreateTime.text = articleDetailData.article.createTime.date.year.toString() + '.'+ articleDetailData.article.createTime.date.month.toString() + '.' + articleDetailData.article.createTime.date.day.toString() + ' ' + koreahour + ':'+ articleDetailData.article.createTime.time.minute.toString()
                        detailContent.text = articleDetailData.article.content
                        tagList = articleDetailData.article.tags
                        sharePosition.text = articleDetailData.article.shareRegion
                        detailCommentCnt.text = "댓글 (" + articleDetailData.article.commentCnt.toString() + ")"

                        Log.d("CommunityDetailFragmentImgs", articleDetailData.article.imgs.toString())
                        Log.d("CommunityDetailFragmentImgs", articleDetailData.article.imgs?.size.toString())

                        if (articleDetailData.article.imgs!!.isNotEmpty()){
                            for (i in 1..articleDetailData.article.imgs.size) {
                                imagesList.add(articleDetailData.article.imgs[i-1])
                            }
                        }


                        // 카테고리별 섹션 구별
                        if (detailCategory.text == "나눔") {
                            if (articleDetailData.article.shareStatus == true) {
                                shareStateSection.visibility = View.VISIBLE
                            } else if (articleDetailData.article.shareStatus == false ){
                                shareStateSection.visibility = View.GONE
                            }
                            sharePositionSection.visibility = View.VISIBLE
                            shareSection.visibility = View.VISIBLE
                            commentSection.visibility = View.GONE
                            commentInputSection.visibility = View.GONE
                        } else {
                            shareStateSection.visibility = View.GONE
                            sharePositionSection.visibility = View.GONE
                            shareSection.visibility = View.GONE
                            commentSection.visibility = View.VISIBLE
                            commentInputSection.visibility = View.VISIBLE
                        }


                        bookmarkStatus = articleDetailData.article.bookmark
                        // 북마크
                        bookmarkButton = view.findViewById(R.id.bookmarkLine)
                        if (bookmarkStatus == true) {
                            bookmarkButton.setImageResource(R.drawable.ic_bookmark_fill)
                        } else {
                            bookmarkButton.setImageResource(R.drawable.ic_bookmark)
                        }


                        Log.d( "CommunityDetailFragment", articleDetailData.toString())

                        // 태그
                        recyclerView = view.findViewById(R.id.tagList)
                        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

                        // Adapter 설정
                        val tagAdapter = ArticleTagAdapter(tagList)
                        recyclerView.adapter = tagAdapter

                    } else {
                        Log.d(TAG, "실패1")
                        Log.d(TAG, response.toString())

                    }
                }
                override fun onFailure(call: Call<CommunityArticleDetailResponse>, t: Throwable) {
                    Log.d(TAG, "실패2")
                }
            }
        )


//        북마크 수정 api

        val userPK = UserData.getUserPK()
        val communityBookmarkService = retrofit.create(CommunityBookmarkService::class.java)
        bookmarkButton = view.findViewById(R.id.bookmarkLine)
        bookmarkButton.setOnClickListener {
            communityBookmarkService.requestCommunityBookmark(BookmarkRequest(articleId, userPK, bookmarkStatus)).enqueue(object :
                Callback<BookmarkResponse> {
                override fun onResponse(
                    call: Call<BookmarkResponse>,
                    response: Response<BookmarkResponse>
                ) {
                    if (response.code() == 200) {
                        Log.d(TAG, "북마크상태변경 성공")
                        bookmarkStatus = !bookmarkStatus
                        bookmarkButton.setImageResource(
                            if (bookmarkStatus) R.drawable.ic_bookmark_fill
                            else R.drawable.ic_bookmark
                        )
                    } else {
                        Log.d(TAG, "북마크상태변경 실패")
                    }
                }

                override fun onFailure(call: Call<BookmarkResponse>, t: Throwable) {
                    Log.d(TAG, "북마크상태변경 실패")
                }
            })
        }


        val spinner: Spinner = view.findViewById(R.id.spinner)
        val spinnerButton: ImageButton = view.findViewById(R.id.spinnerButton)

        val options = arrayOf("  수정  ", "  삭제  ")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        try {
            val method = Spinner::class.java.getDeclaredMethod("setSpinnerButton", ImageButton::class.java)
            method.invoke(spinner, spinnerButton)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedOption = options[position]
//                Toast.makeText(requireContext(), selectedOption, Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무것도 선택하지 않은 경우 처리
            }
        }

        spinnerButton.setOnClickListener {
            spinner.performClick()
        }


        Log.d("CommunityDetailFragmentImgs", imagesList.toString())

        Log.d("CommunityDetailFragment_childFragmentManager", CommunityCommentFragment().toString())

        val viewPager: ViewPager2 = view.findViewById(R.id.carousel_pager)
        Log.d("CommunityDetailFragmentCount중", imagesList.size.toString())

        val imageAdapter = CommunityTabAdapter(this)
        viewPager.adapter = imageAdapter
        val tabLayout: TabLayout = view.findViewById(R.id.carousel_layout)

        var tabList = listOf<String>()

        when (imagesList.size) {
            0 -> {tabList = listOf<String>()}
            1 -> {tabList = listOf<String>("")}
            2 -> {tabList = listOf<String>("", "")}
            3 -> {tabList = listOf<String>("", "", "")}
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabList[position]

        }.attach()



        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private var nowTab: Int = 0
    private inner class CommunityTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int {
            return imagesList.size
        }

        override fun createFragment(position: Int): Fragment {
            Log.d("CommunityDetailFragmentCount중", imagesList.size.toString())
            when (imagesList.size) {
                1 -> {
                    nowTab = position
                    return when (position) {
                        0 -> CommunityDetailImg1Fragment()
                        else -> CommunityTab1Fragment()
                    }
                }
                2 -> {
                    nowTab = position
                    return when (position) {
                        0 -> CommunityDetailImg1Fragment()
                        1 -> CommunityDetailImg2Fragment()
                        else -> CommunityTab1Fragment()
                    }
                }
                3 -> {
                    nowTab = position
                    return when (position) {
                        0 -> CommunityDetailImg1Fragment()
                        1 -> CommunityDetailImg2Fragment()
                        2 -> CommunityDetailImg3Fragment()
                        else -> CommunityTab1Fragment()
                    }
                }
                else -> {
                    return CommunityTab1Fragment()
                }
            }
        }
    }

    private fun postComment(
        articleId: Int,
        content: String,
    ) {
        val retrofit = RetrofitClient.getClient()!!
        val communityCommentPostService = retrofit.create(CommunityCommentPostService::class.java)


        communityCommentPostService.requestCommentPost(CommentPostRequest(articleId, content))
            .enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    val body = response.body()
                    Log.d("CommunityPostFragmentBody", "$body")
                    Log.d("CommunityDetailFragmentArticleId", response.code().toString())
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }

}