package com.go4sumbergedang.toko.ui.activity

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
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
import java.io.ByteArrayOutputStream
import java.io.File

class AddProdukActivity : AppCompatActivity(), AnkoLogger{

    lateinit var binding: ActivityAddProdukBinding
    val sheet = BottomSheetFilePickerFragment()
    var api = ApiClient.instance()
    private var selectedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_produk)
        binding.lifecycleOwner = this
        setupToolbar()

        binding.addFoto.setOnClickListener {
            sheet.setOnImageSelectedListener { bitmap ->
                selectedBitmap = bitmap
                binding.foto.setImageBitmap(bitmap)
            }
            sheet.show(this.supportFragmentManager, "BottomSheetFilePickerFragment")
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

    private fun uploadData(){
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

        val file = createTempFile()
        file.outputStream().use { outputStream ->
            selectedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }

        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val filePart = MultipartBody.Part.createFormData("foto", file.name, requestFile)


        val requestrestoId: RequestBody = RequestBody.create(
            MediaType.parse("text/plain"),
            "c9927b1a-a334-4bd8-9633-62fb9b843b85"
        )
        val namaProdukBody = RequestBody.create(MediaType.parse("text/plain"), namaProduk)
        val hargaProdukBody = RequestBody.create(MediaType.parse("text/plain"), hargaProduk)
        val keteranganProdukBody =
            RequestBody.create(MediaType.parse("text/plain"), keteranganProduk)
        val kategoriBody = RequestBody.create(MediaType.parse("text/plain"), kategori)

        if (filePart != null) {
            api.uploadProduk(
                requestrestoId,
                namaProdukBody,
                hargaProdukBody,
                keteranganProdukBody,
                kategoriBody,
                filePart
            ).enqueue(object : Callback<ResponsePostData> {
                override fun onResponse(
                    call: Call<ResponsePostData>,
                    response: Response<ResponsePostData>
                ) {
                    try {
                        if (response.isSuccessful) {
                            startActivity<DataProdukActivity>()
                        } else {
                            startActivity<DataProdukActivity>()
                        }
                    } catch (e: Exception) {
                        info { "hasan ${e.message}" }
                        toast(e.message.toString())
                    }
                }

                override fun onFailure(call: Call<ResponsePostData>, t: Throwable) {
                    info { "hasan ${t.message}" }
                    toast(t.message.toString())                }
            })
        }
    }

    private fun createTempFile(): File {
        val file = File(cacheDir, "temp_image.jpg")
        file.createNewFile()
        return file
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val CAMERA_REQUEST_CODE = 200
    }
}
