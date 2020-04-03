package com.example.arcore.ui.main.fragment.camera

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.arcore.R
import com.example.arcore.data.local.entity.ImageEntity
import com.example.arcore.ui.core.BaseFragment
import com.example.arcore.ui.main.fragment.viewmodel.ImagesViewModel
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

    private val viewModel: ImagesViewModel by viewModels(
        factoryProducer = {
            ViewModelProvider.AndroidViewModelFactory(
                requireActivity().application
            )
        })

    private var session: Session? = null
    private var shouldRequestArCoreInstall = true

    override fun initView(savedInstanceState: Bundle?) {
        initSceneView()
    }

    override fun onResume() {
        super.onResume()
        CameraPermissionHelper.requestPermissionIfNeeded(requireActivity(), false) {
            initCore()
            subscribeObservables()
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

    private fun subscribeObservables() {
        viewModel.imagesData.observe(viewLifecycleOwner, Observer {
            session?.let { session ->
                Thread {
                    session.configure(createSessionConfig(session).apply {
                        this.augmentedImageDatabase = createAugmentedDB(it, session)
                    })
                }.start()
            }
        })
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
        }

    private fun createAugmentedDB(
        images: List<ImageEntity>,
        session: Session
    ): AugmentedImageDatabase {
        val db = AugmentedImageDatabase(session)
        images.forEach { image ->
            try {
                db.addImage(
                    image.name.toString(),
                    BitmapFactory.decodeStream(
                        requireContext().contentResolver.openInputStream(image.image)
                    )
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return db
    }

    private fun checkArCoreInstall(onInstalled: () -> Unit) {
        try {
            when (ArCoreApk.getInstance().requestInstall(requireActivity(), true)) {
                ArCoreApk.InstallStatus.INSTALLED -> onInstalled.invoke()
                else -> shouldRequestArCoreInstall = false
            }
        } catch (e: UnavailableUserDeclinedInstallationException) {
            e.printStackTrace()
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

        private const val TAG = "CameraFragment"
    }
}