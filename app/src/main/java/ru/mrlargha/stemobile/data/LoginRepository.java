package ru.mrlargha.stemobile.data;

import ru.mrlargha.stemobile.data.model.LoginServerReply;


public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
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
}
