package ru.mrlargha.stemobile.data;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mrlargha.stemobile.data.model.LoggedInUser;

public class LoginDataSource {

    private Retrofit mRetrofit;
    private STEApi mSTEApi;

    public LoginDataSource() {
        mRetrofit = new Retrofit.Builder().baseUrl("http://fspovkbot.tmweb.ru/ste/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mSTEApi = mRetrofit.create(STEApi.class);
    }

    public Result<LoggedInUser> login(String vk_id, String password) {
        Call<LoggedInUser> loggedInUserCall = mSTEApi.login(vk_id, password);
        try {
            Response<LoggedInUser> response = loggedInUserCall.execute();
            return new Result.Success<LoggedInUser>(response.body());
        } catch (IOException e) {
            return new Result.Error(e);
        }
    }

//    public Result<LoggedInUser> getUserInfo(String ste_token){
//      TODO: Request user info
//    }

    public void logout() {
        // TODO: revoke authentication
    }
}
