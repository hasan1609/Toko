package com.go4sumbergedang.toko.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ResponseKategori(

	@field:SerializedName("data")
	val data: List<KategoriModel?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class KategoriModel(

	@field:SerializedName("kategori")
	val kategori: String? = null,

	@field:SerializedName("hasil")
	val hasil: Int? = null
)
