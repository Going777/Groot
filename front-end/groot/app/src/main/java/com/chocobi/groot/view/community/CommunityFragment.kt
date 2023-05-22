package com.chocobi.groot.view.community

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.chocobi.groot.view.main.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.view.community.model.Tag
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class CommunityFragment : Fragment() {
    private val TAG = "CommunityFragment"
    private var nowTab: Int = 0

    private var regionList: ArrayList<String>? = null
    private var regionFullList: ArrayList<String>? = null

    private lateinit var popularTags: ArrayList<Tag>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()

        // 소프트 인풋 모드를 원래대로 복원
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CommunityFragment", "onCreateView()")
        // 소프트 인풋 모드를 재설정
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        val rootView = inflater.inflate(R.layout.fragment_community, container, false)

//        Fragment 이동 조작
        val mActivity = activity as MainActivity

//        Community Post 페이지로 이동
        val communityPostFab = rootView.findViewById<FloatingActionButton>(R.id.communityPostFab)
        communityPostFab.setOnClickListener {
            if (nowTab == 0) {
                mActivity.changeFragment("community_share")
            } else if (nowTab == 1) {
                mActivity.changeFragment("community_post")
            } else if (nowTab == 2) {
                mActivity.changeFragment("community_qna")
            } else {
                mActivity.changeFragment("community_tip")
            }
        }

        val communityChatFab = rootView.findViewById<FloatingActionButton>(R.id.communityChatFab)
        communityChatFab.setOnClickListener {
            mActivity.changeFragment("chat_user_list")
        }

        // Inflate the layout for this fragment
        return rootView
    }


    //    탭 구현
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = view.findViewById(R.id.community_pager)
        val adapter = CommunityTabAdapter(this)
        viewPager.adapter = adapter

        val tabList = listOf<String>("나눔", "자유", "QnA", "Tip")
        val tabLayout: TabLayout = view.findViewById(R.id.layout_tab)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabList[position]
        }.attach()

        tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    nowTab = tab.position
                    Log.d(TAG, tab.position.toString())
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })


    }

    private inner class CommunityTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int {
            return 4
        }

        override fun createFragment(position: Int): Fragment {

            return when (position) {
                0 -> {
                    val bundle = Bundle().apply {
                        putStringArrayList("region_list", regionList)
                        putStringArrayList("region_full_list", regionFullList)
                    }
                    CommunityTab1Fragment().apply { arguments = bundle }
                }

                1 -> CommunityTab2Fragment()
                2 -> CommunityTab3Fragment()
                3 -> CommunityTab4Fragment()
                else -> CommunityTab1Fragment()
            }
        }
    }

    //    사용자와 상호작용가능한 상태가 되었을 때 호출
    override fun onResume() {
        super.onResume()
        regionList = arguments?.getStringArrayList("region_list")
        regionFullList = arguments?.getStringArrayList("region_full_list")
    }
}