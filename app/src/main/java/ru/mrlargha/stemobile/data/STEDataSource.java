package ru.mrlargha.stemobile.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.io.IOException;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mrlargha.stemobile.data.model.SimpleServerReply;
import ru.mrlargha.stemobile.data.model.Substitution;

public class STEDataSource {

    private STEApi mSTEApi;

    public STEDataSource() {
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context)
                -> new Date(json.getAsJsonPrimitive().getAsLong()));

        Gson gson = builder.create();
        Retrofit mRetrofit = new Retrofit.Builder().baseUrl("http://fspovkbot.tmweb.ru/ste/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mSTEApi = mRetrofit.create(STEApi.class);
    }

    public Result login(String vk_id, String password) {
        return executeCall(mSTEApi.login(vk_id, password));
    }

    Result register(String vk_id, String password) {
        return executeCall(mSTEApi.register(vk_id, password));
    }

    Result getInfo(String token) {
        return executeCall(mSTEApi.getInfo(token));
    }

    Result sendSubstitution(Substitution substitution, String token) {
        return executeCall(mSTEApi.insertSubstitution(token, String.valueOf(
                substitution.getSubstitutionDate().getTime()), substitution.getCabinet(),
                                                      substitution.getTeacher(),
                                                      substitution.getSubject(),
                                                      String.valueOf(substitution.getPair()),
                                                      String.valueOf(substitution.getGroup())));
    }

    Result getSubstitutions(String token, int date) {
        return executeCall(mSTEApi.getSubstitutions(token, date));
    }

    private <T extends SimpleServerReply> Result executeCall(Call<T> userCall) {
        try {
            Response<T> response = userCall.execute();
            if (response.body() != null) {
                if (response.body().getStatus().equals("ok")) {
                    return new Result.Success<>(response.body());
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


    public void logout() {
        // TODO: revoke authentication
    }
}
