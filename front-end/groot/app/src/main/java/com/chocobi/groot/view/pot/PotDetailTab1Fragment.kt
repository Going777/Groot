package com.chocobi.groot.view.pot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chocobi.groot.R


class PotDetailTab1Fragment : Fragment() {
    private var waterCycle: String? = null
    private var minHumidity: Int = 0
    private var maxHumidity: Int = 0
    private var lastDate: String? = null
    private var comingDate: String? = null


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
            lastDate = it.getString("lastDate")
            comingDate = it.getString("comingDate")
        }
        val waterTabContent = view.findViewById<TextView>(R.id.waterTabContent)
        val waterLastDate = view.findViewById<TextView>(R.id.waterLastDate)
        val waterLastDateInfo = view.findViewById<TextView>(R.id.waterLastDateInfo)
        val waterComingDate = view.findViewById<TextView>(R.id.waterComingDate)
        // Inflate the layout for this fragment
        val waterDesc =
            "습도 ${minHumidity}~${maxHumidity}% 환경에서 잘 자라요.\n${waterCycle?.replace("함", "해 주세요.")}"!!
        waterTabContent.text = waterDesc
        if (lastDate == null) {
            waterLastDateInfo.text = "아직 물을 주지 않았어요."
        } else {
            waterLastDate.text = lastDate
        }
        waterComingDate.text = comingDate
        return view
    }

}