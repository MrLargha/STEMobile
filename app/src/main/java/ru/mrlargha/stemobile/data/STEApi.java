package ru.mrlargha.stemobile.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.mrlargha.stemobile.data.model.LoginServerReply;
import ru.mrlargha.stemobile.data.model.SimpleServerReply;

public interface STEApi {
    @POST("login")
    Call<LoginServerReply> login(@Query("vk_id") String vk_id, @Query("password") String password);

    @POST("register")
    Call<LoginServerReply> register(@Query("vk_id") String vk_id, @Query("password") String password);

    @GET("info")
    Call<LoginServerReply> getInfo(@Query("token") String token);

    @POST("add_substitution")
    Call<SimpleServerReply> insertSubstitution(String token, String date,
                                               String cabinet, String teacher,
                                               String subject, String pair,
                                               String group);

}
