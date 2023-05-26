package com.go4sumbergedang.toko.webservice

import com.go4sumbergedang.toko.model.ResponseKategori
import com.go4sumbergedang.toko.model.ResponsePostData
import com.go4sumbergedang.toko.model.ResponseProduk
import com.go4sumbergedang.toko.model.ResponseStatusToko
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
}