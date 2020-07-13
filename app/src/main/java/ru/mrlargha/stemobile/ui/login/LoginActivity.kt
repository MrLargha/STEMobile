package ru.mrlargha.stemobile.ui.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.vk.api.sdk.VK
import com.vk.api.sdk.VK.onActivityResult
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import ru.mrlargha.stemobile.R
import ru.mrlargha.stemobile.data.model.LoginServerReply
import ru.mrlargha.stemobile.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private var loginViewModel: LoginViewModel? = null
    private var usernameEditText: TextInputEditText? = null
    private var passwordEditText: TextInputEditText? = null
    private var passwordLayout: TextInputLayout? = null
    private var usernameLayout: TextInputLayout? = null
    private var loginButton: MaterialButton? = null
    private var registerButton: MaterialButton? = null
    private var loadingProgressBar: ProgressBar? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        usernameEditText = findViewById(R.id.vk_id_input)
        passwordEditText = findViewById(R.id.password_input)
        passwordLayout = findViewById(R.id.password)
        usernameLayout = findViewById(R.id.vk_id)
        loginButton = findViewById(R.id.login)
        registerButton = findViewById(R.id.register)
        loadingProgressBar = findViewById(R.id.loading)
        try {
            val pInfo = this.packageManager.getPackageInfo(packageName, 0)
            val version = pInfo.versionName
            (findViewById<View>(R.id.version) as TextView).text = "$version BETA RELEASE"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        usernameLayout!!.setStartIconOnClickListener { VK.login(this) }
        loginViewModel!!.loginFormState.observe(this, Observer observe@{ loginFormState: LoginFormState? ->
            if (loginFormState == null) {
                return@observe
            }
            if (!loginFormState.isDataValid) {
                loadingProgressBar!!.visibility = View.GONE
            }
            if (loginFormState.usernameError != null) {
                usernameLayout!!.error = getString(loginFormState.usernameError!!)
            }
            if (loginFormState.passwordError != null) {
                passwordLayout!!.error = getString(loginFormState.passwordError!!)
            } else {
                passwordLayout!!.isErrorEnabled = false
            }
        })
        loginViewModel!!.loginResult.observe(this, Observer observe@{ loginResult: LoginResult? ->
            if (loginResult == null) {
                return@observe
            }
            loadingProgressBar!!.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
        })
        passwordEditText!!.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel!!.login(usernameEditText!!.text.toString(),
                        passwordEditText!!.text.toString())
            }
            false
        }
        loginButton!!.setOnClickListener {
            loadingProgressBar!!.visibility = View.VISIBLE
            loginViewModel!!.login(usernameEditText!!.text.toString(),
                    passwordEditText!!.text.toString())
            val sp = getSharedPreferences("keystore", Context.MODE_PRIVATE)
            sp.edit().putString("vk_id", usernameEditText!!.text.toString()).apply()
        }
        registerButton!!.setOnClickListener(View.OnClickListener { v: View? ->
            loadingProgressBar!!.visibility = View.VISIBLE
            loginViewModel!!.register(usernameEditText!!.text.toString(),
                    passwordEditText!!.text.toString())
            val sp = getSharedPreferences("keystore", Context.MODE_PRIVATE)
            sp.edit().putString("vk_id", usernameEditText!!.text.toString()).apply()
        })
        passwordEditText!!.setOnEditorActionListener(OnEditorActionListener setOnEditorActionListener@{ v: TextView?, actionId: Int, event: KeyEvent? ->
            if (event != null && (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || (event.action == KeyEvent.ACTION_DOWN
                            && event.keyCode == KeyEvent.KEYCODE_ENTER))) {
                loginButton!!.callOnClick()
                return@setOnEditorActionListener true
            }
            false
        })
        val token = getSharedPreferences("keystore", Context.MODE_PRIVATE).getString("ste_token", "")
        val vk_id = getSharedPreferences("keystore", Context.MODE_PRIVATE).getString("vk_id", "")
        if (!token!!.isEmpty()) {
            loadingProgressBar!!.visibility = View.VISIBLE
            loginViewModel!!.getInfo(token)
        } else if (!vk_id!!.isEmpty()) {
            usernameEditText!!.setText(vk_id)
        }
    }

    private fun updateUiWithUser(model: LoginServerReply?) {
        val welcome = getString(R.string.welcome) + ", " + model!!.name + '!'
        Toast.makeText(applicationContext, welcome, Toast.LENGTH_LONG).show()
        val sp = getSharedPreferences("keystore", Context.MODE_PRIVATE)
        sp.edit().putString("ste_token", model.ste_token).apply()
        startActivityForResult(Intent(this, MainActivity::class.java),
                MainActivity.REQUEST_CODE)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun showLoginFailed(errorString: String?) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MainActivity.REQUEST_CODE) {
            if (resultCode == MainActivity.CODE_LOGOUT) {
                val sp = getSharedPreferences("keystore", Context.MODE_PRIVATE)
                sp.edit().putString("ste_token", "").apply()
                passwordEditText!!.setText("")
                loginViewModel!!.logout()
            } else {
                finish()
            }
        } else {
            onActivityResult(requestCode, resultCode, data, object : VKAuthCallback {
                override fun onLoginFailed(i: Int) {
                    Toast.makeText(this@LoginActivity, "Ошибка авторизации VK",
                            Toast.LENGTH_SHORT).show()
                }

                override fun onLogin(token: VKAccessToken) {
                    usernameEditText!!.setText(token.userId.toString())
                }
            })
        }
    }
}