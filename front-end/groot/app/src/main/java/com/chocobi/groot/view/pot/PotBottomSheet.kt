package com.chocobi.groot.view.pot

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.MsgResponse
import com.chocobi.groot.data.PERMISSION_CAMERA
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.pot.model.PotService
import com.chocobi.groot.view.weather.Main
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PotBottomSheet(context: Context) : BottomSheetDialogFragment() {
    private val TAG = "PotBottomSheet"

    private var potId: Int = 0
    private lateinit var mActivity: MainActivity
    private lateinit var dialog: AlertDialog.Builder

    fun setPotId(id: Int) {
        potId = id
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.bottom_sheet_pot, container, false)
        mActivity = activity as MainActivity

        val deletePotBtn = view.findViewById<ImageButton>(R.id.deletePot)
        deletePotBtn.setOnClickListener {
            dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("화분 삭제")
            dialog.setMessage("화분을 삭제하시겠습니까?")
            dialog.setPositiveButton(
                "삭제",
                DialogInterface.OnClickListener { dialog, which ->
                    deletePot(potId)
                    dialog.dismiss()

                })
            dialog.setNegativeButton(
                "취소",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
            dialog.show()
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        view?.findViewById<Button>(R.id.button_bottom_sheet)?.setOnClickListener {
//            dismiss()
//        }
    }

    private fun deletePot(potId: Int) {
        var retrofit = RetrofitClient.getClient()!!
        var potService = retrofit.create(PotService::class.java)
        potService.deletePot(potId).enqueue(object :
            Callback<MsgResponse> {
            override fun onResponse(
                call: Call<MsgResponse>,
                response: Response<MsgResponse>
            ) {
                val body = response.body()
                if (body != null && response.code() == 200) {
                    Log.d(TAG, "body: $body")
                    dismiss()
                    Toast.makeText(
                        requireContext(),
                        "화분이 삭제되었습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                    mActivity.changeFragment("pot")

                } else {
                    Log.d(TAG, "실패1")
                }
            }

            override fun onFailure(call: Call<MsgResponse>, t: Throwable) {
                Log.d(TAG, "실패2")
            }
        })

    }


}