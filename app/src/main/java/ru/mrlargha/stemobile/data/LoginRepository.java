package ru.mrlargha.stemobile.data;

import androidx.annotation.Nullable;

import ru.mrlargha.stemobile.data.model.LoginServerReply;

/**
 * Репозиторий авторизации
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private final STEDataSource dataSource;
    private LoginServerReply user = null;

    private LoginRepository(STEDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Получить экземпляр репоизитория
     *
     * @param dataSource источник данных
     * @return инстанция <code>LoginRepository</code>
     */
    public static LoginRepository getInstance(STEDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    /**
     * Авторизован ли пользователь
     *
     * @return true - если пользователь авторизован, false - если нет
     */
    public boolean isLoggedIn() {
        return user.getStatus().equals("ok");
    }

    /**
     * Получить токен авторизации
     *
     * @return токен авторизации
     */
    @Nullable
    public String getToken() {
        if (user != null) {
            return user.getSte_token();
        } else {
            return null;
        }
    }

    /**
     * Получить имя пользователя
     *
     * @return имя пользователя
     */
    @Nullable
    public String getName() {
        if (user != null) {
            return user.getName();
        } else {
            return null;
        }
    }

    /**
     * Завершить сеанс (анулировать токен)
     */
    public void logout() {
        dataSource.logout(user.getSte_token());
        user = null;
    }

    private void setLoggedInUser(LoginServerReply user) {
        this.user = user;
    }

    /**
     * Авторизовать пользователя
     *
     * @param username имя пользователя
     * @param password пароль
     * @return ответ сервера <code>LoginServerReply</code>
     */
    public Result<LoginServerReply> login(String username, String password) {
        Result result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoginServerReply>) result).getData());
        }
        return result;
    }

    /**
     * Зарегистрировать пользователя
     *
     * @param vk_id    vk_id пользователя
     * @param password пароль
     * @return ответ сервера <code>LoginServerReply</code>
     */
    public Result<LoginServerReply> register(String vk_id, String password) {
        Result result = dataSource.register(vk_id, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoginServerReply>) result).getData());
        }
        return result;
    }

    /**
     * Получить информацию о пользователе
     *
     * @param token токен авторизации
     * @return ответ сервера <code>LoginServerReply</code>
     */
    public Result<LoginServerReply> getInfo(String token) {
        Result result = dataSource.getInfo(token);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoginServerReply>) result).getData());
        }
        return (Result<LoginServerReply>) result;
    }
}
