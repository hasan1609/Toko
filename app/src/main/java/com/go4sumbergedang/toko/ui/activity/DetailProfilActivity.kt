package com.go4sumbergedang.toko.ui.activity

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.adapter.ProdukAdapter
import com.go4sumbergedang.toko.databinding.ActivityDetailProfilBinding
import com.go4sumbergedang.toko.model.ProdukModel
import com.go4sumbergedang.toko.model.ResponsePostData
import com.go4sumbergedang.toko.model.ResponseToko
import com.go4sumbergedang.toko.session.SessionManager
import com.go4sumbergedang.toko.ui.fragment.BottomSheetFilePickerFragment
import com.go4sumbergedang.toko.webservice.ApiClient
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import com.google.gson.Gson
import com.squareup.picasso.Picasso
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
import java.text.SimpleDateFormat
import java.util.*

class DetailProfilActivity : AppCompatActivity(), AnkoLogger, BottomSheetFilePickerFragment.FilePickerListener {
    lateinit var binding: ActivityDetailProfilBinding
    lateinit var sessionManager: SessionManager
    private lateinit var progressDialog: ProgressDialog
    var api = ApiClient.instance()
    private var selectedImageFile: File? = null
    var idDetail : String? = null
    private val PERMISSION_REQUEST_CODE = 100
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICKER = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_profil)
        binding.lifecycleOwner = this
        sessionManager = SessionManager(this)
        progressDialog = ProgressDialog(this)
        binding.edtNama.setText(sessionManager.getNamaToko())
        getData()
        setupToolbar()
        binding.edtJmbuka.setOnClickListener{
            showTimePicker("Pilih Jam Buka", 9, 0, binding.edtJmbuka)
        }
        binding.edtJmtutup.setOnClickListener{
            showTimePicker("Pilih Jam Tutup", 9, 0, binding.edtJmtutup)
        }
        binding.addFoto.setOnClickListener {
            openImagePicker()
        }

        binding.btnSimpan.setOnClickListener {
            selectedImageFile?.let { file ->
                uploadDataWithfoto(file, idDetail.toString())
            }?: uploadData(idDetail.toString())
        }
    }

    private fun getData() {
        loading(true)
        api.getToko(sessionManager.getId().toString()).enqueue(object : Callback<ResponseToko> {
            override fun onResponse(
                call: Call<ResponseToko>,
                response: Response<ResponseToko>
            ) {
                try {
                    if (response.isSuccessful) {
                        loading(false)
                        val data = response.body()
                        if (data!!.status == true) {
                            binding.edtEmail.text = data.data!!.email
                            binding.edtTlp.setText(data.data.tlp)
                            binding.edtAlamat.setText(data.data.detailResto!!.alamat)
                            binding.edtJmbuka.text = data.data.detailResto.jamBuka
                            binding.edtJmtutup.text = data.data.detailResto.jamTutup
                            idDetail = data.data.detailResto.idDetail
                            val urlImage = getString(R.string.urlImage)
                            if(data.data.detailResto.foto == null){
                                Picasso.get()
                                    .load("$urlImage/public/images/no_image.png")
                                    .into(binding.foto)
                            }else{
                                Picasso.get()
                                    .load(urlImage+data.data.detailResto.foto.toString())
                                    .into(binding.foto)
                            }
                        }
                    } else {
                        loading(false)
                        toast("gagal mendapatkan response")
                    }
                } catch (e: Exception) {
                    loading(false)
                    info { "hasan ${e.message}" }
                    toast(e.message.toString())
                }
            }
            override fun onFailure(call: Call<ResponseToko>, t: Throwable) {
                loading(false)
                info { "hasan ${t.message}" }
                toast(t.message.toString())
            }
        })

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

    private fun showTimePicker(title: String, hour: Int, minute: Int, textView: TextView) {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText(title)
            .build()
        timePicker.addOnPositiveButtonClickListener {
            val selectedHour = timePicker.hour
            val selectedMinute = timePicker.minute
            val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            textView.text = selectedTime
        }
        textView.setOnClickListener {
            timePicker.show(supportFragmentManager, title)
        }
    }

    private fun uploadDataWithfoto(file: File, idToko: String) {
        val nama = binding.edtNama.text.toString()
        val tlp = binding.edtTlp.text.toString()
        val alamat = binding.edtAlamat.text.toString()
        val jamBuka = binding.edtJmbuka.text.toString()
        val jamTutup = binding.edtJmtutup.text.toString()

        if (nama.isEmpty() || tlp.isEmpty() || alamat.isEmpty()) {
            Toast.makeText(this, "Jangan kosongi kolom", Toast.LENGTH_SHORT).show()
            return
        }

        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val imagePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "foto",
            file.name,
            requestBody
        )
        val namaBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), nama)
        val tlpBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), tlp)
        val alamatBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), alamat)
        val jamBukaBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), jamBuka)
        val jamTutupBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), jamTutup)

        loading(true)
        api.updateTokoWithFoto(
            idToko,
            namaBody,
            alamatBody,
            tlpBody,
            jamBukaBody,
            jamTutupBody,
            imagePart
        ).enqueue(object : Callback<ResponsePostData> {
            override fun onResponse(
                call: Call<ResponsePostData>,
                response: Response<ResponsePostData>
            ) {
                if (response.isSuccessful) {
                    loading(false)
                    toast("Berhasil diubah")
                    finish()
                } else {
                    loading(false)
                    toast("Gagal mengubah data")
                }
            }

            override fun onFailure(call: Call<ResponsePostData>, t: Throwable) {
                loading(false)
                toast("Terjadi kesalahan")
            }
        })
    }

    private fun uploadData(idToko: String) {
        val nama = binding.edtNama.text.toString()
        val tlp = binding.edtTlp.text.toString()
        val alamat = binding.edtAlamat.text.toString()
        val jamBuka = binding.edtJmbuka.text.toString()
        val jamTutup = binding.edtJmtutup.text.toString()

        if (nama.isEmpty() || tlp.isEmpty() || alamat.isEmpty()) {
            Toast.makeText(this, "Jangan kosongi kolom", Toast.LENGTH_SHORT).show()
            return
        }

        loading(true)
        api.updateTokoNofoto(
            idToko,
            nama,
            alamat,
            tlp,
            jamBuka,
            jamTutup
        ).enqueue(object : Callback<ResponsePostData> {
            override fun onResponse(
                call: Call<ResponsePostData>,
                response: Response<ResponsePostData>
            ) {
                if (response.isSuccessful) {
                    loading(false)
                    toast("Berhasil diubah")
                    finish()
                } else {
                    loading(false)
                    toast("Gagal mengubah data")
                    info(response)
                }
            }

            override fun onFailure(call: Call<ResponsePostData>, t: Throwable) {
                loading(false)
                toast("Terjadi kesalahan")
            }
        })
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setTitle("Detail Profil")
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