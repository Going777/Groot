package com.chocobi.groot.view.user


import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.GlobalVariables.Companion.changeImgView
import com.chocobi.groot.data.PERMISSION_GALLERY
import com.chocobi.groot.data.UserData
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileBottomSheet(context: Context) : BottomSheetDialogFragment() {
    val TAG = "ProfileBottomSheet"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.bottom_sheet_profile, container, false)
        val mActivity = activity as MainActivity

//        초기 설정
        val nameText = view.findViewById<EditText>(R.id.nameText)
        nameText.setText(UserData.getNickName())
        val profileImg = view.findViewById<ImageView>(R.id.profileImg)
        var userProfile = UserData.getProfile()
        if (userProfile != "" && userProfile != null) {
            Log.d(TAG, userProfile)
            changeImgView(profileImg, userProfile, requireContext())
        }

//        카메라 버튼 클릭
        val profileImgBtn = view.findViewById<ImageButton>(R.id.profileImgBtn)
        profileImgBtn.setOnClickListener {
            mActivity.requirePermissions(
                arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                PERMISSION_GALLERY
            )
        }

//        완료 버튼 클릭
        val profileSubmitBtn = view.findViewById<Button>(R.id.profileSubmitBtn)
        profileSubmitBtn.setOnClickListener {

        }



        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.findViewById<Button>(R.id.button_bottom_sheet)?.setOnClickListener {
            dismiss()
        }
    }

}