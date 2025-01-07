package com.example.reportsapp.callback

import com.example.reportsapp.data.DataRecycler
import com.example.reportsapp.data.DataReports
import com.example.reportsapp.data.DataZero
import com.example.reportsapp.data.DeleteUserRequest
import com.example.reportsapp.data.DeleteUserResponse
import com.example.reportsapp.data.Reports
import com.example.reportsapp.data.UserUpdateRequest
import com.example.reportsapp.data.UserUpdateResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReportsCallable {

    @FormUrlEncoded
    @POST("a/login_reports.php")
    fun getLoginReports(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Reports>

    @FormUrlEncoded
    @POST("a/login_reports2.php")
    fun getForgetPasswordReports(@Field("email") email: String): Call<Reports>

    @FormUrlEncoded
    @POST("a/get_main_report.php")
    fun getMainReports(@Field("id") id: Int): Call<DataReports>

    @FormUrlEncoded
    @POST("a/recycler_group_zero.php")
    fun getMainZero(@Field("id") id: Int): Call<DataRecycler>

    @FormUrlEncoded
    @POST("a/recycler_group_one.php")
    fun getMainOne(@Field("id") id: Int): Call<DataRecycler>

    @FormUrlEncoded
    @POST("a/recycler_group_two.php")
    fun getMainTwo(@Field("id") id: Int): Call<DataRecycler>

    @FormUrlEncoded
    @POST("a/recycler_group_three.php")
    fun getMainThree(@Field("id") id: Int): Call<DataRecycler>

    @POST("a/update_profile_user.php")
    fun updateUser(@Body request: UserUpdateRequest): Call<UserUpdateResponse>

    @POST("a/delete_profile_user.php")
    fun deleteUser(@Body request: DeleteUserRequest): Call<DeleteUserResponse>
}