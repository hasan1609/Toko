package com.go4sumbergedang.toko.webservice

import com.go4sumbergedang.toko.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login/resto")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("fcm") fcm: String
    ): Call<ResponseLogin>

    // update password
    @FormUrlEncoded
    @POST("update-password")
    fun updatePassword(
        @Field("id") id: String,
        @Field("current_password") current_password: String,
        @Field("password") password: String,
    ): Call<ResponsePostData>

    @GET("resto/{id}")
    fun getToko(
        @Path("id") id: String
    ): Call<ResponseToko>

    @GET("status/resto/{id}")
    fun getStatusToko(
        @Path("id") id: String
    ): Call<ResponseStatus>

    @FormUrlEncoded
    @POST("status/resto/{id}")
    fun updateStatusToko(
        @Path("id") id: String,
        @Field("param") param: String
    ): Call<ResponseStatus>

    @Multipart
    @POST("resto/{id}")
    fun updateTokoWithFoto(
        @Path("id") id: String,
        @Part("nama") nama: RequestBody,
        @Part("tlp") tlp: RequestBody,
        @Part("email") email: RequestBody,
        @Part("nik") nik: RequestBody,
        @Part("tempat_lahir") tempat_lahir: RequestBody,
        @Part("ttl") ttl: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("jam_buka") jam_buka: RequestBody,
        @Part("jam_tutup") jam_tutup: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("nama_resto") nama_resto: RequestBody,
        @Part foto: MultipartBody.Part?
    ): Call<ResponsePostData>

    @FormUrlEncoded
    @POST("resto/{id}")
    fun updateTokoNofoto(
        @Path("id") id: String,
        @Field("nama") nama: String,
        @Field("tlp") tlp: String,
        @Field("email") email: String,
        @Field("nik") nik: String,
        @Field("tempat_lahir") tempat_lahir: String,
        @Field("ttl") ttl: String,
        @Field("alamat") alamat: String,
        @Field("jam_buka") jam_buka: String,
        @Field("jam_tutup") jam_tutup: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("nama_resto") nama_resto: String,



    ): Call<ResponsePostData>

    @GET("count/kategori/{id}")
    fun getKategori(
        @Path("id") id: String
    ): Call<ResponseKategori>

    @GET("kategori/produk/{id}/{kategori}")
    fun getProduk(
        @Path("id") id: String,
        @Path("kategori") kategori: String
    ): Call<ResponseProduk>

    @POST("produk/del/{id}")
    fun hapusProduk(
        @Path("id") id: String
    ): Call<ResponsePostData>

    @Multipart
    @POST("produk")
    fun uploadProduk(
        @Part("user_id") user_id: RequestBody,
        @Part("nama_produk") nama_produk: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part("keterangan") keterangan: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part foto_produk: MultipartBody.Part?
    ): Call<ResponsePostData>

    @FormUrlEncoded
    @POST("status/produk/{id}")
    fun updateStatusProduk(
        @Path("id") id: String,
        @Field("param") param: String
    ): Call<ResponseStatus>

    @Multipart
    @POST("produk/{id}")
    fun updateProdukWithFoto(
        @Path("id") id: String,
        @Part("nama_produk") nama_produk: RequestBody,
        @Part("harga") harga: RequestBody,
        @Part("keterangan") keterangan: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part foto_produk: MultipartBody.Part?
    ): Call<ResponsePostData>

    @FormUrlEncoded
    @POST("produk/{id}")
    fun updateProdukNofoto(
        @Path("id") id: String,
        @Field("nama_produk") nama_produk: String,
        @Field("harga") harga: String,
        @Field("keterangan") keterangan: String,
        @Field("kategori") kategori: String,
    ): Call<ResponsePostData>

    @GET("review/resto/{id}")
    fun getReview(
        @Path("id") id: String
    ): Call<ResponseUlasan>

    @GET("order/resto/{id}")
    fun getOrderLog(
        @Path("id") id: String
    ): Call<ResponseOrderLog>

    @GET("order/detail/{id}")
    fun getDetailOrderLog(
        @Path("id") id: String
    ): Call<ResponseProdukOrderLog>

    @GET("notifikasi/{id}")
    fun getNotifikasiLog(
        @Path("id") id: String
    ): Call<ResponseNotifikasiLog>

    @POST("notifikasi/{id}")
    fun updateNotifikasiStatusLog(
        @Path("id") id: String
    ): Call<ResponsePostData>
}