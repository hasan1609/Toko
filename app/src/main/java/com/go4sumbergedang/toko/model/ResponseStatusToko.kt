package com.go4sumbergedang.toko.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ResponseStatusToko(

	@field:SerializedName("data")
	val data: StatusTokoModel? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class StatusTokoModel(

	@field:SerializedName("status")
	val status: String? = null
)
