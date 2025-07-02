package com.bebas.expensetracker.view.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bebas.expensetracker.viewmodel.AuthViewModel
import com.bebas.expensetracker.databinding.ActivitySignUpBinding
import com.bebas.expensetracker.model.User

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignUp.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()
            ) {
                showToast("Semua field wajib diisi")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                showToast("Password tidak cocok")
                return@setOnClickListener
            }

            val user = User(
                username = username,
                firstName = firstName,
                lastName = lastName,
                password = password
            )

            viewModel.registerUser(user) { success, message ->
                runOnUiThread {
                    showToast(message)
                    if (success) finish()
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}