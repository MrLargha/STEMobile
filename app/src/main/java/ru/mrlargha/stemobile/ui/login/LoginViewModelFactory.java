//package ru.mrlargha.stemobile.ui.login;
//
//import androidx.annotation.NonNull;
//import androidx.lifecycle.ViewModel;
//import androidx.lifecycle.ViewModelProvider;
//
//import ru.mrlargha.stemobile.data.LoginDataSource;
//import ru.mrlargha.stemobile.data.LoginRepository;
//
///**
// * ViewModel provider factory to instantiate LoginViewModel.
// * Required given LoginViewModel has a non-empty constructor
// */
//public class LoginViewModelFactory implements ViewModelProvider.Factory {
//
//    @NonNull
//    @Override
//    @SuppressWarnings("unchecked")
//    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
//            return (T) new LoginViewModel();
//        } else {
//            throw new IllegalArgumentException("Unknown ViewModel class");
//        }
//    }
//}
