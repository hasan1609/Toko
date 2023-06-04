package com.go4sumbergedang.toko.ui.activity
import android.Manifest
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.databinding.ActivityAddProdukBinding
import com.go4sumbergedang.toko.model.ResponsePostData
import com.go4sumbergedang.toko.ui.fragment.BottomSheetFilePickerFragment
import com.go4sumbergedang.toko.webservice.ApiClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class AddProdukActivity : AppCompatActivity(), AnkoLogger, BottomSheetFilePickerFragment.FilePickerListener {

    private lateinit var binding: ActivityAddProdukBinding
    private lateinit var progressDialog: ProgressDialog
    var api = ApiClient.instance()
    private var selectedImageFile: File? = null

    private val PERMISSION_REQUEST_CODE = 100
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICKER = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_produk)
        binding.lifecycleOwner = this
        progressDialog = ProgressDialog(this)

        binding.addFoto.setOnClickListener {
            openImagePicker()
        }

        binding.btnSimpan.setOnClickListener {
            selectedImageFile?.let { file ->
                uploadData(file)
            }
        }
    }

    private fun openImagePicker() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                PERMISSION_REQUEST_CODE
            )
        } else {
            val bottomSheetFragment = BottomSheetFilePickerFragment()
            bottomSheetFragment.show(supportFragmentManager, "filePicker")
        }
    }

    override fun onFilePickerOptionSelected(option: Int) {
        when (option) {
            BottomSheetFilePickerFragment.OPTION_CAMERA -> {
                openCamera()
            }
            BottomSheetFilePickerFragment.OPTION_GALLERY -> {
                openGallery()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICKER)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICKER -> {
                    val imageUri = data?.data
                    binding.foto.setImageURI(imageUri)
                    selectedImageFile = File(getRealPathFromURI(imageUri))
                }
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    binding.foto.setImageBitmap(imageBitmap)
                    selectedImageFile = saveImageToInternalStorage(imageBitmap)
                }
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri?): String {
        var realPath = ""
        uri?.let {
            val cursor = contentResolver.query(it, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val idx = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    realPath = it.getString(idx)
                }
            }
        }
        return realPath
    }

    private fun saveImageToInternalStorage(image: Bitmap): File {
        val fileDir = filesDir
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val file = File(fileDir, fileName)
        FileOutputStream(file).use { outputStream ->
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
        }
        return file
    }

    private fun uploadData(file: File) {
        val namaProduk = binding.edtNama.text.toString()
        val hargaProduk = binding.edtHarga.text.toString()
        val keteranganProduk = binding.edtKeterangan.text.toString()
        val kategori = when (binding.rgKategori.checkedRadioButtonId) {
            binding.rbMakanan.id -> "makanan"
            binding.rbMinuman.id -> "minuman"
            binding.rbLainnya.id -> "lainnya"
            else -> ""
        }

        if (namaProduk.isEmpty() || hargaProduk.isEmpty() || kategori.isEmpty()) {
            Toast.makeText(this, "Jangan kosongi kolom", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val imagePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "foto_makanan",
            file.name,
            requestBody
        )
        val requestrestoId: RequestBody = RequestBody.create(
            MediaType.parse("text/plain"),
            "c9927b1a-a334-4bd8-9633-62fb9b843b85"
        )
        val namaProdukBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), namaProduk)
        val hargaProdukBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), hargaProduk)
        val keteranganProdukBody: RequestBody =
            RequestBody.create(MediaType.parse("text/plain"), keteranganProduk)
        val kategoriBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), kategori)

        loading(true)
        api.uploadProduk(
            requestrestoId,
            namaProdukBody,
            hargaProdukBody,
            keteranganProdukBody,
            kategoriBody,
            imagePart
        ).enqueue(object : Callback<ResponsePostData> {
            override fun onResponse(
                call: Call<ResponsePostData>,
                response: Response<ResponsePostData>
            ) {
                if (response.isSuccessful) {
                    loading(false)
                    finish()
                } else {
                    loading(false)
                    toast("Gagal menambahkan produk")
                }
            }

            override fun onFailure(call: Call<ResponsePostData>, t: Throwable) {
                loading(false)
                toast("Terjadi kesalahan")
                Log.e("AddProdukActivity", "Error: ${t.localizedMessage}")
            }
        })
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            progressDialog.setMessage("Tunggu sebentar...")
            progressDialog.setCancelable(false)
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }
}
