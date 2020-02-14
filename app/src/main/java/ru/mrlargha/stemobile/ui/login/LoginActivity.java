package ru.mrlargha.stemobile.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import ru.mrlargha.stemobile.R;
import ru.mrlargha.stemobile.data.model.LoginServerReply;
import ru.mrlargha.stemobile.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private TextInputLayout passwordLayout;
    private TextInputLayout usernameLayout;
    private MaterialButton loginButton;
    private MaterialButton registerButton;
    private ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        usernameEditText = findViewById(R.id.vk_id_input);
        passwordEditText = findViewById(R.id.password_input);
        passwordLayout = findViewById(R.id.password);
        usernameLayout = findViewById(R.id.vk_id);
        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.register);
        loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            registerButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameLayout.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordLayout.setError(getString(loginFormState.getPasswordError()));
            } else {
                passwordLayout.setErrorEnabled(false);
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(usernameEditText.getText().toString(),
                    passwordEditText.getText().toString());
            SharedPreferences sp = getSharedPreferences("keystore", MODE_PRIVATE);
            sp.edit().putString("vk_id", usernameEditText.getText().toString()).apply();
        });

        registerButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.register(usernameEditText.getText().toString(),
                    passwordEditText.getText().toString());
            SharedPreferences sp = getSharedPreferences("keystore", MODE_PRIVATE);
            sp.edit().putString("vk_id", usernameEditText.getText().toString()).apply();
        });

        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                loginButton.callOnClick();
                return true;
            }
            return false;
        });

        String token = getSharedPreferences("keystore", MODE_PRIVATE).getString("ste_token", "");
        String vk_id = getSharedPreferences("keystore", MODE_PRIVATE).getString("vk_id", "");
        if (!token.isEmpty()) {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.getInfo(token);
        } else if (!vk_id.isEmpty()) {
            usernameEditText.setText(vk_id);
        }

    }

    private void updateUiWithUser(LoginServerReply model) {
        String welcome = getString(R.string.welcome) + ", " + model.getName() + '!';
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        SharedPreferences sp = getSharedPreferences("keystore", MODE_PRIVATE);
        sp.edit().putString("ste_token", model.getSte_token()).apply();
        startActivityForResult(new Intent(this, MainActivity.class),
                MainActivity.REQUEST_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_CODE) {
            if (resultCode == MainActivity.CODE_LOGOUT) {
                SharedPreferences sp = getSharedPreferences("keystore", MODE_PRIVATE);
                sp.edit().putString("ste_token", "").apply();
                passwordEditText.setText("");
                loginViewModel.logout();
            } else {
                finish();
            }
        }
    }
}
