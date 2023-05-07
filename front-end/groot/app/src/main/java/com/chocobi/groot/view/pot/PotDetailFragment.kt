package com.chocobi.groot.view.pot

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.Transition
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.chocobi.groot.R
import com.google.android.gms.common.internal.ImagesContract.URL
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.utils.Color
import java.io.BufferedInputStream
import java.net.URL

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PotDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("DEPRECATION")
class PotDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val TAG = "PotDetailFragment"

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
        var rootView = inflater.inflate(R.layout.fragment_pot_detail, container, false)


        // Inflate the layout for this fragment
        return rootView  }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val potId = arguments?.getInt("potId")
        Log.d(TAG, potId.toString())
        val potName = view.findViewById<TextView>(R.id.potName)
        potName.text = potId.toString() + "번 화분"


        val characterSceneView = view.findViewById<SceneView>(R.id.characterSceneView)
        characterSceneView.backgroundColor = Color(255.0f, 255.0f, 255.0f, 255.0f)
        val imageUrl = "https://groot-a303-s3.s3.ap-northeast-2.amazonaws.com/assets/tree_2.png"
        Glide.with(this)
            .load(imageUrl)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                ) {
                    characterSceneView.setBackgroundDrawable(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Optional, but can be used to null out references to avoid memory leaks
                }
            })
//        characterSceneView.backgroundColor = Color(1.0f, 1.0f, 1.0f, 1.0f)
//        characterSceneView.setBackgroundDrawable(drawable)


        val modelNode = ModelNode().apply {
            loadModelGlbAsync(
                glbFileLocation = "https://groot-a303-s3.s3.ap-northeast-2.amazonaws.com/assets/porin_2.glb",
                autoAnimate = false,
                scaleToUnits = 1.3f,
                centerOrigin = Position(x = 0f, y = 0f, z = 0f),
            )
        }

        characterSceneView.addChild(modelNode)


        val tab1 = PotDetailTab1Fragment()
        val tab2 = PotDetailTab2Fragment()
        val tab3 = PotDetailTab3Fragment()
        val tab4 = PotDetailTab4Fragment()
        val tab5 = PotDetailTab5Fragment()

        var tabBtn1 = view.findViewById<ImageButton>(R.id.tabBtn1)
        var tabBtn2 = view.findViewById<ImageButton>(R.id.tabBtn2)
        var tabBtn3 = view.findViewById<ImageButton>(R.id.tabBtn3)
        var tabBtn4 = view.findViewById<ImageButton>(R.id.tabBtn4)
        var tabBtn5 = view.findViewById<ImageButton>(R.id.tabBtn5)
        tabBtn1.setOnClickListener {
            childFragmentManager.beginTransaction().replace(R.id.tab_container, tab1).commit()
        }
        tabBtn2.setOnClickListener {
            childFragmentManager.beginTransaction().replace(R.id.tab_container, tab2).commit()
        }
        tabBtn3.setOnClickListener {
            childFragmentManager.beginTransaction().replace(R.id.tab_container, tab3).commit()
        }
        tabBtn4.setOnClickListener {
            childFragmentManager.beginTransaction().replace(R.id.tab_container, tab4).commit()
        }
        tabBtn5.setOnClickListener {
            childFragmentManager.beginTransaction().replace(R.id.tab_container, tab5).commit()
        }
    }

}