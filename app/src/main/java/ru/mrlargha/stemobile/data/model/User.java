package ru.mrlargha.stemobile.data.model;

/**
 * Модель пользователя
 */
public class User {
    private final String name;
    private final int group;
    private String permissions;
    private final int vk_id;

    /**
     * Конструктор по-умолчанию
     *
     * @param name        имя пользователя
     * @param group       учебная группа пользователя
     * @param permissions привилегии пользователя
     * @param vk_id       vkID пользователя
     */
    public User(String name, int group, String permissions, int vk_id) {
        this.name = name;
        this.group = group;
        this.permissions = permissions;
        this.vk_id = vk_id;
    }

    public String getName() {
        return name;
    }

    public int getGroup() {
        return group;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public int getVk_id() {
        return vk_id;
    }
}
