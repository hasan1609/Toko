package com.go4sumbergedang.toko.model

import com.google.gson.annotations.SerializedName

data class KategoriProdukModel(

    @field:SerializedName("kategori")
    val kategori: String? = null,

    @field:SerializedName("hasil")
    val hasil: Int? = null
)