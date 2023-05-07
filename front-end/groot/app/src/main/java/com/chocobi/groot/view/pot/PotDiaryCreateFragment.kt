package com.chocobi.groot.view.pot

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.PERMISSION_GALLERY


class PotDiaryCreateFragment : Fragment() {

    private val TAG = "PotDiaryCreateFragment"

    private var potId: Int? = null
    private var potName: String? = null
    private var potPlant: String? = null
    private var potCharImg: String? = null


    private var myImageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mActivity = activity as MainActivity
        val rootView = inflater.inflate(R.layout.fragment_pot_diary_create, container, false)

//        변수 받기
        potId = arguments?.getInt("potId")
        potName = arguments?.getString("potName")
        potPlant = arguments?.getString("potPlant")
        potCharImg = arguments?.getString("potCharImg")

//        화면 구성
        var potNameText = rootView.findViewById<TextView>(R.id.potNameText)
        var potNameText2 = rootView.findViewById<TextView>(R.id.potNameText2)
        potNameText.text = potName
        potNameText2.text = potName
        var potPlantText = rootView.findViewById<TextView>(R.id.potPlantText)
        potPlantText.text = potPlant
        var potCharImage = rootView.findViewById<ImageView>(R.id.characterImg)
        if (potCharImg is String) {
            GlobalVariables.changeImgView(potCharImage, potCharImg.toString(), requireContext())
        }


//        사진 첨부 취소 버튼
        val attachCancleBtn = rootView.findViewById<ImageButton>(R.id.attachCancleBtn)
//        사진 첨부 섹션
        val attachPhotoSection = rootView.findViewById<LinearLayout>(R.id.attachPhotoSection)
//        첨부된 이미지 섹션
        val attachedPhotoSection =
            rootView.findViewById<ConstraintLayout>(R.id.attachedPhotoSection)
        val attachedPhoto = rootView.findViewById<ImageView>(R.id.attachedPhoto)

        myImageView = rootView.findViewById(R.id.attachedPhoto)

        Log.d(TAG, "onCreateView() 포토 이니셜라이즈")
//        사진 첨부하기
//        attachPhotoSection.setOnClickListener {
//            mActivity.requirePermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_GALLERY)
////            attachedPhotoSection.visibility = View.VISIBLE
//        }
        attachPhotoSection.setOnClickListener {
            mActivity.setGalleryStatus("pot_diary_create")
            mActivity.requirePermissions(
                arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                PERMISSION_GALLERY
            )
        }
        attachCancleBtn.setOnClickListener {
            attachPhotoSection.visibility = View.VISIBLE
            attachedPhotoSection.visibility = View.GONE
        }

        return rootView
    }

    fun attachPhoto(uri: Uri) {
        Log.d(TAG, "attachPhoto() 사진을 첨부합니다 $uri")
        myImageView?.setImageURI(uri)
    }

    fun getPhotoImageView(): ImageView? {
        return myImageView
    }
}