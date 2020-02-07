package ru.mrlargha.stemobile.data.model;

public class User {
    private String name;
    private int group;
    private String permissions;
    private int vk_id;


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
