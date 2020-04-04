package com.example.arcore.ui.main.fragment.list

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.arcore.R
import com.example.arcore.data.local.entity.ImageEntity
import com.example.arcore.ui.core.BaseFragment
import com.example.arcore.ui.main.fragment.list.adapter.ImagesAdapter
import com.example.arcore.ui.main.fragment.list.decoration.PaddingItemDecoration
import com.example.arcore.ui.main.fragment.viewmodel.ImagesViewModel
import com.example.arcore.util.permission.CameraPermissionHelper
import kotlinx.android.synthetic.main.images_list_fragment.*
import java.io.File

class ImagesListFragment : BaseFragment(R.layout.images_list_fragment),
    ImagesAdapter.OnItemClickListener {

    private val viewModel: ImagesViewModel by viewModels(factoryProducer = {
        ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application
        )
    })

    private var cameraImageUri: Uri? = null

    override fun initView(savedInstanceState: Bundle?) {
        initListeners()
        initRecyclerView()
        subscribeObservables()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PHOTO_GALLERY_REQUEST_CODE) {
                data?.data?.let {
                    viewModel.addImage(ImageEntity(it))
                }
            } else if (requestCode == PHOTO_CAMERA_REQUEST_CODE) {
                cameraImageUri?.let {
                    viewModel.addImage(ImageEntity(it))
                }
            }
        }
    }

    override fun onSaveClicked(image: ImageEntity) {
        viewModel.updateImage(image)
    }

    override fun onDeleteClicked(image: ImageEntity) {
        viewModel.deleteImage(image)
    }

    private fun initListeners() {
        fabListFragmentAddGallery?.setOnClickListener {
            fabListFragmentAdd.close(false)
            openPhotoPicker()
        }
        fabListFragmentAddCamera?.setOnClickListener {
            fabListFragmentAdd.close(false)
            handleImageFromCameraSelected()
        }
        fabListFragmentContinue?.setOnClickListener {
            navigation.navigateToCamera(ArrayList(viewModel.imagesData.value ?: emptyList()))
        }
    }

    private fun initRecyclerView() {
        rvListFragmentItems.adapter = ImagesAdapter(listener = this)
        rvListFragmentItems.addItemDecoration(
            PaddingItemDecoration(
                resources.getDimensionPixelOffset(R.dimen.default_margin_padding),
                resources.getDimensionPixelOffset(R.dimen.default_margin_padding),
                resources.getDimensionPixelOffset(R.dimen.default_margin_padding),
                resources.getDimensionPixelOffset(R.dimen.default_margin_padding)
            )
        )
    }

    private fun subscribeObservables() {
        viewModel.imagesData.observe(viewLifecycleOwner, Observer {
            (rvListFragmentItems.adapter as? ImagesAdapter)?.updateItems(it)
        })
    }

    private fun openPhotoPicker() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(
            Intent.createChooser(photoPickerIntent, ""),
            PHOTO_GALLERY_REQUEST_CODE
        )
    }

    private fun handleImageFromCameraSelected() {
        CameraPermissionHelper.requestPermissionIfNeeded(requireActivity(), true) {
            openCamera()
        }
    }

    private fun openCamera() {
        cameraImageUri = FileProvider.getUriForFile(
            requireContext(),
            "com.example.arcore.fileprovider",
            File("${requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/${System.currentTimeMillis()}.jpg")
        )
        startActivityForResult(
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            },
            PHOTO_CAMERA_REQUEST_CODE
        )
    }

    companion object {

        private const val PHOTO_GALLERY_REQUEST_CODE = 1
        private const val PHOTO_CAMERA_REQUEST_CODE = 2
    }
}