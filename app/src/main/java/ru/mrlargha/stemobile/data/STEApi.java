package ru.mrlargha.stemobile.data;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.mrlargha.stemobile.data.model.LoggedInUser;

public interface STEApi {
    @POST("login")
    Call<LoggedInUser> login(@Query("vk_id") String vk_id, @Query("password") String password);
}
