package ru.mrlargha.stemobile.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.mrlargha.stemobile.data.model.LoginServerReply;
import ru.mrlargha.stemobile.data.model.SimpleServerReply;
import ru.mrlargha.stemobile.data.model.SubstitutionsReply;
import ru.mrlargha.stemobile.data.model.UsersReply;

public interface STEApi {
    @POST("login")
    Call<LoginServerReply> login(@Query("vk_id") String vk_id, @Query("password") String password);

    @POST("register")
    Call<LoginServerReply> register(@Query("vk_id") String vk_id, @Query("password") String password);

    @GET("info")
    Call<LoginServerReply> getInfo(@Query("token") String token);

    @POST("add_substitution")
    Call<SimpleServerReply> insertSubstitution(@Query("token") String token,
                                               @Query("date") String date,
                                               @Query("cabinet") String cabinet,
                                               @Query("teacher") String teacher,
                                               @Query("subject") String subject,
                                               @Query("pair") String pair,
                                               @Query("group") String group);

    @GET("get_substitutions")
    Call<SubstitutionsReply> getSubstitutions(@Query("token") String token,
                                              @Query("date") long date);

    @GET("get_users")
    Call<UsersReply> getUsers(@Query("token") String token);

    @POST("set_user_permission")
    Call<SimpleServerReply> setPermission(@Query("token") String token,
                                          @Query("vk_id") int vk_id,
                                          @Query("permissions") String permission);

    @GET("logout")
    Call<SimpleServerReply> logout(@Query("token") String token);
}
