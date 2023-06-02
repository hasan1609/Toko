package com.go4sumbergedang.toko.ui.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class AddProdukActivity : AppCompatActivity(), AnkoLogger {

    lateinit var binding: ActivityAddProdukBinding
    var api = ApiClient.instance()
    lateinit var progressDialog: ProgressDialog
    private var selectedFile: Uri? = null

    companion object {
        private const val REQUEST_IMAGE_PICKER = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_produk)
        binding.lifecycleOwner = this
        progressDialog = ProgressDialog(this)
        setupToolbar()

        binding.addFoto.setOnClickListener {
            checkPermissionsAndOpenImagePicker()
        }
        binding.btnSimpan.setOnClickListener {
            uploadData()
        }
    }
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setTitle("Tambah Produk")
        binding.toolbar.setNavigationIcon(R.drawable.back)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun checkPermissionsAndOpenImagePicker() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_IMAGE_PICKER
            )
        } else {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        ImagePicker.with(this)
            .start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_IMAGE_PICKER) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImagePicker.REQUEST_CODE) {
                binding.foto.setImageURI(data?.data)
                selectedFile = data?.data
            }
        }
    }

    private fun uploadData(){
        val file = File(selectedFile?.path ?: "")
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
            toast("Jangan Kosongi kolom")
            return
        }
        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val imagePart : MultipartBody.Part = MultipartBody.Part.createFormData("foto_makanan", file.name, requestBody)
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
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        finish()
                    } else {
                        loading(false)
                        toast("Gagal menambahkan produk")
                    }
                } catch (e: Exception) {
                    loading(false)
                    info { "hasan ${e.message}" }
                    toast(e.message.toString())
                }
            }

            override fun onFailure(call: Call<ResponsePostData>, t: Throwable) {
                loading(false)
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })
    }

    fun loading(status : Boolean){
        if (status){
            progressDialog.setTitle("Loading...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
        }else{
            progressDialog.dismiss()
        }
    }
}