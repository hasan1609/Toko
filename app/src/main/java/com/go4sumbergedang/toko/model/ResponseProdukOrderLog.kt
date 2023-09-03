package com.go4sumbergedang.toko.model

import com.google.gson.annotations.SerializedName

data class ResponseProdukOrderLog(

	@field:SerializedName("data")
	val data: List<ProdukOrderLogModel?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null,

	@field:SerializedName("totalJumlah")
	val totalJumlah: Int? = null
)


