package com.bebas.expensetracker.view.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bebas.expensetracker.databinding.ActivitySignInBinding
import com.bebas.expensetracker.view.main.MainActivity
import com.bebas.expensetracker.util.SessionManager
import com.bebas.expensetracker.viewmodel.AuthViewModel

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var sessionManager: SessionManager
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        binding.btnSignIn.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                showToast("Username dan password wajib diisi")
                return@setOnClickListener
            }

            viewModel.loginUser(username, password) { success, message, user ->
                runOnUiThread {
                    if (success && user != null) {
                        sessionManager.saveSession(
                            id = user.id,
                            username = user.username,
                            firstName = user.firstName,
                            lastName = user.lastName
                        )
                        showToast("Login berhasil")
                        navigateToMain()
                    } else {
                        showToast(message)
                    }
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}