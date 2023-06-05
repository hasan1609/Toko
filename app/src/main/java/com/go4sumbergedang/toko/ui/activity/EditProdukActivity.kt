package com.go4sumbergedang.toko.ui.activity

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.databinding.ActivityAddProdukBinding
import com.go4sumbergedang.toko.model.ProdukModel
import com.go4sumbergedang.toko.model.ResponsePostData
import com.go4sumbergedang.toko.ui.fragment.BottomSheetFilePickerFragment
import com.go4sumbergedang.toko.webservice.ApiClient
import com.google.gson.Gson
import com.squareup.picasso.Picasso
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

class EditProdukActivity : AppCompatActivity(), AnkoLogger, BottomSheetFilePickerFragment.FilePickerListener {

    lateinit var binding: ActivityAddProdukBinding
    lateinit var produk: ProdukModel
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
        val gson = Gson()
        produk =
            gson.fromJson(intent.getStringExtra("detail"), ProdukModel::class.java)
        setupToolbar()

        binding.edtNama.setText(produk.namaMakanan)
        binding.edtHarga.setText(produk.harga)
        if (produk.keterangan != null){
            binding.edtKeterangan.setText(produk.keterangan.toString())
        }
        when (produk.kategori) {
            "makanan" -> binding.rgKategori.check(binding.rbMakanan.id)
            "minuman" -> binding.rgKategori.check(binding.rbMinuman.id)
            "lainnya" -> binding.rgKategori.check(binding.rbLainnya.id)
        }
        val urlImage = getString(R.string.urlImage)
        val fotoMakanan = produk.fotoMakanan.toString()
        Picasso.get()
            .load(urlImage+fotoMakanan)
            .into(binding.foto)

        binding.addFoto.setOnClickListener {
            openImagePicker()
        }
        binding.btnSimpan.setOnClickListener {

            selectedImageFile?.let { file ->
                uploadDataWithfoto(file, produk.idMakanan.toString())
            }?: uploadData(produk.idMakanan.toString())
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

    private fun uploadDataWithfoto(file: File, idMakanan: String) {
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
        val namaProdukBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), namaProduk)
        val hargaProdukBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), hargaProduk)
        val keteranganProdukBody: RequestBody =
            RequestBody.create(MediaType.parse("text/plain"), keteranganProduk)
        val kategoriBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), kategori)

        loading(true)
        api.updateProdukWithFoto(
            idMakanan,
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
    private fun uploadData(idMakanan: String) {
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
        loading(true)
        api.updateProdukNofoto(
            idMakanan,
            namaProduk,
            hargaProduk,
            keteranganProduk,
            kategori,
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

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setTitle("Ubah Produk")
        binding.toolbar.setNavigationIcon(R.drawable.back)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
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