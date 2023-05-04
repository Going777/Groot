package com.chocobi.groot.view.plant

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.PERMISSION_CAMERA
import com.chocobi.groot.view.user.ProfileBottomSheet
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
//    private var activityToolbar: androidx.appcompat.widget.Toolbar? = null

    //    fun getToolbar(): androidx.appcompat.widget.Toolbar? {
//        return activityToolbar
//    }



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

//        toolbar 잡기
//        activityToolbar = rootView.findViewById<androidx.appcompat.widget.Toolbar>(R.id.activityToolbar)

//        Fragment 이동 조작
        val mActivity = activity as MainActivity

//        Plant Diary 페이지로 이동
        val toDiaryBtn = rootView.findViewById<Button>(R.id.toDiaryBtn)
        toDiaryBtn.setOnClickListener {
            mActivity.changeFragment("plant_diary")
        }


        val plantFAB = rootView.findViewById<FloatingActionButton>(R.id.plantFAB)
        plantFAB.setOnClickListener {
            var dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("새 화분 등록하기")
            val dialogArray = arrayOf("카메라로 등록", "검색으로 등록")

            dialog.setItems(dialogArray) { _, which ->
                when (which) {
                    0 -> {
                        mActivity.setCameraStatus("addPlant")
                        mActivity.requirePermissions(
                            arrayOf(android.Manifest.permission.CAMERA),
                            PERMISSION_CAMERA
                        )
                    }

                    1 -> {
                        val plantBottomSheet = PlantBottomSheet(requireContext())
                        plantBottomSheet.show(
                            mActivity.supportFragmentManager,
                            plantBottomSheet.tag
                        )
                    }
                }
            }
            dialog.setNegativeButton(
                "취소",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
            dialog.show()
        }


        // Inflate the layout for this fragment
        return rootView
    }


}