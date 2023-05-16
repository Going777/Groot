package com.chocobi.groot.view.community

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
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
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.community.adapter.ArticleTagAdapter
import com.chocobi.groot.view.community.adapter.CommentAdapter
import com.chocobi.groot.view.community.model.Article
import com.chocobi.groot.view.community.model.BookmarkResponse
import com.chocobi.groot.view.community.model.Comment
import com.chocobi.groot.view.community.model.CommunityArticleDetailResponse
import com.chocobi.groot.view.community.model.CommunityCommentResponse
import com.chocobi.groot.view.community.model.CommunityService
import com.chocobi.groot.view.weather.Main
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.hdodenhof.circleimageview.CircleImageView
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

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var frameLayoutProgress: FrameLayout
    private lateinit var getCommentData: CommunityCommentResponse
    private var articleId: Int = 0
    private lateinit var frameLayoutComment: FrameLayout
    private var shareStatus = false
    private var userPK: Int = 0
    private var bookmarkStatus = false
    private lateinit var shareStateText: TextView
    private var articleDetailData: Article? = null
    private lateinit var detailCategory: TextView
    private lateinit var categoryIcon: ImageView
    private lateinit var backBtn: ImageView
    private lateinit var userProfile: CircleImageView

    private lateinit var detailTitle: TextView
    private lateinit var detailNickName: TextView
    private lateinit var detailViews: TextView
    private lateinit var detailCreateTime: TextView
    private lateinit var detailContent: TextView
    private lateinit var detailCommentCnt: TextView
    private lateinit var detailProfileImg: CircleImageView
    private lateinit var sharePosition: TextView

    private lateinit var shareSection: LinearLayoutCompat
    private lateinit var commentSection: LinearLayoutCompat
    private lateinit var commentInputSection: CardView
    private lateinit var spinnerButton: ImageButton
    private lateinit var carouselSection: LinearLayoutCompat
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var imageAdapter: CommunityTabAdapter

    private lateinit var mActivity: MainActivity
    private lateinit var progressSection: ConstraintLayout


    private val imagesList: MutableList<String?> = arrayListOf()

    private lateinit var getData: CommunityArticleDetailResponse


    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_community_detail, container, false)
        articleId = arguments?.getInt("articleId") ?: 0
        mActivity = activity as MainActivity
        userPK = UserData.getUserPK()
        val args = Bundle()
        if (articleId != 0) {
            args.putInt("articleId", articleId)
        }

        val communityUserShareFragment = CommunityUserShareFragment()
        communityUserShareFragment.arguments = args
        childFragmentManager.beginTransaction()
            .add(R.id.communityUserShareFragment, communityUserShareFragment)
            .commit()

