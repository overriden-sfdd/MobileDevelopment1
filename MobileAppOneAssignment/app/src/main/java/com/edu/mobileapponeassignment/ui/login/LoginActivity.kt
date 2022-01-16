package com.edu.mobileapponeassignment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.edu.mobileapponeassignment.data.remote.dto.login.UserLogin
import com.edu.mobileapponeassignment.databinding.ActivityLoginBinding
import com.edu.mobileapponeassignment.ui.login.viewmodels.LoginViewModel
import com.edu.mobileapponeassignment.ui.login.viewmodels.LoginViewModelFactory
import com.edu.mobileapponeassignment.ui.main.MainActivity

data class LoggedInUserView(
    val displayName: String
)

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        val usernameInput = binding.editTextUsername
        val passwordInput = binding.editTextPassword

        binding.loginButton.setOnClickListener {
            loginViewModel.setUserLogin(UserLogin(usernameInput.text.toString(), passwordInput.text.toString()))
        }

        loginViewModel.loggedInUser.observe(this@LoginActivity, {
            val loggedInUserView = LoggedInUserView(it.firstName)
            updateUiWithUser(loggedInUserView)
            startActivity(Intent(this, MainActivity::class.java))

            // Complete and destroy login activity once successful (always successful in my simple case though)
            finish()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        loginViewModel.cancelJobs()
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }
}