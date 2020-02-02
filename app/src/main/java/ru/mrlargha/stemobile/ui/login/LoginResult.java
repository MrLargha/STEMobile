package ru.mrlargha.stemobile.ui.login;

import androidx.annotation.Nullable;

import ru.mrlargha.stemobile.data.model.LoginServerReply;

class LoginResult {
    @Nullable
    private LoginServerReply success;
    @Nullable
    private String error;

    LoginResult(@Nullable String error) {
        this.error = error;
    }

    LoginResult(@Nullable LoginServerReply success) {
        this.success = success;
    }

    @Nullable
    LoginServerReply getSuccess() {
        return success;
    }

    @Nullable
    String getError() {
        return error;
    }
}
