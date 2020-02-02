package ru.mrlargha.stemobile.data;

import ru.mrlargha.stemobile.data.model.LoginServerReply;


public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;
    private LoginServerReply user = null;

    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoginServerReply user) {
        this.user = user;
    }

    public Result<LoginServerReply> login(String username, String password) {
        Result<LoginServerReply> result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoginServerReply>) result).getData());
        }
        return result;
    }

    public Result<LoginServerReply> register(String vk_id, String password) {
        Result<LoginServerReply> result = dataSource.register(vk_id, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoginServerReply>) result).getData());
        }
        return result;
    }

    public Result<LoginServerReply> getInfo(String token) {
        Result<LoginServerReply> result = dataSource.getInfo(token);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoginServerReply>) result).getData());
        }
        return result;
    }
}
