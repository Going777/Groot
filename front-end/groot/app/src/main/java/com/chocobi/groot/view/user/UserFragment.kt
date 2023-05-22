package com.chocobi.groot.view.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.community.CommunityTab1Fragment
import com.chocobi.groot.view.community.CommunityTab2Fragment
import com.chocobi.groot.view.community.CommunityTab3Fragment
import com.chocobi.groot.view.community.CommunityTab4Fragment
import com.chocobi.groot.view.community.model.CommunityArticleListResponse
import com.chocobi.groot.view.user.model.UserService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserFragment : Fragment() {


    private val TAG = "UserFragment"
    private lateinit var collectionBtn: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_user, container, false)

        collectionBtn = rootView.findViewById(R.id.collectionBtn)
        collectionBtn.setOnClickListener {
            var intent = Intent(requireContext(), CollectionActivity::class.java)
//            intent.putExtra("toPage", "search_detail")
//            intent.putExtra("plant_id", plantId.toString())
            startActivity(intent)
        }

        //        초기 화면 설정
        var nicknameText = rootView.findViewById<TextView>(R.id.nickname)
        nicknameText.text = UserData.getNickName()

        var registerDateText = rootView.findViewById<TextView>(R.id.registerDate)
        registerDateText.text = UserData.getRegisterDate().toString()

        var totalArticleText = rootView.findViewById<TextView>(R.id.totalArticle)
        getUserArticle(totalArticleText)

        var totalBookmarkText = rootView.findViewById<TextView>(R.id.totalBookmark)
        getUserBookmark(totalBookmarkText)

        var profileImg = rootView.findViewById<CircleImageView>(R.id.profileImg)
        var userProfile = UserData.getProfile()
        if (userProfile != "" && userProfile != null) {
            GlobalVariables.changeImgView(profileImg, userProfile, requireContext())
        } else {
            profileImg.setImageResource(R.drawable.basic_profile)
        }

//        Fragment 이동 조작
        val mActivity = activity as MainActivity

//        Setting 페이지로 이동
        val settingBtn = rootView.findViewById<ImageButton>(R.id.settingBtn)
        settingBtn.setOnClickListener {
            mActivity.changeFragment("setting")
        }

        // Inflate the layout for this fragment
        return rootView
    }


    //    탭 구현
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = view.findViewById(R.id.user_pager)
        val adapter = UserTabAdapter(this)
        viewPager.adapter = adapter

        val tabList = listOf<String>("식물", "작성글", "북마크")
        val tabLayout: TabLayout = view.findViewById(R.id.layout_tab)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabList[position]
        }.attach()
    }

    private inner class UserTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> UserTab1Fragment()
                1 -> UserTab2Fragment()
                2 -> UserTab3Fragment()
                else -> UserTab1Fragment()
            }
        }
    }



    private fun getUserArticle(totalArticleText: TextView) {

        var retrofit = RetrofitClient.getClient()!!
        var userService = retrofit.create(UserService::class.java)

        userService.requestUserArticleList(0, 1).enqueue(object :
            Callback<CommunityArticleListResponse> {
            override fun onResponse(
                call: Call<CommunityArticleListResponse>,
                response: Response<CommunityArticleListResponse>
            ) {
                if (response.code() == 200) {
                    Log.d(TAG, "성공")

                    val checkTotal = response.body()?.articles?.total
                    totalArticleText.text = checkTotal.toString()
                    Log.d(TAG, "$checkTotal")

                } else {
                    Log.d(TAG, "실패1")
                }
            }

            override fun onFailure(call: Call<CommunityArticleListResponse>, t: Throwable) {
                Log.d(TAG, "실패2")
            }
        })
    }

    private fun getUserBookmark(totalBookmarkText: TextView) {

        var retrofit = RetrofitClient.getClient()!!
        var userService = retrofit.create(UserService::class.java)

        userService.requestUserBookmarkList(0, 1).enqueue(object :
            Callback<CommunityArticleListResponse> {
            override fun onResponse(
                call: Call<CommunityArticleListResponse>,
                response: Response<CommunityArticleListResponse>
            ) {
                if (response.code() == 200) {
                    Log.d(TAG, "성공")

                    val checkTotal = response.body()?.articles?.total
                    totalBookmarkText.text = checkTotal.toString()
                    Log.d(TAG, "$checkTotal")

                } else {
                    Log.d(TAG, "실패1")
                }
            }

            override fun onFailure(call: Call<CommunityArticleListResponse>, t: Throwable) {
                Log.d(TAG, "실패2")
            }
        })
    }

}