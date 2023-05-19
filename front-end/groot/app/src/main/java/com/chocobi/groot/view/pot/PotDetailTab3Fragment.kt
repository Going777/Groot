package com.chocobi.groot.view.pot

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chocobi.groot.R


class PotDetailTab3Fragment : Fragment() {
    private val TAG = "PotDetailTab3Fragment"
    private var insectInfo: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pot_detail_tab3, container, false)
        arguments?.let {
            insectInfo = it.getString("insectInfo")
        }
        var insectDesc = "벌레에 강한 식물이에요."
        if (!insectInfo.isNullOrBlank()) {
            insectDesc = "${insectInfo?.replace(",", ", ")} 주의가 필요해요."!!
        }
        val bugTabContent = view.findViewById<TextView>(R.id.bugTabContent)
        bugTabContent.text = insectDesc

        return view
    }


}