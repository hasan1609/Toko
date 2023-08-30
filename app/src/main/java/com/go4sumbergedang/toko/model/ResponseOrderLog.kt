package com.go4sumbergedang.toko.model

import com.google.gson.annotations.SerializedName

data class ResponseOrderLog(

	@field:SerializedName("data")
	val data: List<DataItemOrder?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class DataItemOrder(

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("order")
	val order: DetailOrderModel? = null
)

