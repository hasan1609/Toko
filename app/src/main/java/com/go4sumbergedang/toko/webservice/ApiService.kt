package com.go4sumbergedang.toko.webservice

import com.go4sumbergedang.toko.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("status/resto/{id}")
    fun getStatusToko(
        @Path("id") id: String
    ): Call<ResponseStatusToko>

    @FormUrlEncoded
    @POST("status/resto/{id}")
    fun updateStatusToko(
        @Path("id") id: String,
        @Field("param") param: String
    ): Call<ResponseStatusToko>

    @GET("count/kategori/{id}")
    fun getKategori(
        @Path("id") id: String
    ): Call<ResponseKategori>

    @GET("kategori/produk/{id}/{kategori}")
    fun getProduk(
        @Path("id") id: String,
        @Path("kategori") kategori: String
    ): Call<ResponseProduk>

    @POST("makanan/del/{id}")
    fun hapusProduk(
        @Path("id") id: String
    ): Call<ResponsePostData>

    @Multipart
    @POST("makanan")
    fun uploadProduk(
        @Part("resto_id") restoId: RequestBody,
        @Part("nama_makanan") namaMakanan: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part("keterangan") keterangan: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part foto_makanan: MultipartBody.Part
    ): Call<ResponsePostData>
}