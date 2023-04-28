package com.chocobi.groot.view.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.view.search.DictRVAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserTab1Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserTab1Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_user_tab1, container, false)
        val mActivity = activity as MainActivity
//        리사이클 뷰
        val items = mutableListOf<String>()
        items.add("산세베리아")
        items.add("산세베리아")
        items.add("산세베리아")

        val rv = rootView.findViewById<RecyclerView>(R.id.useTab1RecyclerView)

        val rvAdapter = UserTab1RVAdapter(items)
        rv.layoutManager = LinearLayoutManager(activity)
        rv.adapter = rvAdapter

        rvAdapter.itemClick = object : UserTab1RVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                mActivity.changeFragment("plant_detail")
            }
        }

        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserTab1Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserTab1Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}