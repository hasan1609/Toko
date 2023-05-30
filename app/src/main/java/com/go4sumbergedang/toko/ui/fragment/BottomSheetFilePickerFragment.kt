package com.go4sumbergedang.toko.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.databinding.BottomSheetFilePickBinding
import com.go4sumbergedang.toko.ui.activity.AddProdukActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.jetbrains.anko.AnkoLogger
import java.io.IOException

class BottomSheetFilePickerFragment : BottomSheetDialogFragment(), AnkoLogger {
    lateinit var binding: BottomSheetFilePickBinding
    private var onImageSelectedListener: ((Bitmap) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.bottom_sheet_file_pick,container,false)
        binding.lifecycleOwner = this
        binding.iconKamera.setOnClickListener {
            openCamera()
        }

        binding.iconFile.setOnClickListener {
            openGallery()
        }
        return binding.root
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let { onImageSelectedListener?.invoke(it) }
                }
                GALLERY_REQUEST_CODE -> {
                    val imageUri = data?.data
                    imageUri?.let {
                        try {
                            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                                MediaStore.Images.Media.getBitmap(
                                    requireActivity().contentResolver,
                                    imageUri
                                )
                            } else {
                                val source =
                                    ImageDecoder.createSource(requireActivity().contentResolver, imageUri)
                                ImageDecoder.decodeBitmap(source)
                            }
                            onImageSelectedListener?.invoke(bitmap)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            dismiss()
        }
    }

    fun setOnImageSelectedListener(listener: (Bitmap) -> Unit) {
        onImageSelectedListener = listener
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 1
        private const val GALLERY_REQUEST_CODE = 2
        private const val CAMERA_PERMISSION_REQUEST_CODE = 3
    }
}