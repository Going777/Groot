package com.chocobi.groot.view.community

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.chocobi.groot.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.INDICATOR_GRAVITY_CENTER
import com.google.android.material.tabs.TabLayoutMediator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CommunityDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CommunityDetailFragment : Fragment() {
    private lateinit var bookmarkButton: ImageButton
    private var isBookmarked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_community_detail, container, false)



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
                Toast.makeText(requireContext(), selectedOption, Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무것도 선택하지 않은 경우 처리
            }
        }

        spinnerButton.setOnClickListener {
            spinner.performClick()
        }



        // 북마크

        bookmarkButton = view.findViewById(R.id.bookmarkLine)
        isBookmarked = false

        bookmarkButton.setOnClickListener {
            isBookmarked = !isBookmarked
            bookmarkButton.setImageResource(
                if (isBookmarked) R.drawable.ic_bookmark_fill
                else R.drawable.ic_bookmark
            )
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