package com.chocobi.groot

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.chocobi.groot.data.PERMISSION_CAMERA
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
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

class CharacterActivity : AppCompatActivity(R.layout.activity_character) {
    private val TAG = "CharacterActivity"
    private val PERMISSION_WRITE_STORAGE = 4

    lateinit var sceneView: ArSceneView

    private lateinit var GLBfile: String
    private lateinit var level: String
    private lateinit var potName: String
    private lateinit var potPlant: String
    private lateinit var capture: ExtendedFloatingActionButton
    lateinit var changeAnimationButton: ExtendedFloatingActionButton
    private var animationIdx = 0

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
            fileLocation = "https://groot-a303-s3.s3.ap-northeast-2.amazonaws.com/assets/tree_2.glb",
        )

    var modelNode: ArModelNode? = null
        set(value) {
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mActivity = MainActivity()

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

        sceneView = findViewById<ArSceneView?>(R.id.sceneView).apply {
            onArTrackingFailureChanged = { reason ->
                Toast.makeText(context, "사물을 감지하지 못해 메인 화면으로 돌아갑니다", Toast.LENGTH_LONG).show()
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }
            isDepthOcclusionEnabled = false
        }
        changeAnimationButton = findViewById(R.id.changeAnimation)
        capture = findViewById(R.id.capture)
        changeAnimationButton.apply {
            val bottomMargin = (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
            doOnApplyWindowInsets { systemBarsInsets ->
                (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
                    systemBarsInsets.bottom + bottomMargin
            }
            setOnClickListener { changeAnimation() }

        }

        capture.setOnClickListener {
            Log.d("CharacterActivity","onCreate() 캐릭터 눌림")
//            MainActivity().requirePermissions(
//                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                PERMISSION_WRITE_STORAGE
//            )
            checkPermissionsAndCapture()
        }

        newModelNode()
    }
    private fun checkPermissionsAndCapture() {
        val permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permissionCheck = ContextCompat.checkSelfPermission(this, permission)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.d("CharacterActivity","checkPermissionsAndCapture() 캐릭터 권한 거부")
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_WRITE_STORAGE)
        } else {
            Log.d("CharacterActivity","checkPermissionsAndCapture() 캐릭터 권한 승인")
            captureAndSaveImage()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("CharacterActivity","onRequestPermissionsResult() 캐릭터")

        if (requestCode == PERMISSION_WRITE_STORAGE) {
            Log.d("CharacterActivity","onRequestPermissionsResult() $grantResults")
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureAndSaveImage()
            } else {
                Toast.makeText(this, "Permission denied. Cannot capture and save image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun captureAndSaveImage() {
        // 캡처할 View 객체
//        val viewToCapture = findViewById<View>(R.id.sceneView) // 캡처할 View ID로 변경하세요

        // View의 스크린샷 캡처
        val bitmap = Bitmap.createBitmap(sceneView.width, sceneView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        sceneView.draw(canvas)

        // 저장할 폴더 및 파일 경로 설정
        val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/groot"
        val fileName = "captured_image.jpg"
        val filePath = "$folderPath/$fileName"

        // 폴더 생성
        val folder = File(folderPath)
        if (!folder.exists()) {
            folder.mkdirs()
        }

        // 이미지 파일 저장
        try {
            FileOutputStream(filePath).use { outStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                Toast.makeText(this, "Image captured and saved: $filePath", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save image.", Toast.LENGTH_SHORT).show()
        }
    }

    fun changeAnimation() {
        modelNode?.stopAnimation(animationIdx)
        val maxIdx = modelNode?.animator?.animationCount!!
        ++animationIdx
        if (animationIdx == maxIdx) {
            animationIdx =
                if (level == "0" || level == "1" || level == "2" || level == "3" || level == "4") 9 else 0
        }
        modelNode?.playAnimation(animationIdx)
    }

    fun newModelNode() {
//        isLoading = true
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
            }
            setReceiveShadows(false)
            setCastShadows(false)
            setScreenSpaceContactShadows(false)
        }
        modelNode?.anchor()


        sceneView.addChild(modelNode!!)
        // Select the model node by default (the model node is also selected on tap)
        sceneView.selectedNode = modelNode

    }

    private fun captureSceneView() {
        val bitmap = Bitmap.createBitmap(sceneView.width, sceneView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        sceneView.draw(canvas)

        Log.d("CharacterActivity","captureSceneView()  $bitmap")

        // 비트맵을 저장하거나 다른 용도로 사용할 수 있습니다.
        // 예시: 저장하기
//        saveBitmap(bitmap)
    }

    private fun saveBitmap(bitmap: Bitmap) {
        val folderPath = Environment.getExternalStorageDirectory().absolutePath
        val filePath = "$folderPath/scene_capture.png"

        try {
            FileOutputStream(filePath).use { outStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                Toast.makeText(this, "Scene captured and saved: $filePath", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save scene capture.", Toast.LENGTH_SHORT).show()
        }
    }

//    private fun checkPermissionsAndCapture() {
//        val permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//
//        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
//            captureSceneView()
//        } else {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
//                // 권한 요청에 대한 설명을 표시할 수 있는 경우
//                showPermissionRationale()
//            } else {
//                // 권한 요청 대화 상자 표시
//                ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_WRITE_STORAGE)
//            }
//        }
//    }

    private fun showPermissionRationale() {
        // 권한 요청에 대한 설명을 표시하는 로직을 구현합니다.
        // AlertDialog 또는 다른 사용자 인터페이스 요소를 사용하여 권한에 대한 설명을 표시합니다.
        // 예시:
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("To capture scene view, the app needs permission to write external storage.")
            .setPositiveButton("OK") { _, _ ->
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_WRITE_STORAGE)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == PERMISSION_WRITE_STORAGE) {
//                captureSceneView()
////            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////            } else {
////                Toast.makeText(this, "Permission denied. Cannot capture scene view.", Toast.LENGTH_SHORT).show()
////            }
//        }
//    }
}