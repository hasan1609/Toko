package com.go4sumbergedang.toko.model

import com.google.gson.annotations.SerializedName

data class ResponseUlasan(

	@field:SerializedName("data")
	val data: List<UlasanModel?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class Customer(

	@field:SerializedName("foto")
	val foto: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null
)

data class UserCust(

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("id_user")
	val idUser: String? = null
)
