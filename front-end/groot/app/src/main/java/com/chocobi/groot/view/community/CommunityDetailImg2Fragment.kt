package com.chocobi.groot.view.community

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.util.ThreadUtil

class CommunityDetailImg2Fragment(private val image: String) : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_community_detail_img2, container, false)

        val detailImage3 = rootView.findViewById<ImageView>(R.id.detailImage2)

        ThreadUtil.startThread {
            val futureTarget: FutureTarget<Bitmap> = Glide.with(this)
                .asBitmap()
                .load(image)
                .submit()

            val bitmap = futureTarget.get()

            ThreadUtil.startUIThread(0) {
                detailImage3.setImageBitmap(bitmap)
            }
        }

        return rootView
    }

}