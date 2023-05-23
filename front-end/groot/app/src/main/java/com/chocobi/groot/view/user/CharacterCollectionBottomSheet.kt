package com.chocobi.groot.view.user

import android.annotation.SuppressLint
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chocobi.groot.R
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.utils.Color

class CharacterCollectionBottomSheet(context: Context): BottomSheetDialogFragment() {
    private var grwType: String? = null
    private var glbPath: String? = null
    private var modelNode: ModelNode? = null

    private lateinit var grwTypeText: TextView
    private lateinit var characterSceneView: SceneView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.bottom_sheet_character_collection, container, false)

        grwTypeText = view.findViewById(R.id.grwTypeText)
        characterSceneView = view.findViewById(R.id.characterSceneView)

        grwTypeText.text = grwType

        setCharacterSceneView()
        Log.d("CharacterCollectionBottomSheet","onCreateView() $grwType")
        Log.d("CharacterCollectionBottomSheet","onCreateView() $glbPath")
        return view
    }

    fun setData(grwType: String, glbPath: String) {
        // 데이터 처리 로직 작성
        this.grwType = grwType
        this.glbPath = glbPath
    }

    private fun setCharacterSceneView() {
        if (modelNode != null) {
            characterSceneView.removeChild(modelNode!!)
        }

        characterSceneView.backgroundColor = Color(255.0f, 255.0f, 255.0f, 0.0f)

        modelNode = ModelNode().apply {
            loadModelGlbAsync(
                glbFileLocation = glbPath!!,
                autoAnimate = false,
                scaleToUnits = 1.2f,
                centerOrigin = Position(x = 0f, y = 0f, z = 0f),
            ) {
                isScaleEditable = false
                isRotationEditable = false
            }
        }
        if (modelNode != null) {
            characterSceneView.addChild(modelNode!!)
        }
    }
}