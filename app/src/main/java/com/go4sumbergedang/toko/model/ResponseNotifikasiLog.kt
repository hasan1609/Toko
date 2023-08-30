package com.go4sumbergedang.toko.model

import com.google.gson.annotations.SerializedName

data class ResponseNotifikasiLog(

	@field:SerializedName("data")
	val data: List<NotifikasiLogModel?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)
