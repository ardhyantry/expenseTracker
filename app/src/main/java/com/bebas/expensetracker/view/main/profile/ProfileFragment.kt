package com.bebas.expensetracker.view.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bebas.expensetracker.databinding.FragmentProfileBinding
import com.bebas.expensetracker.model.AppDatabase
import com.bebas.expensetracker.model.User
import com.bebas.expensetracker.util.SessionManager
import com.bebas.expensetracker.view.auth.SignInActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var session: SessionManager
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        session = SessionManager(requireContext())
        val userId = session.getUserId()
        val userDao = AppDatabase.getInstance(requireContext()).userDao()

        // Ambil data user di background thread
        lifecycleScope.launch {
            user = withContext(Dispatchers.IO) {
                userDao.getUserById(userId)
            }

            user?.let {
                binding.tvWelcome.text = "Hai, ${it.firstName} ${it.lastName}"
            } ?: run {
                showToast("User tidak ditemukan")
            }
        }

        // Tombol GANTI PASSWORD
        binding.btnChangePassword.setOnClickListener {
            val oldPassword = binding.etOldPassword.text.toString().trim()
            val newPassword = binding.etNewPassword.text.toString().trim()
            val repeatPassword = binding.etRepeatPassword.text.toString().trim()

            if (oldPassword.isEmpty() || newPassword.isEmpty() || repeatPassword.isEmpty()) {
                showToast("Semua field harus diisi")
                return@setOnClickListener
            }

            if (user == null) {
                showToast("User belum dimuat")
                return@setOnClickListener
            }

            if (oldPassword != user!!.password) {
                showToast("Password lama salah")
                return@setOnClickListener
            }

            if (newPassword != repeatPassword) {
                showToast("Password baru tidak cocok")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val updatedUser = user!!.copy(password = newPassword)
                withContext(Dispatchers.IO) {
                    userDao.update(updatedUser)
                }
                user = updatedUser
                showToast("Password berhasil diperbarui")
                clearFields()
            }
        }

        // Tombol LOGOUT
        binding.btnSignOut.setOnClickListener {
            session.clearSession()
            startActivity(Intent(requireContext(), SignInActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun clearFields() {
        binding.etOldPassword.text = null
        binding.etNewPassword.text = null
        binding.etRepeatPassword.text = null
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}
