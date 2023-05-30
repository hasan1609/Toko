package com.go4sumbergedang.toko.model

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class ProdukPost(
    val resto_id: String? = null,
    val nama_makanan: String? = null,
    val harga: Double? = null,
    val keterangan: String? = null,
    val kategori: String? = null,
    val foto_makanan: MultipartBody.Part? = null
)