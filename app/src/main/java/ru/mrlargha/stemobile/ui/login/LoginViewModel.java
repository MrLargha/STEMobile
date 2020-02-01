package ru.mrlargha.stemobile.ui.login;

import android.os.AsyncTask;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.mrlargha.stemobile.R;
import ru.mrlargha.stemobile.data.LoginRepository;
import ru.mrlargha.stemobile.data.Result;
import ru.mrlargha.stemobile.data.model.LoginServerReply;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        new LoginTask().execute(username, password);
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

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    private class LoginTask extends AsyncTask<String, Void, Result<LoginServerReply>> {

        @Override
        protected Result<LoginServerReply> doInBackground(String... strings) {
            return loginRepository.login(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(Result<LoginServerReply> loggedInUserResult) {
            if (loggedInUserResult instanceof Result.Success) {
                LoginServerReply data = ((Result.Success<LoginServerReply>) loggedInUserResult).getData();
                loginResult.setValue(new LoginResult(new LoggedInUserView(data.getName())));
            } else {
                loginResult.setValue(new LoginResult(((Result.Error) loggedInUserResult).getErrorString()));
            }
        }
    }
}
