package com.chocobi.groot.view.community

import android.content.Context
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
import com.chocobi.groot.Thread.ThreadUtil
import java.lang.ref.WeakReference

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CommunityDetailImg3Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CommunityDetailImg3Fragment(private val image: String) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
//    private var view: WeakReference<View> = WeakReference(itemView)


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
        val rootView = inflater.inflate(R.layout.fragment_community_detail_img1, container, false)

        var detailImage1 = rootView.findViewById<ImageView>(R.id.detailImage1)

//        detailImage1.post {
//            view.get()?.let {
//                ThreadUtil.startThread {
//                    val futureTarget: FutureTarget<Bitmap> = Glide.with(it.context)
//                        .asBitmap()
//                        .load(image)
//                        .submit(detailImage1.width, detailImage1.height)
//
//                    val bitmap = futureTarget.get()
//
//                    ThreadUtil.startUIThread(0) {
//                        detailImage1.setImageBitmap(bitmap)
//                    }
//                }
//            }
//        }


        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CommunityDetailImg3Fragment.
         */
        // TODO: Rename and change types and number of parameters

    }
}