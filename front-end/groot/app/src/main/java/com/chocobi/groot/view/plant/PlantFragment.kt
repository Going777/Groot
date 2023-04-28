package com.chocobi.groot.view.plant

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlantFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlantFragment : Fragment() {
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

        val rootView = inflater.inflate(R.layout.fragment_plant, container, false)

//        Fragment 이동 조작
        val mActivity = activity as MainActivity

//        Plant Diary 페이지로 이동
        val toDiaryBtn = rootView.findViewById<Button>(R.id.toDiaryBtn)
        toDiaryBtn.setOnClickListener {
            mActivity.changeFragment("plant_diary")
        }

//        Plant Detail 페이지로 이동
        val toDtlBtn = rootView.findViewById<Button>(R.id.toDtlBtn)
        toDtlBtn.setOnClickListener {
            mActivity.changeFragment("plant_detail")
        }

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
         * @return A new instance of fragment PlantFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlantFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}