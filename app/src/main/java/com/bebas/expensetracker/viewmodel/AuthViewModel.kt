package com.bebas.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bebas.expensetracker.model.AppDatabase
import com.bebas.expensetracker.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getInstance(application).userDao()

    fun registerUser(user: User, onResult: (success: Boolean, message: String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingUser = userDao.getUserByUsername(user.username)
            if (existingUser != null) {
                onResult(false, "Username sudah terdaftar")
            } else {
                userDao.insert(user)
                onResult(true, "Registrasi berhasil")
            }
        }
    }
    fun loginUser(
        username: String,
        password: String,
        onResult: (success: Boolean, message: String, user: User?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userDao.getUserByUsername(username)
            if (user != null && user.password == password) {
                onResult(true, "Login berhasil", user)
            } else {
                onResult(false, "Username atau password salah", null)
            }
        }
    }
}
