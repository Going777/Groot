/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chocobi.groot.mlkit.kotlin.ml

import android.content.pm.PackageManager
import android.opengl.Matrix
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.ar.core.Anchor
import com.google.ar.core.Coordinates2d
import com.google.ar.core.Frame
import com.google.ar.core.TrackingState
import com.chocobi.groot.mlkit.java.common.helpers.DisplayRotationHelper
import com.chocobi.groot.mlkit.java.common.samplerender.Framebuffer
import com.chocobi.groot.mlkit.java.common.samplerender.SampleRender
import com.chocobi.groot.mlkit.java.common.samplerender.arcore.BackgroundRenderer
import com.chocobi.groot.mlkit.kotlin.ml.classification.DetectedObjectResult
import com.chocobi.groot.mlkit.kotlin.ml.classification.GoogleCloudVisionDetector
import com.chocobi.groot.mlkit.kotlin.ml.classification.MLKitObjectDetector
import com.chocobi.groot.mlkit.kotlin.ml.classification.ObjectDetector
import com.chocobi.groot.mlkit.kotlin.ml.render.LabelRender
import com.chocobi.groot.mlkit.kotlin.ml.render.PointCloudRender
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.NotYetAvailableException
import java.util.Collections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


import com.chocobi.groot.mlkit.java.common.samplerender.Mesh
import com.chocobi.groot.mlkit.java.common.samplerender.Shader
import com.chocobi.groot.mlkit.java.common.samplerender.Texture
import com.chocobi.groot.mlkit.java.common.samplerender.arcore.SpecularCubemapFilter
import com.chocobi.groot.mlkit.kotlin.ml.render.PawnRender
import com.google.ar.core.Trackable
import java.io.IOException
import kotlin.math.log

/** Renders the ML application into using our sample Renderer. */
class AppRenderer(val activity: ArActivity) : DefaultLifecycleObserver, SampleRender.Renderer {
    companion object {
        val TAG = "MLAppRenderer"

    }

    lateinit var view: ArActivityView
    private val coroutineScope = MainScope()

    val displayRotationHelper = DisplayRotationHelper(activity)

    // Rendering components
    lateinit var backgroundRenderer: BackgroundRenderer
    val pointCloudRender = PointCloudRender()
    val labelRenderer = LabelRender()
//    val pawnRenderer = PawnRender()

    // Virtual object (ARCore pawn)
    lateinit var virtualSceneFramebuffer: Framebuffer
    lateinit var dfgTexture: Texture
    lateinit var cubemapFilter: SpecularCubemapFilter
    lateinit var virtualObjectMesh: Mesh
    lateinit var virtualObjectShader: Shader
    lateinit var virtualObjectAlbedoTexture: Texture
    lateinit var virtualObjectAlbedoInstantPlacementTexture: Texture
    val modelMatrix = FloatArray(16)
    val modelViewMatrix = FloatArray(16)
    val modelViewProjectionMatrix = FloatArray(16)




    // Matrices for reuse in order to prevent reallocations every frame.
    val viewMatrix = FloatArray(16)
    val projectionMatrix = FloatArray(16)
    val viewProjectionMatrix = FloatArray(16)

    val arLabeledAnchors = Collections.synchronizedList(mutableListOf<ARLabeledAnchor>())
    var scanButtonWasPressed = false

    val mlKitAnalyzer = MLKitObjectDetector(activity)
    val gcpAnalyzer = run {
        // API key used to authenticate with Google Cloud Vision API. See README for steps on how to
        // obtain a valid API key.
        val applicationInfo =
            activity.packageManager.getApplicationInfo(
                activity.packageName,
                PackageManager.GET_META_DATA
            )
        val apiKey = applicationInfo.metaData.getString("com.chocobi.groot.mlkit.kotlin.ml.API_KEY")
        if (apiKey == null) null else GoogleCloudVisionDetector(activity, apiKey)
    }

    var currentAnalyzer: ObjectDetector = gcpAnalyzer ?: mlKitAnalyzer

    override fun onResume(owner: LifecycleOwner) {
        displayRotationHelper.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        displayRotationHelper.onPause()
    }

