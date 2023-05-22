package com.chocobi.groot

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.opengl.GLES20
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log

import android.view.Menu
import android.view.MenuItem
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.utils.doOnApplyWindowInsets
import io.github.sceneview.utils.setFullScreen
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CharacterActivity : AppCompatActivity(R.layout.activity_character) {
    private val TAG = "CharacterActivity"
    lateinit var sceneView: ArSceneView

    private lateinit var GLBfile: String
    private lateinit var level: String
    private lateinit var potName: String
    private lateinit var potPlant: String
    private lateinit var captureBtn: ExtendedFloatingActionButton

    //    lateinit var loadingView: View
//    private lateinit var statusText: TextView

    //    lateinit var placeModelButton: ExtendedFloatingActionButton
//    lateinit var newModelButton: ExtendedFloatingActionButton
    lateinit var changeAnimationButton: ExtendedFloatingActionButton

    //    lateinit var stopAnimationButton: ExtendedFloatingActionButton
//    lateinit var resumeAnimationButton: ExtendedFloatingActionButton
    private var animationIdx = 0
//    private var time = System.currentTimeMillis()

    //    뒤로가기 조작
    override fun onBackPressed() {
        // 다른 Activity로 이동하는 코드를 여기에 작성합니다.
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // 현재 Activity를 종료하려는 경우
    }


    data class Model(
        val fileLocation: String,
        val scaleUnits: Float? = null,
        val placementMode: PlacementMode? = PlacementMode.INSTANT,
        val applyPoseRotation: Boolean? = true
    )

    val models =
        Model(
            fileLocation = "",
//            placementMode = PlacementMode.INSTANT,
//            applyPoseRotation = false
        )

    var modelNode: ArModelNode? = null
        //    var isLoading = false
        set(value) {
            field = value
//            loadingView.isGone = !value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_character)


        GLBfile = intent.getStringExtra("GLBfile").toString()
        level = intent.getStringExtra("level").toString()
        Log.d("CharacterActivity", "onCreate() ${level}레벨레벨")
        Log.d("CharacterActivity", "onCreate() ${GLBfile}파일 명")
        potName = intent.getStringExtra("potName").toString()
        potPlant = intent.getStringExtra("potPlant").toString()

        animationIdx =
            if (level == "0" || level == "1" || level == "2" || level == "3" || level == "4") 9 else 0

        setFullScreen(
            findViewById(R.id.rootView),
            fullScreen = true,
            hideSystemBars = false,
            fitsSystemWindows = false
        )

//        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar)?.apply {
//            doOnApplyWindowInsets { systemBarsInsets ->
//                (layoutParams as ViewGroup.MarginLayoutParams).topMargin = systemBarsInsets.top
//            }
//            title = ""
//        })
//        statusText = findViewById(R.id.statusText)
//        statusText.text = potName

        sceneView = findViewById<ArSceneView?>(R.id.sceneView).apply {
            onArTrackingFailureChanged = { reason ->
                Toast.makeText(context, "사물을 감지하지 못해 메인 화면으로 돌아갑니다", Toast.LENGTH_LONG).show()
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }
            isDepthOcclusionEnabled = false
        }

        changeAnimationButton = findViewById(R.id.changeAnimation)
        changeAnimationButton.apply {
            val bottomMargin = (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
            doOnApplyWindowInsets { systemBarsInsets ->
                (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
                    systemBarsInsets.bottom + bottomMargin
            }
            setOnClickListener { changeAnimation() }

        }

//        stopAnimationButton = findViewById(R.id.stopAnimation)
//        stopAnimationButton.apply {
//            val bottomMargin = (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
//            doOnApplyWindowInsets { systemBarsInsets ->
//                (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
//                    systemBarsInsets.bottom + bottomMargin
//            }
//            setOnClickListener { stopAnimtion() }
//
//        }
//
//        resumeAnimationButton = findViewById(R.id.resumeAnimation)
//        resumeAnimationButton.setOnClickListener {
//            resumeAnimation()
//        }

        captureBtn = findViewById(R.id.captureBtn)
        captureBtn.setOnClickListener {
            captureSceneView()
        }


        newModelNode()
//        placeModelNode()
    }

    private fun captureSceneView() {
        val bitmap = Bitmap.createBitmap(
            sceneView.width,
            sceneView.height,
            Bitmap.Config.ARGB_8888
        )
        PixelCopy.request(
            sceneView, bitmap, { result ->
                when (result) {
                    PixelCopy.SUCCESS -> {
                        val file = this@CharacterActivity.createExternalFile(
                            environment = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                Environment.DIRECTORY_SCREENSHOTS
                            } else {
                                Environment.DIRECTORY_PICTURES
                            }, extension = ".png"
                        )
                        file?.let { outputFile ->
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputFile.outputStream())

                            // Generate a content URI for the file
                            val fileUri = FileProvider.getUriForFile(
                                this@CharacterActivity,
                                "com.chocobi.groot.fileprovider",
                                outputFile
                            )

                            // Create an intent to share the image
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(fileUri, "image/png")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }

                            // Verify that the intent can be handled by an activity
                            if (intent.resolveActivity(packageManager) != null) {
                                startActivity(intent)
                            } else {
                                Log.d("CharacterActivity", "captureSceneView()  No app available to handle the image")
                            }
                        }
                    }
                    else -> Log.d("CharacterActivity", "captureSceneView() 실패")
                }
            }, Handler(
                HandlerThread("screenshot")
                    .apply { start() }.looper
            )
        )
    }

    private fun createExternalFile(environment: String, extension: String): File? {
        val storageDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getExternalFilesDir(environment)
        } else {
            Environment.getExternalStoragePublicDirectory(environment)
        }

        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMG_$timeStamp$extension"
            val file = File(storageDir, fileName)
            file.createNewFile()
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.a, menu)
//        return super.onCreateOptionsMenu(menu)
//    }

    fun changeAnimation() {
        modelNode?.stopAnimation(animationIdx)
        val maxIdx = modelNode?.animator?.animationCount!!
        ++animationIdx
        if (animationIdx == maxIdx) {
            animationIdx =
                if (level == "0" || level == "1" || level == "2" || level == "3" || level == "4") 9 else 0
        }
//        modelNode?.animator?.playbackSpeed =0.5f
        modelNode?.playAnimation(animationIdx)
//        val duration = modelNode?.animator?.getAnimationDuration(animationIdx)
//        val elapsedTimeInSeconds = duration?.times(2f) ?: 0f
//        modelNode?.animator?.applyAnimation(animationIdx, duration?.times(1f)!!)
//        Log.d("CharacterActivity", "changeAnimation() ${modelNode?.animator}")
//        Log.d("CharacterActivity", "changeAnimation() ${elapsedTimeInSeconds} ${duration}")
    }

    fun stopAnimtion() {
        modelNode?.stopAnimation(animationIdx)
    }

    fun resumeAnimation() {
        modelNode?.playAnimation(animationIdx)
    }

    fun newModelNode() {
        modelNode?.takeIf { !it.isAnchored }?.let {
            sceneView.removeChild(it)
            it.destroy()
        }
        val model = models
        modelNode = ArModelNode(
            placementMode = PlacementMode.INSTANT,
            instantAnchor = true,
            followHitPosition = false,
        ).apply {
            loadModelGlbAsync(
                glbFileLocation = GLBfile ?: model.fileLocation,
                autoAnimate = false,
                scaleToUnits = 0.2f,
                centerOrigin = Position(x = 0f, y = 0.0f, z = 0f)
                // Place the model origin at the bottom center
            ) {
                isPositionEditable = true
                isScaleEditable = false
                isRotationEditable = true
                followHitPosition = false
                instantAnchor = true

//                applyPosePosition = false

//                isScaleEditable = false
//            playAnimation(0, true)
            }
//            position = Position(x = 0.0f, y = 0f, z = 10f)
//            rotation = Rotation(x=10.0f, y = -10f, z=0f)
//            scale = Scale(1f)
            setReceiveShadows(false)
            setCastShadows(false)
            setScreenSpaceContactShadows(false)

//            onAnchorChanged = { anchor ->
//                placeModelButton.isGone = anchor != null
//            }
//            onHitResult = { node, _ ->
//                placeModelButton.isGone = !node.isTracking
//            }
        }
        modelNode?.anchor()


        sceneView.addChild(modelNode!!)
        // Select the model node by default (the model node is also selected on tap)
        sceneView.selectedNode = modelNode

    }
}