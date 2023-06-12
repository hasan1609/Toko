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

data class UlasanModel(

	@field:SerializedName("resto_id")
	val restoId: String? = null,

	@field:SerializedName("ulasan")
	val ulasan: String? = null,

	@field:SerializedName("driver_id")
	val driverId: String? = null,

	@field:SerializedName("user_cust")
	val userCust: UserCust? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("rating")
	val rating: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id_review")
	val idReview: String? = null,

	@field:SerializedName("customer_id")
	val customerId: String? = null,

	@field:SerializedName("customer")
	val customer: Customer? = null
)

data class UserCust(

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("id_user")
	val idUser: String? = null
)
