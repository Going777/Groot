package com.chocobi.groot.view.pot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chocobi.groot.R


class PotDetailTab2Fragment : Fragment() {
    private var grwType: String? = null
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
        val view = inflater.inflate(R.layout.fragment_pot_detail_tab2, container, false)
        arguments?.let {
            grwType = it.getString("grwType")
            lastDate = it.getString("lastDate")
            comingDate = it.getString("comingDate")
        }
        val pruningTabContent = view.findViewById<TextView>(R.id.pruningTabContent)
        val pruningLastDate = view.findViewById<TextView>(R.id.pruningLastDate)
        val pruningLastDateInfo = view.findViewById<TextView>(R.id.pruningLastDateInfo)
        val pruningComingDate = view.findViewById<TextView>(R.id.pruningComingDate)

        // Inflate the layout for this fragment
        val typeDesc = "${grwType?.replace(",", ", ")!!} 형태로 자라는 식물이에요."
        pruningTabContent.text = typeDesc
        if (lastDate == null) {
            pruningLastDateInfo.text = "아직 분갈이를 하지 않았어요."
        } else {
            pruningLastDate.text = lastDate
        }
        pruningComingDate.text = comingDate
        return view
    }


}