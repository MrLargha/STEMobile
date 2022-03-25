package ru.mrlargha.stemobile.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.mrlargha.stemobile.data.model.LoginServerReply;
import ru.mrlargha.stemobile.data.model.SimpleServerReply;
import ru.mrlargha.stemobile.data.model.SubstitutionFormHints;
import ru.mrlargha.stemobile.data.model.SubstitutionsReply;
import ru.mrlargha.stemobile.data.model.UsersReply;

/**
 * API для работы с сервером
 */
public interface STEApi {
    /**
     * Авторизовать пользователя
     *
     * @param vk_id    vk_id пользователя
     * @param password пароль пользователя
     * @return <code>LoginServerReply</code>
     */
    @POST("login")
    Call<LoginServerReply> login(@Query("vk_id") String vk_id, @Query("password") String password);

    /**
     * Зарегистрировать пользователя
     *
     * @param vk_id    vk_id пользователя
     * @param password пароль
     * @return ответ сервера <code>LoginServerReply</code>
     */
    @POST("register")
    Call<LoginServerReply> register(@Query("vk_id") String vk_id, @Query("password") String password);

    /**
     * Получить информацию о пользователе
     *
     * @param token токен авторизации
     * @return ответ сервера <code>LoginServerReply</code>
     */
    @GET("info")
    Call<LoginServerReply> getInfo(@Query("token") String token);

    /**
     * Добавить замещения на сервер
     *
     * @param token   токен авторизации
     * @param date    дата на которую вводятся замещение
     * @param cabinet кабинет
     * @param teacher преподаватель
     * @param subject предмет
     * @param pair    пара
     * @param group   учебная группа
     * @return ответ сервера <code>SimpleServerReply</code>
     */
    @POST("add_substitution")
    Call<SimpleServerReply> insertSubstitution(@Query("token") String token,
                                               @Query("date") String date,
                                               @Query("cabinet") String cabinet,
                                               @Query("teacher") String teacher,
                                               @Query("subject") String subject,
                                               @Query("pair") String pair,
                                               @Query("group") String group);

    /**
     * Получить списко замещения
     *
     * @param token токен авторизации
     * @param date  дата на которую нужно предоставить замещения
     * @return ответ сервера <code>SubstitutionsReply</code>
     */
    @GET("get_substitutions")
    Call<SubstitutionsReply> getSubstitutions(@Query("token") String token,
                                              @Query("date") long date);

    /**
     * Удалить замещение с сервера
     *
     * @param token токен авторизации
     * @param date  дата замещения
     * @param group учебная группа
     * @param pair  пара
     * @return ответ сервера <code>SubstitutionsReply</code>
     */
    @POST("delete_substitution")
    Call<SimpleServerReply> deleteSubstitution(@Query("token") String token,
                                               @Query("date") String date,
                                               @Query("group") String group,
                                               @Query("pair") String pair);

    /**
     * @param token
     * @return ответ сервера <code>UsersReply</code>
     */
    @GET("get_users")
    Call<UsersReply> getUsers(@Query("token") String token);

    /**
     * Установить привилегии пользователя
     *
     * @param token      токен авторизации пользоватеоя
     * @param vk_id      k_id
     * @param permission требуемые привилегии
     * @return ответ сервера <code>SubstitutionsReply</code>
     */
    @POST("set_user_permission")
    Call<SimpleServerReply> setPermission(@Query("token") String token,
                                          @Query("vk_id") int vk_id,
                                          @Query("permissions") String permission);

    /**
     * Выйти из аккаунта
     *
     * @param token токен авторизации
     * @return ответ сервера <code>SimpleServerReply</code>
     */
    @GET("logout")
    Call<SimpleServerReply> logout(@Query("token") String token);

    /**
     * Получить подсказки для формы авторизации пользователя
     *
     * @return ответ сервера <code>SubstitutionFormHints</code>
     */
    @GET("form_hints")
    Call<SubstitutionFormHints> getFormHints();
}
