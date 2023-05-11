package com.chocobi.groot.view.pot

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.PERMISSION_CAMERA
import com.google.android.material.floatingactionbutton.FloatingActionButton


class PotFragment : Fragment() {

//    private var activityToolbar: androidx.appcompat.widget.Toolbar? = null

    //    fun getToolbar(): androidx.appcompat.widget.Toolbar? {
//        return activityToolbar
//    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_pot, container, false)

//        toolbar 잡기
//        activityToolbar = rootView.findViewById<androidx.appcompat.widget.Toolbar>(R.id.activityToolbar)

//        Fragment 이동 조작
        val mActivity = activity as MainActivity

//        Pot Diary 페이지로 이동
        val toDiaryBtn = rootView.findViewById<Button>(R.id.toDiaryBtn)
        toDiaryBtn.setOnClickListener {
            mActivity.changeFragment("pot_diary")
        }


        val potFAB = rootView.findViewById<FloatingActionButton>(R.id.potFAB)
        potFAB.setOnClickListener {
            var dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("새 화분 등록하기")
            val dialogArray = arrayOf("카메라로 등록", "검색으로 등록")

            dialog.setItems(dialogArray) { _, which ->
                when (which) {
                    0 -> {
                        mActivity.setCameraStatus("addPot")
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