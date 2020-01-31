package ru.mrlargha.stemobile.data.model;


public class LoggedInUser {

    private String status;
    private String name;
    private String permissions;
    private String ste_token;

    public LoggedInUser(String status, String name, String permissions, String ste_token) {
        this.status = status;
        this.name = name;
        this.permissions = permissions;
        this.ste_token = ste_token;
    }

    public String getName() {
        return name;
    }

    public String getPermissions() {
        return permissions;
    }

    public String getSte_token() {
        return ste_token;
    }

    public String getStatus() {
        return status;
    }
}
