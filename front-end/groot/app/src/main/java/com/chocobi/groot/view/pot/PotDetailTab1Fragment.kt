package com.chocobi.groot.view.pot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chocobi.groot.R


class PotDetailTab1Fragment : Fragment() {
    private var waterCycle:String? = null
    private var minHumidity:Int = 0
    private var maxHumidity:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pot_detail_tab1, container, false)
        arguments?.let {
            waterCycle = it.getString("waterCycle")
            minHumidity = it.getInt("minHumidity")
            maxHumidity = it.getInt("maxHumidity")
        }
        val waterTabContent = view.findViewById<TextView>(R.id.waterTabContent)
        // Inflate the layout for this fragment
        val waterDesc = "${minHumidity}~${maxHumidity}% 환경에서 잘 자라요\n${waterCycle?.replace("함","해 주세요")}"!!

        waterTabContent.text = waterDesc
        return view
    }

}