//        ================================================================
//        ================================================================

        findViews(view)
        setFirstView()
        imageAdapter = CommunityTabAdapter(this)

        return view
    }

    private fun getArticleDetail() {
        val retrofit = RetrofitClient.getClient()!!

        val communityArticleDetailService =
            retrofit.create(CommunityArticleDetailService::class.java)

        communityArticleDetailService.requestCommunityArticleDetail(articleId!!).enqueue(object :
            Callback<CommunityArticleDetailResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<CommunityArticleDetailResponse>,
                response: Response<CommunityArticleDetailResponse>
            ) {
                if (response.code() == 200) {
                    Log.d(TAG, "성공")
                    val responseData = response.body()?.article
                    getData = response.body()!!
                    Log.d(TAG, "$responseData")

                    val article = getData.article
                    articleDetailData = Article(
                        category = article.category,
                        profile = article.profile,
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

                    bookmarkStatus = articleDetailData?.bookmark ?: false
                    setDetailContent()
                    setImageCarousel()


                    // 태그
                    recyclerView.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

                    // 태그 Adapter 설정
                    val tagAdapter = ArticleTagAdapter(tagList)
                    recyclerView.adapter = tagAdapter

                } else {
                    Log.d(TAG, "게시글 디테일 실패1")
                    Log.d(TAG, response.toString())
                }
            }

            override fun onFailure(call: Call<CommunityArticleDetailResponse>, t: Throwable) {
                Log.d(TAG, "게시글 디테일 실패2")
            }
        }
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getArticleDetail()


//        댓글
        setListeners()
        initList()
        showProgress()
        commentRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        val layoutManager = LinearLayoutManager(context)
        commentRecyclerView.layoutManager = layoutManager
        getArticleComment()

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
                        0 -> CommunityDetailImg1Fragment(imagesList[0].toString())
                        else -> CommunityTab1Fragment()
                    }
                }

                2 -> {
                    nowTab = position
                    return when (position) {
                        0 -> CommunityDetailImg1Fragment(imagesList[0].toString())
                        1 -> CommunityDetailImg2Fragment(imagesList[1].toString())
                        else -> CommunityTab1Fragment()
                    }
                }

                3 -> {
                    nowTab = position
                    return when (position) {
                        0 -> CommunityDetailImg1Fragment(imagesList[0].toString())
                        1 -> CommunityDetailImg2Fragment(imagesList[1].toString())
                        2 -> CommunityDetailImg3Fragment(imagesList[2].toString())
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
            .enqueue(object : Callback<CommunityCommentResponse> {
                override fun onResponse(
                    call: Call<CommunityCommentResponse>,
                    response: Response<CommunityCommentResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("commentResponse", response.body().toString())
                        val commentitem = response.body()!!.comment[0]
                        Log.d("commentItem", commentitem.toString())
//                        for (commentReturn in response.body()!!.comment) {
//                            val communityCommentResponse = CommunityCommentResponse(
//                                comment = listOf(
//                                    Comment(
//                                        userPK = commentitem.userPK ?: 0,
//                                        nickName = commentitem.nickName ?: "",
//                                        commentId = commentitem.commentId ?: 0,
//                                        content = commentitem.content ?: "",
//                                        profile = commentitem.profile ?: "",
//                                        createTime = commentitem.createTime,
//                                        updateTime = commentitem.updateTime
//                                    )
//                                ),
//                                result = getCommentData.result,
//                                msg = getCommentData.msg
//                            )
//                        }

//                            userPK = userPK,
//                            nickname = nickname,
//                            content = content,
//                            createTime = createTime,
//                            updateTime = updateTime
//                        )
//                        commentAdapter.addComment(comment)

//                        val comments = getCommentData.comment
//                        Log.d("CommunityCommentFragmentComments", comments.toString())
//                        for (commentitem in comments) {
//                            val communityCommentResponse = CommunityCommentResponse(
//                                comment = listOf(
//                                    Comment(
//                                        userPK = commentitem.userPK ?: 0,
//                                        nickName = commentitem.nickName ?: "",
//                                        commentId = commentitem.commentId ?: 0,
//                                        content = commentitem.content ?: "",
//                                        profile = commentitem.profile ?: "",
//                                        createTime = commentitem.createTime,
//                                        updateTime = commentitem.updateTime
//                                    )
//                                ),
//                                result = getCommentData.result,
//                                msg = getCommentData.msg
//                            )
//                            commentAdapter.addComment(communityCommentResponse)
//                        }
                    }
                }


                override fun onFailure(call: Call<CommunityCommentResponse>, t: Throwable) {
                    Log.d("CommunityDetailFragment", "댓글 작성 실패")
                    Log.d("CommunityDetailFragmentCommentPost", "$t")
                }
            })
    }


    private fun findViews(view: View) {
//        로딩스피너
        progressSection = view.findViewById(R.id.progressSection)
//        헤더
        detailCategory = view.findViewById(R.id.categoryName)
        categoryIcon = view.findViewById(R.id.categoryIcon)
        backBtn = view.findViewById(R.id.backBtn)

//        사용자 프로필
        userProfile = view.findViewById<CircleImageView>(R.id.userProfile)

//        이미지 섹션
        carouselSection = view.findViewById(R.id.carouselSection)
        viewPager = view.findViewById(R.id.carousel_pager)
        tabLayout = view.findViewById(R.id.carousel_layout)

//        디테일
        detailTitle = view.findViewById(R.id.detailTitle)
        detailNickName = view.findViewById(R.id.detailNickName)
        detailViews = view.findViewById(R.id.detailViews)
        detailCreateTime = view.findViewById(R.id.detailCreateTime)
        detailContent = view.findViewById(R.id.detailContent)
        detailCommentCnt = view.findViewById(R.id.detailCommentCnt)
        detailProfileImg = view.findViewById(R.id.detailProfileImg)
        sharePosition = view.findViewById<TextView>(R.id.sharePosition)


//        카테고리 분기
        shareSection = view.findViewById<LinearLayoutCompat>(R.id.shareSection)
        commentSection = view.findViewById<LinearLayoutCompat>(R.id.commentSection)
        commentInputSection = view.findViewById<CardView>(R.id.commentInputSection)

//        나눔 상태
        shareStateText = view.findViewById(R.id.shareStateText)

//        북마크
        bookmarkButton = view.findViewById(R.id.bookmarkLine)

//        댓글
        postCommentBtn = view.findViewById(R.id.postCommentBtn)
        postCommentInput = view.findViewById(R.id.postCommentInput)

        //    댓글 리사이클러뷰
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        commentRecyclerView = view.findViewById(R.id.commentRecycleView)
        frameLayoutProgress = view.findViewById(R.id.frameLayoutProgress)


//        글 설정 버튼
        spinnerButton = view.findViewById(R.id.spinnerButton)

//        태그
        recyclerView = view.findViewById(R.id.tagList)
    }

    //    댓글 리사이클러뷰
    private fun setListeners() {
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initList() {
        commentAdapter = CommentAdapter(commentRecyclerView)
        commentRecyclerView.adapter = commentAdapter // RecyclerView에 Adapter 설정
        val size = commentAdapter.itemCount
        commentRecyclerView.scrollToPosition(size - 1)

        commentAdapter.delegate = object : CommentAdapter.RecyclerViewAdapterDelegate {
            override fun onLoadMore() {
            }

            fun reload(mutableList: MutableList<CommunityCommentResponse>) {
                TODO("Not yet implemented")
            }

            fun loadMore(mutableList: MutableList<CommunityCommentResponse>) {
                TODO("Not yet implemented")
            }
        }
        commentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        commentRecyclerView.adapter = commentAdapter
    }

    private fun showProgress() {
        frameLayoutProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        frameLayoutProgress.visibility = View.GONE
    }

    private fun createDummyData(): MutableList<CommunityCommentResponse> {
        val list: MutableList<CommunityCommentResponse> = mutableListOf()

// API response를 이용하여 데이터 생성
        val comments = getCommentData.comment
        Log.d("CommunityCommentFragmentComments", comments.toString())
        for (commentitem in comments) {
            val communityCommentResponse = CommunityCommentResponse(
                comment = listOf(
                    Comment(
                        userPK = commentitem.userPK ?: 0,
                        nickName = commentitem.nickName ?: "",
                        commentId = commentitem.commentId ?: 0,
                        content = commentitem.content ?: "",
                        profile = commentitem.profile ?: "",
                        createTime = commentitem.createTime,
                        updateTime = commentitem.updateTime
                    )
                ),
                result = getCommentData.result,
                msg = getCommentData.msg
            )
            list.add(communityCommentResponse)
        }
        Log.d("CommunityCommentFragmentList", list.toString())
        return list
    }


    private fun changeShareStatus(articleId: Int, userPK: Int) {
        val retrofit = RetrofitClient.getClient()
        val communityShareStatusService = retrofit?.create(CommunityShareStatusService::class.java)
        communityShareStatusService?.requestCommunityShareStatus(
            ShareStatusRequest(
                articleId,
                userPK
            )
        )
            ?.enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.code() == 200) {
                        shareStatus = !shareStatus

                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Log.d(TAG, "나눔상태 변경 실패")
                }
            })

    }

    private fun getArticleComment() {
        // retrofit 객체 만들기
        var commentRetrofit = RetrofitClient.getClient()!!
        var communityCommentService = commentRetrofit.create(CommunityCommentService::class.java)

        communityCommentService.requestCommunityComment(articleId).enqueue(object :
            Callback<CommunityCommentResponse> {
            override fun onResponse(
                call: Call<CommunityCommentResponse>,
                response: Response<CommunityCommentResponse>
            ) {
                if (response.code() == 200) {
                    Log.d("CommunityCommentFragment", "성공")
                    val checkResponse = response.body()?.comment
                    getCommentData = response.body()!!
                    Log.d("CommunityCommentFragment", "$checkResponse")

                    val list = createDummyData()
                    ThreadUtil.startUIThread(1000) {
                        commentAdapter.reload(list)
                        hideProgress()

                    }


                } else {
                    Log.d("CommunityCommentFragment", "실패1")
                }
            }

            override fun onFailure(call: Call<CommunityCommentResponse>, t: Throwable) {
                Log.d("CommunityCommentFragment실패", "실패2")
            }
        })
    }

    private fun requestBookmark() {
        val retrofit = RetrofitClient.getClient()!!
        val communityBookmarkService = retrofit.create(CommunityBookmarkService::class.java)
        communityBookmarkService.requestCommunityBookmark(
            BookmarkRequest(
                articleId,
                userPK,
                bookmarkStatus
            )
        ).enqueue(object :
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

    private fun setFirstView() {
//        헤더
        categoryIcon.setImageResource(R.drawable.ic_article)
        backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

//        사용자 프로필
        userProfile.post {
            ThreadUtil.startThread {
                val futureTarget: FutureTarget<Bitmap> = Glide.with(requireContext())
                    .asBitmap()
                    .load(UserData.getProfile())
                    .submit(userProfile.width, userProfile.height)

                val bitmap = futureTarget.get()

                ThreadUtil.startUIThread(0) {
                    userProfile.setImageBitmap(bitmap)
                }
            }
        }

//        댓글
        postCommentBtn.setOnClickListener {
            var content = postCommentInput?.text.toString()
            if (articleId != null) {
                postComment(articleId, content)
            }

            // 입력창 리셋 및 키보드 닫기
            postCommentInput?.setText("")
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                view?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )

        }


        //        북마크 수정 api

        bookmarkButton.setOnClickListener {
            requestBookmark()
        }
    }

    private fun setDetailContent() {
        detailCategory.text = articleDetailData?.category
        detailTitle.text = articleDetailData?.title
        detailNickName.text = articleDetailData?.nickName
        detailViews.text = articleDetailData?.views.toString()
        val koreahour = (articleDetailData?.createTime?.time?.hour ?: 0) + 9
        detailCreateTime.text =
            articleDetailData?.createTime?.date?.year.toString() + '.' + articleDetailData?.createTime?.date?.month.toString() + '.' + articleDetailData?.createTime?.date?.day.toString() + ' ' + koreahour + ':' + articleDetailData?.createTime?.time?.minute.toString()
        detailContent.text = articleDetailData?.content
        tagList = articleDetailData?.tags ?: emptyList()
        sharePosition.text = articleDetailData?.shareRegion
        detailCommentCnt.text =
            "댓글 (" + articleDetailData?.commentCnt.toString() + ")"



        detailProfileImg.post {
            ThreadUtil.startThread {
                val futureTarget: FutureTarget<Bitmap> = Glide.with(requireContext())
                    .asBitmap()
                    .load(articleDetailData?.profile)
                    .submit(detailProfileImg.width, detailProfileImg.height)

                val bitmap = futureTarget.get()

                ThreadUtil.startUIThread(0) {
                    detailProfileImg.setImageBitmap(bitmap)
                }
            }
        }

        // 카테고리별 섹션 구별
        if (detailCategory.text == "나눔") {
            shareStatus = articleDetailData?.shareStatus!!

            if (articleDetailData?.shareStatus == true) {
                shareStateText.visibility = View.VISIBLE
            } else if (articleDetailData?.shareStatus == false) {
                shareStateText.visibility = View.GONE
            }
            sharePosition.visibility = View.VISIBLE
            shareSection.visibility = View.VISIBLE
            commentSection.visibility = View.GONE
            commentInputSection.visibility = View.GONE
        } else {
            shareStateText.visibility = View.GONE
            sharePosition.visibility = View.GONE
            shareSection.visibility = View.GONE
            commentSection.visibility = View.VISIBLE
            commentInputSection.visibility = View.VISIBLE
        }

        // 북마크
        if (bookmarkStatus == true) {
            bookmarkButton.setImageResource(R.drawable.ic_bookmark_fill)
        } else {
            bookmarkButton.setImageResource(R.drawable.ic_bookmark)
        }

//        드롭다운
        if (UserData.getUserPK() == articleDetailData?.userPK) {
            spinnerButton.setOnClickListener {
                val articleBottomSheet = ArticleBottomSheet(requireContext(), articleId)
                articleBottomSheet.show(
                    mActivity.supportFragmentManager,
                    articleBottomSheet.tag
                )
            }
        } else {
            spinnerButton.visibility = View.GONE
        }


    }

    private fun setImageCarousel() {
        if (articleDetailData?.imgs != null && articleDetailData?.imgs!!.isNotEmpty()) {
            carouselSection.visibility = View.VISIBLE
            for (i in 1..articleDetailData?.imgs!!.size) {
                imagesList.add(articleDetailData?.imgs!![i - 1])
                Log.d("carouselImagesList", imagesList.toString())
            }
        } else if (articleDetailData?.imgs != null && articleDetailData?.imgs!!.isEmpty()) {
            carouselSection.visibility = View.GONE
            val layoutParams = detailTitle.layoutParams as ViewGroup.MarginLayoutParams
            detailTitle.layoutParams = layoutParams
        }
        viewPager.adapter = imageAdapter

        var tabList = listOf<String>()

        when (imagesList.size) {
            0 -> {
                tabList = listOf<String>()
            }

            1 -> {
                tabList = listOf<String>("")
            }

            2 -> {
                tabList = listOf<String>("", "")
            }

            3 -> {
                tabList = listOf<String>("", "", "")
            }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabList[position]

        }.attach()

        progressSection.visibility = View.GONE
    }

    private fun setShareText(selectedOption: String) {
        if (selectedOption == " 나눔 완료 ") {
            changeShareStatus(articleId, UserData.getUserPK())
            shareStateText.visibility = View.VISIBLE
            shareStateText.text = "나눔 완료"
            shareStatus = true

        } else if (selectedOption == " 나눔 취소 ") {
            changeShareStatus(articleId, UserData.getUserPK())
            shareStateText.visibility = View.VISIBLE
            shareStateText.text = "나눔 취소"
            shareStatus = false
        }
    }
}