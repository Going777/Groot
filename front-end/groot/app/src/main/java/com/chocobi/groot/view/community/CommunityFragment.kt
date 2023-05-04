package com.chocobi.groot.view.community

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CommunityFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CommunityFragment : Fragment() {
    private val TAG = "CommunityFragment"
    private var nowTab: Int = 0
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_community, container, false)

//        Fragment 이동 조작
        val mActivity = activity as MainActivity

//        Community Post 페이지로 이동
        val communityPostFab = rootView.findViewById<FloatingActionButton>(R.id.communityPostFab)
        communityPostFab.setOnClickListener {
            if (nowTab == 0) {
                mActivity.changeFragment("community_share")
            } else {
                mActivity.changeFragment("community_post")
            }
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


    }

    private inner class CommunityTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int {
            return 4
        }

        override fun createFragment(position: Int): Fragment {
            nowTab = position
            Log.d(TAG, nowTab.toString())
            return when (position) {
                0 -> CommunityTab1Fragment()
                1 -> CommunityTab2Fragment()
                2 -> CommunityTab3Fragment()
                3 -> CommunityTab4Fragment()
                else -> CommunityTab1Fragment()
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CommunityFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CommunityFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}