    /** Binds UI elements for ARCore interactions. */
    fun bindView(view: ArActivityView) {
        Log.d(TAG, "bindView")
        this.view = view

        view.scanButton.setOnClickListener {
            // frame.acquireCameraImage is dependent on an ARCore Frame, which is only available in
            // onDrawFrame.
            // Use a boolean and check its state in onDrawFrame to interact with the camera image.
            scanButtonWasPressed = true
            view.setScanningActive(true)
            hideSnackbar()
        }

        val gcpConfigured = gcpAnalyzer != null
        val configuredAnalyzer = gcpAnalyzer ?: mlKitAnalyzer
        view.useCloudMlSwitch.setOnCheckedChangeListener { _, isChecked ->
            currentAnalyzer = if (isChecked) configuredAnalyzer else mlKitAnalyzer
        }

        view.useCloudMlSwitch.isChecked = gcpConfigured
        view.useCloudMlSwitch.isEnabled = gcpConfigured
        currentAnalyzer = if (gcpConfigured) configuredAnalyzer else mlKitAnalyzer
//
//        if (!gcpConfigured) {
//            showSnackbar(
//                "Google Cloud Vision isn't configured (see README). The Cloud ML switch will be disabled."
//            )
//        }

        view.resetButton.setOnClickListener {
            synchronized(arLabeledAnchors) { arLabeledAnchors.clear() }
            view.resetButton.isEnabled = false
            hideSnackbar()
        }
    }

    override fun onSurfaceCreated(render: SampleRender) {
        Log.d(TAG, "onSurfaceCreated")
        backgroundRenderer =
            BackgroundRenderer(render).apply { setUseDepthVisualization(render, false) }
        pointCloudRender.onSurfaceCreated(render)
        labelRenderer.onSurfaceCreated(render)
//        pawnRenderer.onSurfaceCreated(render)


        // Virtual object to render (ARCore pawn)
        virtualSceneFramebuffer = Framebuffer(render, /*width=*/ 1, /*height=*/ 1)
        cubemapFilter =
            SpecularCubemapFilter(render, 16, 32)
        dfgTexture =
            Texture(
                render,
                Texture.Target.TEXTURE_2D,
                Texture.WrapMode.CLAMP_TO_EDGE,
                /*useMipmaps=*/ false
            )
        virtualObjectAlbedoTexture =
            Texture.createFromAsset(
                render,
                "models/T_Devil Tree_03.png",
//                "models/pawn_albedo.png",
                Texture.WrapMode.CLAMP_TO_EDGE,
                Texture.ColorFormat.SRGB
            )

        virtualObjectAlbedoInstantPlacementTexture =
            Texture.createFromAsset(
                render,
                "models/T_Devil Tree_03.png",
//                "models/pawn_albedo_instant_placement.png",
                Texture.WrapMode.CLAMP_TO_EDGE,
                Texture.ColorFormat.SRGB
            )

        val virtualObjectPbrTexture =
            Texture.createFromAsset(
                render,
                "models/T_Devil Tree_03.png",
//                "models/pawn_roughness_metallic_ao.png",
                Texture.WrapMode.CLAMP_TO_EDGE,
                Texture.ColorFormat.LINEAR
            )
        virtualObjectMesh = Mesh.createFromAsset(render, "models/pawn.obj")
//        virtualObjectMesh = Mesh.createFromAsset(render, "models/Devil_Tree_Candy.obj")
        virtualObjectShader =
            Shader.createFromAssets(
                render,
                "shaders/devil_tree.vert",
//                "shaders/environmental_hdr.vert",
                "shaders/devil_tree.frag",
//                "shaders/environmental_hdr.frag",
                mapOf("NUMBER_OF_MIPMAP_LEVELS" to cubemapFilter.numberOfMipmapLevels.toString())
            )
                .setTexture("u_AlbedoTexture", virtualObjectAlbedoTexture)
                .setTexture(
                    "u_RoughnessMetallicAmbientOcclusionTexture",
                    virtualObjectPbrTexture
                )
                .setTexture("u_Cubemap", cubemapFilter.filteredCubemapTexture)
                .setTexture("u_DfgTexture", dfgTexture)




    }

