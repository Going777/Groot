package com.chocobi.groot.view.pot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.mlkit.kotlin.ml.ArActivity
import com.chocobi.groot.view.pot.adapter.PotCollectionRVAdapter


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PotCollectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PotCollectionFragment : Fragment() {
    private val TAG = "PotCollectionFragment"
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
        val rootView = inflater.inflate(R.layout.fragment_pot_collection, container, false)
        val potItems = mutableListOf<String>()
        val mactivity = activity as MainActivity

        potItems.add("산세산세")
        potItems.add("산세산세")
        potItems.add("산세산세")
        potItems.add("산세산세")
        potItems.add("산세산세")
        potItems.add("산세산세")
        potItems.add("산세산세")
        potItems.add("산세산세")
        potItems.add("산세산세")
        potItems.add("산세산세")
        potItems.add("산세산세")
        potItems.add("산세산세")
        potItems.add("산세산세")

        Log.d(TAG, "onCreateView(), $potItems")

        val potCollectionRv = rootView.findViewById<RecyclerView>(R.id.pot_collectioin_recycler_view)

        val potRvAdapter = PotCollectionRVAdapter(potItems)
        potCollectionRv.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        potCollectionRv.adapter = potRvAdapter

        potRvAdapter.setItemClickListener(object: PotCollectionRVAdapter.ItemClickListener{
            override fun onPostBtnClick(view: View, position: Int) {
                mactivity.changeFragment("pot_diary_create")
            }

            override fun onScanBtnClick(view: View, position: Int) {
                val intent = Intent(context, ArActivity::class.java)
                startActivity(intent)
            }

            override fun onDetailBtnClick(view: View, position: Int) {
                mactivity.changeFragment("pot_detail")
            }
        })

        return rootView
    }

}