package com.example.arcore.ui.main.fragment.camera

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import com.example.arcore.R
import com.example.arcore.ui.core.BaseFragment
import com.example.arcore.util.permission.CameraPermissionHelper
import com.google.ar.core.*
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import kotlinx.android.synthetic.main.camera_fragment.*
import kotlinx.android.synthetic.main.lable_view.view.*
import java.io.IOException

class CameraFragment : BaseFragment(R.layout.camera_fragment) {

    private var session: Session? = null
    private var shouldRequestArCoreInstall = true

    override fun initView(savedInstanceState: Bundle?) {
        initSceneView()
    }

    override fun onResume() {
        super.onResume()
        CameraPermissionHelper.requestPermissionIfNeeded(requireActivity(), false) {
            initCore()
        }
    }

    private fun initSceneView() {
        arSceneCameraFragment.scene.addOnUpdateListener {
            val frame = arSceneCameraFragment.arFrame
            val updatedAugmentedImages = frame!!.getUpdatedTrackables(AugmentedImage::class.java)
            for (augmentedImage in updatedAugmentedImages) {
                if (augmentedImage.trackingState == TrackingState.TRACKING) {
                    if (augmentedImage.name == "Nature" || augmentedImage.name == "Flowers") {
                        setLabel(augmentedImage)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initCore() {
        if (session == null) {
            checkArCoreInstall {
                arSceneCameraFragment.setupSession(
                    Session(
                        requireContext(),
                        setOf(Session.Feature.SHARED_CAMERA)
                    ).apply {
                        this.configure(createSessionConfig(this))
                    })
            }
        }
        arSceneCameraFragment?.session?.resume()
        arSceneCameraFragment?.resume()
    }

    private fun createSessionConfig(session: Session) = Config(session).apply {
        focusMode = Config.FocusMode.AUTO
        updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        lightEstimationMode = Config.LightEstimationMode.DISABLED
        createAugmentedDB(this, session)
    }

    private fun createAugmentedDB(config: Config, session: Session) {
        val nature = loadAugmentedImage("test.jpg")
        val flowers = loadAugmentedImage("test2.jpg")
        val augmentedImageDatabase = AugmentedImageDatabase(session)
        augmentedImageDatabase.addImage("Nature", nature)
        augmentedImageDatabase.addImage("Flowers", flowers)
        config.augmentedImageDatabase = augmentedImageDatabase
    }

    // TODO refactor
    private fun loadAugmentedImage(name: String): Bitmap? = try {
        requireActivity().assets.open(name).use { `is` ->
            BitmapFactory.decodeStream(`is`)
        }
    } catch (e: IOException) {
        Log.e("Error", "IO exception loading augmented image bitmap.", e)
        null
    }

    private fun checkArCoreInstall(onInstalled: () -> Unit) {
        try {
            when (ArCoreApk.getInstance().requestInstall(requireActivity(), true)) {
                ArCoreApk.InstallStatus.INSTALLED -> onInstalled.invoke()
                else -> shouldRequestArCoreInstall = false
            }
        } catch (e: UnavailableUserDeclinedInstallationException) {
            // TODO handle (Show dialog with message)
        }
    }

    @SuppressLint("NewApi")
    private fun setLabel(augmentedImage: AugmentedImage) {
        try {
            ViewRenderable.builder()
                .setView(requireContext(), R.layout.lable_view)
                .build()
                .thenAccept {
                    it.isShadowCaster = false
                    it.isShadowReceiver = false
                    it.view.tvLabelViewName.text = augmentedImage.name
                    val pose = augmentedImage.centerPose
                    val anchorNode = AnchorNode(arSceneCameraFragment.session!!.createAnchor(pose))
                    anchorNode.name = augmentedImage.name
                    if (arSceneCameraFragment.scene.findInHierarchy { it.name == anchorNode.name } == null) {
                        val node = Node()
                        val p = Pose.makeTranslation(0.0f, 0.0f, 0.0f)
                        node.localPosition = Vector3(0.0f, 0.0f, -(augmentedImage.extentZ / 2))
                        node.localRotation = Quaternion(p.qx(), 90f, -90f, p.qw())
                        node.renderable = it
                        node.setParent(anchorNode)
                        arSceneCameraFragment.scene.addChild(anchorNode)
                    }
                }
        } catch (e: Exception) {
            e.toString()
        }
    }
}