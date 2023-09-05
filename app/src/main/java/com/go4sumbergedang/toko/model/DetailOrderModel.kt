package com.go4sumbergedang.toko.model

import com.google.gson.annotations.SerializedName

data class DetailOrderModel(

    @field:SerializedName("detail_driver")
    val detailDriver: DetailDriverModel? = null,

    @field:SerializedName("driver_id")
    val driverId: String? = null,

    @field:SerializedName("produk_order")
    val produkOrder: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("biaya_pesanan")
    val biayaPesanan: String? = null,

    @field:SerializedName("resto_id")
    val restoId: String? = null,

    @field:SerializedName("alamat_tujuan")
    val alamatTujuan: String? = null,

    @field:SerializedName("alamat_dari")
    val alamatDari: String? = null,

    @field:SerializedName("id_order")
    val idOrder: String? = null,

    @field:SerializedName("total")
    val total: String? = null,

    @field:SerializedName("latitude_tujuan")
    val latitudeTujuan: String? = null,

    @field:SerializedName("kategori")
    val kategori: String? = null,

    @field:SerializedName("latitude_dari")
    val latitudeDari: String? = null,

    @field:SerializedName("ongkos_kirim")
    val ongkosKirim: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("driver")
    val driver: UserModel? = null,

    @field:SerializedName("customer")
    val customer: UserModel? = null,

    @field:SerializedName("detail_customer")
    val detailCustomer: DetailCustomerModel? = null,

    @field:SerializedName("customer_id")
    val customerId: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("longitude_tujuan")
    val longitudeTujuan: String? = null,

    @field:SerializedName("longitude_dari")
    val longitudeDari: String? = null
)