    override fun onSurfaceChanged(render: SampleRender?, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceChanged")
        displayRotationHelper.onSurfaceChanged(width, height)
        virtualSceneFramebuffer.resize(width, height)
    }

    var objectResults: List<DetectedObjectResult>? = null

    override fun onDrawFrame(render: SampleRender) {
        Log.d(TAG, "onDrawFrame")
        val session = activity.arCoreSessionHelper.session ?: return
        session.setCameraTextureNames(intArrayOf(backgroundRenderer.cameraColorTexture.textureId))

        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        displayRotationHelper.updateSessionIfNeeded(session)

        val frame =
            try {
                session.update()
            } catch (e: CameraNotAvailableException) {
                Log.e(TAG, "Camera not available during onDrawFrame", e)
                showSnackbar("Camera not available. Try restarting the app.")
                return
            }

        backgroundRenderer.updateDisplayGeometry(frame)
        backgroundRenderer.drawBackground(render)

        // Get camera and projection matrices.
        val camera = frame.camera
        camera.getViewMatrix(viewMatrix, 0)
        camera.getProjectionMatrix(projectionMatrix, 0, 0.01f, 100.0f)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // Handle tracking failures.
        if (camera.trackingState != TrackingState.TRACKING) {
            return
        }


//        depth 설정
        try {
            backgroundRenderer.setUseDepthVisualization(
                render,
                activity.depthSettings.depthColorVisualizationEnabled()
            )
            backgroundRenderer.setUseOcclusion(render, activity.depthSettings.useDepthForOcclusion())
        } catch (e: IOException) {
            Log.e(TAG, "Failed to read a required asset file", e)
//            showError("Failed to read a required asset file: $e")
            return
        }

        // Draw point cloud.
        frame.acquirePointCloud().use { pointCloud ->
//            포인트 클라우드 지움
//            pointCloudRender.drawPointCloud(render, pointCloud, viewProjectionMatrix)

//            추가사항
            Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        }

        // Frame.acquireCameraImage must be used on the GL thread.
        // Check if the button was pressed last frame to start processing the camera image.
        if (scanButtonWasPressed) {
            scanButtonWasPressed = false
            val cameraImage = frame.tryAcquireCameraImage()
            if (cameraImage != null) {
                // Call our ML model on an IO thread.
                coroutineScope.launch(Dispatchers.IO) {
                    val cameraId = session.cameraConfig.cameraId
                    val imageRotation =
                        displayRotationHelper.getCameraSensorToDisplayRotation(cameraId)
                    objectResults =
                        try {
                            currentAnalyzer.analyze(cameraImage, imageRotation)
                        } catch (exception: Exception) {
                            showSnackbar(
                                "Exception thrown analyzing input frame: " +
                                        exception.message +
                                        "\n" +
                                        "See adb log for details."
                            )
                            Log.e(TAG, "Exception thrown analyzing input frame", exception)
                            null
                        }
                    cameraImage.close()
                }
            }
        }

        /** If results were completed this frame, create [Anchor]s from model results. */
        val objects = objectResults
        if (objects != null) {
            objectResults = null
            Log.i(TAG, "$currentAnalyzer got objects: $objects")
            val anchors =
                objects.mapNotNull { obj ->
                    val anchor =
                        createAnchor(
                            obj.centerCoordinate.x.toFloat(),
                            obj.centerCoordinate.y.toFloat(),
                            frame
                        )
                            ?: return@mapNotNull null
                    Log.i(TAG, "Created anchor ${anchor.pose} from hit test")
                    ARLabeledAnchor(anchor, obj.label)
                }
            arLabeledAnchors.addAll(anchors)
            view.post {
                view.resetButton.isEnabled = arLabeledAnchors.isNotEmpty()
                view.setScanningActive(false)
                when {
                    objects.isEmpty() &&
                            currentAnalyzer == mlKitAnalyzer &&
                            !mlKitAnalyzer.hasCustomModel() ->
                        showSnackbar(
                            "화분이 보이지 않습니다." +
                                    " 해당 화분에서 루티를 찾아주세요."
                        )
//                    showSnackbar(
//                        "Default ML Kit classification model returned no results. " +
//                                "For better classification performance, see the README to configure a custom model."
//                    )

                    objects.isEmpty() -> showSnackbar("Classification model returned no results.")
                    anchors.size != objects.size ->
                        showSnackbar(
                            "Objects were classified, but could not be attached to an anchor. " +
                                    "Try moving your device around to obtain a better understanding of the environment."
                        )
                }
            }
        }

        render.clear(virtualSceneFramebuffer, 0f, 0f, 0f, 0f)
        // Draw labels at their anchor position.
        synchronized(arLabeledAnchors) {
            for (arDetectedObject in arLabeledAnchors) {
                val anchor = arDetectedObject.anchor
                if (anchor.trackingState != TrackingState.TRACKING) continue
//                labelRenderer.draw(
//                    render,
//                    viewProjectionMatrix,
//                    anchor.pose,
//                    camera.pose,
//                    arDetectedObject.label
//                )

                if (arDetectedObject.label == "Plant") {
//                    labelRenderer.draw(
//                        render,
//                        viewProjectionMatrix,
//                        anchor.pose,
//                        camera.pose,
//                        arDetectedObject.label
//                    )



                    Log.d(TAG, "plant 인지")

//                    캐릭터 띄우기
                    activity.goCharacter()


                    anchor.pose.toMatrix(modelMatrix, 0)
                    Log.d(TAG, modelMatrix.toString())
                    Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
                    Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0)


                    virtualObjectShader.setMat4("u_ModelView", modelViewMatrix)
                    virtualObjectShader.setMat4("u_ModelViewProjection", modelViewProjectionMatrix)
//                    val texture =
//                        if ((trackable as? InstantPlacementPoint)?.trackingMethod ==
//                            InstantPlacementPoint.TrackingMethod.SCREENSPACE_WITH_APPROXIMATE_DISTANCE
//                        ) {
//                            virtualObjectAlbedoInstantPlacementTexture
//                        } else {
//                            virtualObjectAlbedoTexture
//                        }
                    val texture = virtualObjectAlbedoInstantPlacementTexture
                    virtualObjectShader.setTexture("u_AlbedoTexture", texture)
//                    render.draw(virtualObjectMesh, virtualObjectShader, virtualSceneFramebuffer)

                }
                backgroundRenderer.drawVirtualScene(render, virtualSceneFramebuffer, 0.01f, 100f)

            }
        }
    }

    /**
     * Utility method for [Frame.acquireCameraImage] that maps [NotYetAvailableException] to `null`.
     */
    fun Frame.tryAcquireCameraImage() =
        try {
            acquireCameraImage()
        } catch (e: NotYetAvailableException) {
            null
        } catch (e: Throwable) {
            throw e
        }

    private fun showSnackbar(message: String): Unit =
        activity.view.snackbarHelper.showMessageWithDismiss(activity, message)

    private fun hideSnackbar() = activity.view.snackbarHelper.hide(activity)

    /** Temporary arrays to prevent allocations in [createAnchor]. */
    private val convertFloats = FloatArray(4)
    private val convertFloatsOut = FloatArray(4)

    /**
     * Create an anchor using (x, y) coordinates in the [Coordinates2d.IMAGE_PIXELS] coordinate space.
     */
    fun createAnchor(xImage: Float, yImage: Float, frame: Frame): Anchor? {
        Log.d(TAG, "createAnchor")
        // IMAGE_PIXELS -> VIEW
        convertFloats[0] = xImage
        convertFloats[1] = yImage
        frame.transformCoordinates2d(
            Coordinates2d.IMAGE_PIXELS,
            convertFloats,
            Coordinates2d.VIEW,
            convertFloatsOut
        )

        // Conduct a hit test using the VIEW coordinates
        val hits = frame.hitTest(convertFloatsOut[0], convertFloatsOut[1])
        val result = hits.getOrNull(0) ?: return null
        return result.trackable.createAnchor(result.hitPose)
    }
}

data class ARLabeledAnchor(val anchor: Anchor, val label: String)


//==========================================================
private data class WrappedAnchor(
    val anchor: Anchor,
    val trackable: Trackable,
)