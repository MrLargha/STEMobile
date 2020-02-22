package ru.mrlargha.stemobile.ui.users;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.LinkedList;
import java.util.List;

import ru.mrlargha.stemobile.data.Result;
import ru.mrlargha.stemobile.data.STERepository;
import ru.mrlargha.stemobile.data.model.SimpleServerReply;
import ru.mrlargha.stemobile.data.model.User;
import ru.mrlargha.stemobile.data.model.UsersReply;

public class UsersViewModel extends AndroidViewModel {

    private MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>(new LinkedList<>());
    private MutableLiveData<Boolean> hasNetworkOperationInProgress = new MutableLiveData<>(false);
    private MutableLiveData<String> errorString = new MutableLiveData<>("");
    private STERepository mSTERepository;

    public UsersViewModel(@NonNull Application application) {
        super(application);
        mSTERepository = STERepository.getRepository(application);
        hasNetworkOperationInProgress.setValue(true);
        new GetUsersTask().execute();
    }

    void setUserPermission(User user) {
        hasNetworkOperationInProgress.setValue(true);
        new SetUserPermissionTask().execute(String.valueOf(user.getVk_id()), user.getPermissions());
    }

    MutableLiveData<Boolean> getHasNetworkOperationInProgress() {
        return hasNetworkOperationInProgress;
    }

    MutableLiveData<List<User>> getUsersLiveData() {
        return usersLiveData;
    }

    MutableLiveData<String> getErrorString() {
        return errorString;
    }

    private class GetUsersTask extends AsyncTask<Void, Void, List<User>> {

        @Override
        protected List<User> doInBackground(Void... voids) {
            Result<UsersReply> result = mSTERepository.getUsers();
            if (result instanceof Result.Success) {
                return ((Result.Success<UsersReply>) result).getData().getUsers();
            } else {
                errorString.postValue(((Result.Error) result).getErrorString());
                return new LinkedList<>();
            }
        }

        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);
            if (!users.isEmpty()) {
                usersLiveData.setValue(users);
            }
            hasNetworkOperationInProgress.setValue(false);
        }
    }

    private class SetUserPermissionTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Result<SimpleServerReply> result =
                    mSTERepository.setUserPermission(Integer.parseInt(strings[0]), strings[1]);
            if (result instanceof Result.Success) {
                return ((Result.Success<SimpleServerReply>) result).getData().getStatus();
            } else {
                return ((Result.Error) result).getErrorString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("ok")) {
                hasNetworkOperationInProgress.setValue(false);
            } else {
                errorString.postValue(result);
                new GetUsersTask().execute();
            }
        }
    }
}
