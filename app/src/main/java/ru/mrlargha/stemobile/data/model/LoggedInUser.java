package ru.mrlargha.stemobile.data.model;


public class LoggedInUser {

    private String userId;
    private String displayName;
    private String permissions;
    private String ste_api_token;

    public LoggedInUser(String userId, String displayName, String permissions, String ste_api_token) {
        this.userId = userId;
        this.displayName = displayName;
        this.permissions = permissions;
        this.ste_api_token = ste_api_token;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPermissions() {
        return permissions;
    }

    public String getSte_api_token() {
        return ste_api_token;
    }
}
