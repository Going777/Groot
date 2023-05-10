package com.chocobi.groot.view.pot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chocobi.groot.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PotDetailTab4Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PotDetailTab4Fragment : Fragment() {
    private var place:String? = null
    private var minGrwTemp:Int = 0
    private var maxGrwTemp:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pot_detail_tab4, container, false)
        arguments?.let {
            place = it.getString("place")
            minGrwTemp = it.getInt("minGrwTemp")
            maxGrwTemp = it.getInt("maxGrwTemp")
        }
        val sunTabContent = view.findViewById<TextView>(R.id.sunTabContent)
        // Inflate the layout for this fragment
        val placeDesc = place?.replace(",","\n")!!
        val tempDesc = "${minGrwTemp}~${maxGrwTemp}°C 환경에서 잘 자라요."
        sunTabContent.text = placeDesc+"\n"+tempDesc
        return view
    }


}