package com.agromall.clockin.data.source.remote

import com.agromall.clockin.data.dto.*
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @GET("search/repositories")
    fun searchRepos(@Query("page") page: Int, @Query("q") search: String): Single<ListResponse<Staff>>

    @GET("department")
    fun getDepatments(): Single<ListResponse<String>>

    @POST("auth/login")
    fun login(@Body req: LoginRequest): Single<ListResponse<String>>

    @Multipart
    @POST("staff")
    fun postStaff(@Part image: MultipartBody.Part,
                  @Part fpl: MultipartBody.Part,
                  @Part("last_name") lName: RequestBody,
                  @Part("first_name") fName: RequestBody,
                  @Part("email") email: RequestBody,
                  @Part("department") dept: RequestBody,
                  @Part("id") id: RequestBody?): Single<SingleResponse<String>>

    @GET("staff")
    fun getStaffs(): Single<ListResponse<StaffRes>>
}



//@Part("finger_print_image_r") fpr: MultipartBody.Part,