package com.go4sumbergedang.toko.model

import com.google.gson.annotations.SerializedName

data class ResponseProduk(

	@field:SerializedName("data")
	val data: List<ProdukModel?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class ProdukModel(

	@field:SerializedName("resto_id")
	val restoId: String? = null,

	@field:SerializedName("keterangan")
	val keterangan: Any? = null,

	@field:SerializedName("terjual")
	val terjual: Any? = null,

	@field:SerializedName("id_makanan")
	val idMakanan: String? = null,

	@field:SerializedName("harga")
	val harga: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("foto_makanan")
	val fotoMakanan: String? = null,

	@field:SerializedName("nama_makanan")
	val namaMakanan: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("kategori")
	val kategori: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
