package com.chocobi.groot.view.pot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.chocobi.groot.R


class PotDetailTab5Fragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_pot_detail_tab5, container, false)
        arguments?.let {
            lastDate = it.getString("lastDate")
            comingDate = it.getString("comingDate")
        }
        val nutrientLastDate = view.findViewById<TextView>(R.id.nutrientLastDate)
        val nutrientComingDate = view.findViewById<TextView>(R.id.nutrientComingDate)
        val nutrientLastDateInfo = view.findViewById<TextView>(R.id.nutrientLastDateInfo)
        val nutrientComingDateSection =
            view.findViewById<LinearLayout>(R.id.nutrientComingDateSection)
        if (lastDate == null) {
            nutrientLastDateInfo.text = "아직 영양제를 주지 않았어요."
        } else {
            nutrientLastDate.text = lastDate
        }
        if (comingDate == null) {
            nutrientComingDateSection.visibility = View.GONE
        } else {
            nutrientComingDate.text = comingDate
        }

        // Inflate the layout for this fragment
        return view
    }

}