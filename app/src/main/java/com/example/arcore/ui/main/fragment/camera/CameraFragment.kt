package com.example.arcore.ui.main.fragment.camera

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import com.example.arcore.R
import com.example.arcore.data.local.entity.ImageEntity
import com.example.arcore.ui.core.BaseFragment
import com.example.arcore.util.permission.CameraPermissionHelper
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import kotlinx.android.synthetic.main.camera_fragment.*
import kotlinx.android.synthetic.main.lable_view.view.*

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

    override fun onPause() {
        super.onPause()
        arSceneCameraFragment?.session?.pause()
        arSceneCameraFragment?.pause()
    }

    private fun initSceneView() {
        arSceneCameraFragment.scene.addOnUpdateListener {
            arSceneCameraFragment.arFrame?.let {
                val updatedAugmentedImages = it.getUpdatedTrackables(AugmentedImage::class.java)
                for (augmentedImage in updatedAugmentedImages) {
                    if (augmentedImage.trackingState == TrackingState.TRACKING) {
                        setLabel(augmentedImage)
                    }
                }
            }
        }
    }

    private fun initCore() {
        if (session == null) {
            checkArCoreInstall {
                arSceneCameraFragment.setupSession(
                    Session(
                        requireContext(),
                        setOf(Session.Feature.SHARED_CAMERA)
                    ).apply {
                        this.configure(createSessionConfig(this))
                        session = this
                    })
            }
        }
        arSceneCameraFragment?.session?.resume()
        arSceneCameraFragment?.resume()
    }

    private fun createSessionConfig(session: Session) =
        Config(session).apply {
            focusMode = Config.FocusMode.AUTO
            updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            lightEstimationMode = Config.LightEstimationMode.DISABLED
            augmentedImageDatabase = createAugmentedDB(session)
        }

    private fun createAugmentedDB(session: Session): AugmentedImageDatabase {
        val images: ArrayList<ImageEntity> = arguments?.getParcelableArrayList(KEY_BUNDLE_IMAGES)
            ?: throw IllegalArgumentException("Images should not be null")
        val db = AugmentedImageDatabase(session)
        images.forEach { image ->
            db.addImage(
                image.name.toString(), BitmapFactory.decodeStream(
                    requireContext().contentResolver.openInputStream(image.image)
                )
            )
        }
        return db
    }

    private fun checkArCoreInstall(onInstalled: () -> Unit) {
        try {
            when (ArCoreApk.getInstance().requestInstall(requireActivity(), true)) {
                ArCoreApk.InstallStatus.INSTALLED -> onInstalled.invoke()
                else -> shouldRequestArCoreInstall = false
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
        }
    }

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

    companion object {

        private const val KEY_BUNDLE_IMAGES = "key_bundle_images"

        fun newInstance(images: ArrayList<ImageEntity>) = CameraFragment().apply {
            this.arguments = Bundle().apply {
                putParcelableArrayList(KEY_BUNDLE_IMAGES, images)
            }
        }
    }
}