package com.chocobi.groot.view.community

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.chocobi.groot.R
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.community.adapter.CommentAdapter
import com.chocobi.groot.view.community.model.Article
import com.chocobi.groot.view.community.model.BookmarkResponse
import com.chocobi.groot.view.community.model.CommunityArticleDetailResponse
import com.chocobi.groot.view.community.model.CommunityCommentResponse
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityDetailFragment : Fragment() {
    private lateinit var bookmarkButton: ImageButton

    private var commentList = arrayListOf<CommunityCommentResponse>(
//        CommunityCommentResponse("sample_plant_image", "박세희1", "4", "댓글123"),
//        CommunityCommentResponse("sample_plant_image", "박세희2", "4", "댓글123"),
//        CommunityCommentResponse("sample_plant_image", "박세희3", "4", "댓글123"),
//        CommunityCommentResponse("sample_plant_image", "박세희4", "4", "댓글123"),
    )

    private lateinit var getData: CommunityArticleDetailResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_community_detail, container, false)
        val articleId = arguments?.getInt("articleId")
        Log.d("CommunityDetailFragmentArticleId", articleId.toString())

        var detailCategory = view.findViewById<TextView>(R.id.detailCategory)
        var detailTitle = view.findViewById<TextView>(R.id.detailTitle)
        var detailNickName = view.findViewById<TextView>(R.id.detailNickName)
        var detailViews = view.findViewById<TextView>(R.id.detailViews)
        var detailCreateTime = view.findViewById<TextView>(R.id.detailCreateTime)
        var bookmarkLine = view.findViewById<ImageButton>(R.id.bookmarkLine)
        var detailContent = view.findViewById<TextView>(R.id.detailContent)
        var detailTag = view.findViewById<TextView>(R.id.detailTag)
        var detailCommentCnt = view.findViewById<TextView>(R.id.detailCommentCnt)
        var bookmarkStatus = false

//                retrofit 객체 만들기
        val retrofit = RetrofitClient.getClient()!!

        val communityArticleDetailService = retrofit.create(CommunityArticleDetailService::class.java)

        communityArticleDetailService.requestCommunityArticleDetail(articleId!!).enqueue(object :
            Callback<CommunityArticleDetailResponse> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<CommunityArticleDetailResponse>, response: Response<CommunityArticleDetailResponse>) {
                    if (response.code() == 200) {
                        Log.d("CommunityDetailFragment", "성공")
                        val responseData =  response.body()?.article
                        getData = response.body()!!
                        Log.d("CommunityDetailFragment", "$responseData")

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
                        detailTag.text = articleDetailData.article.tags.toString()
                        detailCommentCnt.text = "댓글 (" + articleDetailData.article.commentCnt.toString() + ")"


                        bookmarkStatus = articleDetailData.article.bookmark
                        // 북마크
                        bookmarkButton = view.findViewById(R.id.bookmarkLine)
                        if (bookmarkStatus == true) {
                            bookmarkButton.setImageResource(R.drawable.ic_bookmark_fill)
                        } else {
                            bookmarkButton.setImageResource(R.drawable.ic_bookmark)
                        }


                        Log.d( "CommunityDetailFragment", articleDetailData.toString())

                    } else {
                        Log.d("CommunityDetailFragment", "실패1")
                    }
                }
                override fun onFailure(call: Call<CommunityArticleDetailResponse>, t: Throwable) {
                    Log.d("CommunityDetailFragment", "실패2")
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
                        Log.d("CommunityDetailFragment", "북마크상태변경 성공")
                        bookmarkStatus = !bookmarkStatus
                        bookmarkButton.setImageResource(
                            if (bookmarkStatus) R.drawable.ic_bookmark_fill
                            else R.drawable.ic_bookmark
                        )
                    } else {
                        Log.d("CommunityDetailFragment", "북마크상태변경 실패")
                    }
                }

                override fun onFailure(call: Call<BookmarkResponse>, t: Throwable) {
                    Log.d("CommunityDetailFragment", "북마크상태변경 실패")
                }
            })
        }


//        댓글 목록 api
        val communityCommentService = retrofit.create(CommunityCommentService::class.java)

//// 2. RecyclerView의 레이아웃 매니저 설정
//        val commentRecycleView = view.findViewById<RecyclerView>(R.id.commentRecycleView)
//        val layoutManager = LinearLayoutManager(context)
//        commentRecycleView.layoutManager = layoutManager
//
//// 3. RecyclerView 어댑터가 데이터를 올바르게 처리하는지 확인
//        val commentAdapter = CommentAdapter(commentList)
//        commentRecycleView.adapter = commentAdapter


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



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = view.findViewById(R.id.carousel_pager)
        val adapter = CommunityTabAdapter(this)
        viewPager.adapter = adapter

        val tabList = listOf<String>("", "", "")
        val tabLayout: TabLayout = view.findViewById(R.id.carousel_layout)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabList[position]

        }.attach()

    }

    private var nowTab: Int = 0
    private inner class CommunityTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            nowTab = position
            return when (position) {
                0 -> CommunityDetailImg1Fragment()
                1 -> CommunityDetailImg2Fragment()
                2 -> CommunityDetailImg3Fragment()
                else -> CommunityTab1Fragment()
            }
        }
    }

}