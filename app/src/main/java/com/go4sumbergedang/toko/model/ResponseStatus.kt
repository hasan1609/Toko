package com.go4sumbergedang.toko.model

import com.google.gson.annotations.SerializedName

data class ResponseStatus(

	@field:SerializedName("data")
	val data: StatusModel? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class StatusModel(

	@field:SerializedName("status")
	val status: String? = null
)
