package com.go4sumbergedang.toko.model

import com.google.gson.annotations.SerializedName

data class ResponseLogin(

	@field:SerializedName("data")
	val data: UserModel? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null,

	@field:SerializedName("token")
	val token: String? = null
)

