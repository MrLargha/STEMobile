package ru.mrlargha.stemobile.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.mrlargha.stemobile.data.model.*

interface STEApi {
    @POST("login")
    fun login(@Query("vk_id") vk_id: String, @Query("password") password: String): Call<LoginServerReply>

    @POST("register")
    fun register(@Query("vk_id") vk_id: String, @Query("password") password: String): Call<LoginServerReply>

    @GET("info")
    fun getInfo(@Query("token") token: String): Call<LoginServerReply>

    @POST("add_substitution")
    fun insertSubstitution(@Query("token") token: String,
                           @Query("date") date: String,
                           @Query("cabinet") cabinet: String,
                           @Query("teacher") teacher: String,
                           @Query("subject") subject: String,
                           @Query("pair") pair: String,
                           @Query("group") group: String): Call<SimpleServerReply>

    @GET("get_substitutions")
    fun getSubstitutions(@Query("token") token: String,
                         @Query("date") date: Long): Call<SubstitutionsReply>

    @POST("delete_substitution")
    fun deleteSubstitution(@Query("token") token: String,
                           @Query("date") date: String,
                           @Query("group") group: String,
                           @Query("pair") pair: String): Call<SimpleServerReply>

    @GET("get_users")
    fun getUsers(@Query("token") token: String): Call<UsersReply>

    @POST("set_user_permission")
    fun setPermission(@Query("token") token: String,
                      @Query("vk_id") vk_id: Int,
                      @Query("permissions") permission: String): Call<SimpleServerReply>

    @GET("logout")
    fun logout(@Query("token") token: String): Call<SimpleServerReply>

    @GET("form_hints")
    fun getFormHints(): Call<SubstitutionFormHints>
}