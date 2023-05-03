package com.chocobi.groot.view.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val PERMISSION_CAMERA = 0
    private val PERMISSON_GALLERY = 1


    private fun setupRecyclerView() {
        // RecyclerView 설정
    }

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

        val rootView = inflater.inflate(R.layout.fragment_search, container, false)

//        Fragment 이동 조작
        val mActivity = activity as MainActivity

//        Camera 버튼 클릭
        val cameraBtn = rootView.findViewById<ImageButton>(R.id.cameraBtn)
        cameraBtn.setOnClickListener {
            mActivity.requirePermissions(arrayOf(android.Manifest.permission.CAMERA),  PERMISSION_CAMERA)
        }


//        검색 결과 나타내기
        val dictItems = mutableListOf<String>()

        dictItems.add("산세베리아")
        dictItems.add("산세베리아")
        dictItems.add("산세베리아")
        dictItems.add("산세베리아")
        dictItems.add("산세베리아")
        dictItems.add("산세베리아")
        dictItems.add("산세베리아")
        dictItems.add("산세베리아")
        dictItems.add("산세베리아")
        dictItems.add("산세베리아")
        dictItems.add("산세베리아")
        dictItems.add("산세베리아")
        dictItems.add("산세베리아")
        dictItems.add("산세베리아")
        dictItems.add("산세베리아")
        dictItems.add("산세베리아")
        dictItems.add("산세베리아")
        dictItems.add("산세베리아")


        val rv = rootView.findViewById<RecyclerView>(R.id.dictRecyclerView)

        val rvAdapter = DictRVAdapter(dictItems)
        rv.layoutManager = GridLayoutManager(activity, 3)
        rv.adapter = rvAdapter

        rvAdapter.itemClick = object : DictRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                mActivity.changeFragment("search_detail")
            }
        }

        // 자동완성으로 보여줄 내용들
        val plantNames = GlobalVariables.prefs.getString("plant_names","")
        val items = plantNames.substring(1, plantNames.length - 1).split(",").toTypedArray() // 괄호 제거하고 쉼표로 분리

        var autoCompleteTextView = rootView.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        var adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        autoCompleteTextView.setAdapter(adapter)

        // Inflate the layout for this fragment
        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}