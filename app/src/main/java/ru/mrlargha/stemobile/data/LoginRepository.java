package ru.mrlargha.stemobile.data;

import androidx.annotation.Nullable;

import ru.mrlargha.stemobile.data.model.LoginServerReply;


public class LoginRepository {

    private static volatile LoginRepository instance;

    private STEDataSource dataSource;
    private LoginServerReply user = null;

    private LoginRepository(STEDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(STEDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user.getStatus().equals("ok");
    }

    @Nullable
    public String getToken() {
        if (user != null) {
            return user.getSte_token();
        } else {
            return null;
        }
    }

    @Nullable
    public String getName() {
        if (user != null) {
            return user.getName();
        } else {
            return null;
        }
    }

    public void logout() {
        dataSource.logout(user.getSte_token());
        user = null;
    }

    private void setLoggedInUser(LoginServerReply user) {
        this.user = user;
    }


    public Result<LoginServerReply> login(String username, String password) {
        Result result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoginServerReply>) result).getData());
        }
        return result;
    }

    public Result<LoginServerReply> register(String vk_id, String password) {
        Result result = dataSource.register(vk_id, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoginServerReply>) result).getData());
        }
        return result;
    }

    public Result<LoginServerReply> getInfo(String token) {
        Result result = dataSource.getInfo(token);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoginServerReply>) result).getData());
        }
        return (Result<LoginServerReply>) result;
    }
}
