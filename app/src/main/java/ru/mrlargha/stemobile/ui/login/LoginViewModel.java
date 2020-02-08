package ru.mrlargha.stemobile.ui.login;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.mrlargha.stemobile.R;
import ru.mrlargha.stemobile.data.LoginRepository;
import ru.mrlargha.stemobile.data.Result;
import ru.mrlargha.stemobile.data.STEDataSource;
import ru.mrlargha.stemobile.data.model.LoginServerReply;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository = LoginRepository.getInstance(new STEDataSource());

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        new LoginTask().execute(username, password);
    }

    void register(String username, String password) {
        new RegisterTask().execute(username, password);
    }

    void getInfo(String token) {
        new FetchInfoTask().execute(token);
    }

    void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        return !username.isEmpty();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    private void handleLoginResult(Result<LoginServerReply> loggedInUserResult) {
        if (loggedInUserResult instanceof Result.Success) {
            LoginServerReply data = ((Result.Success<LoginServerReply>) loggedInUserResult).getData();
            loginResult.setValue(new LoginResult(data));
        } else {
            loginResult.setValue(new LoginResult(((Result.Error) loggedInUserResult).getErrorString()));
        }
    }

    public void logout() {
        new LogoutTask().execute();
    }

    private class LoginTask extends AuthTask {
        @Override
        protected Result<LoginServerReply> doInBackground(String... strings) {
            return loginRepository.login(strings[0], strings[1]);
        }
    }

    private class LogoutTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            loginRepository.logout();
            return null;
        }
    }

    private class RegisterTask extends AuthTask {
        @Override
        protected Result<LoginServerReply> doInBackground(String... strings) {
            return loginRepository.register(strings[0], strings[1]);
        }
    }

    private class FetchInfoTask extends AuthTask {
        @Override
        protected Result<LoginServerReply> doInBackground(String... strings) {
            return loginRepository.getInfo(strings[0]);
        }
    }

    private class AuthTask extends AsyncTask<String, Void, Result<LoginServerReply>> {

        @Override
        protected Result<LoginServerReply> doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(Result<LoginServerReply> loggedInUserResult) {
            handleLoginResult(loggedInUserResult);
        }
    }
}
