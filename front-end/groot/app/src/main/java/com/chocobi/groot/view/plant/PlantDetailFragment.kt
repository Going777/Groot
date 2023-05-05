package com.chocobi.groot.view.plant

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chocobi.groot.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlantDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlantDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val TAG = "PlantDetailFragment"

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
        var rootView = inflater.inflate(R.layout.fragment_plant_detail, container, false)


        // Inflate the layout for this fragment
        return rootView  }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val potId = arguments?.getInt("potId")
        Log.d(TAG, potId.toString())
        val plantName = view.findViewById<TextView>(R.id.plantName)
        plantName.text = potId.toString() + "번 화분"

        val tab1 = PlantDetailTab1Fragment()
        val tab2 = PlantDetailTab2Fragment()
        val tab3 = PlantDetailTab3Fragment()
        val tab4 = PlantDetailTab4Fragment()
        val tab5 = PlantDetailTab5Fragment()

        var tabBtn1 = view.findViewById<ImageButton>(R.id.tabBtn1)
        var tabBtn2 = view.findViewById<ImageButton>(R.id.tabBtn2)
        var tabBtn3 = view.findViewById<ImageButton>(R.id.tabBtn3)
        var tabBtn4 = view.findViewById<ImageButton>(R.id.tabBtn4)
        var tabBtn5 = view.findViewById<ImageButton>(R.id.tabBtn5)
        tabBtn1.setOnClickListener {
            childFragmentManager.beginTransaction().replace(R.id.tab_container, tab1).commit()
        }
        tabBtn2.setOnClickListener {
            childFragmentManager.beginTransaction().replace(R.id.tab_container, tab2).commit()
        }
        tabBtn3.setOnClickListener {
            childFragmentManager.beginTransaction().replace(R.id.tab_container, tab3).commit()
        }
        tabBtn4.setOnClickListener {
            childFragmentManager.beginTransaction().replace(R.id.tab_container, tab4).commit()
        }
        tabBtn5.setOnClickListener {
            childFragmentManager.beginTransaction().replace(R.id.tab_container, tab5).commit()
        }


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlantDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlantDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}