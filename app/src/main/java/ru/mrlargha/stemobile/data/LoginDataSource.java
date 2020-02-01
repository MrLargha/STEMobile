package ru.mrlargha.stemobile.data;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mrlargha.stemobile.data.model.LoginServerReply;

public class LoginDataSource {

    private Retrofit mRetrofit;
    private STEApi mSTEApi;

    public LoginDataSource() {
        mRetrofit = new Retrofit.Builder().baseUrl("http://fspovkbot.tmweb.ru/ste/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mSTEApi = mRetrofit.create(STEApi.class);
    }

    public Result<LoginServerReply> login(String vk_id, String password) {
        Call<LoginServerReply> loggedInUserCall = mSTEApi.login(vk_id, password);
        try {
            Response<LoginServerReply> response = loggedInUserCall.execute();
            if (response.body() != null) {
                if (response.body().getStatus().equals("ok")) {
                    return new Result.Success<LoginServerReply>(response.body());
                } else {
                    return new Result.Error(response.body().getError_string());
                }
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            return new Result.Error("Ошибка сети");
        }
    }

//    public Result<LoggedInUser> getUserInfo(String ste_token){
//      TODO: Request user info
//    }

    public void logout() {
        // TODO: revoke authentication
    }
}
