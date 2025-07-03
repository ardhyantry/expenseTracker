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
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var session: SessionManager
    private lateinit var user: User

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

        lifecycleScope.launch {
            user = userDao.getUserById(userId) ?: return@launch
            binding.tvWelcome.text = "Hai, ${user.firstName} ${user.lastName}"
        }

        // tombol GANTI PASSWORD
        binding.btnChangePassword.setOnClickListener {
            val oldPassword = binding.etOldPassword.text.toString().trim()
            val newPassword = binding.etNewPassword.text.toString().trim()
            val repeatPassword = binding.etRepeatPassword.text.toString().trim()

            if (oldPassword.isEmpty() || newPassword.isEmpty() || repeatPassword.isEmpty()) {
                showToast("Semua field harus diisi")
                return@setOnClickListener
            }

            if (oldPassword != user.password) {
                showToast("Password lama salah")
                return@setOnClickListener
            }

            if (newPassword != repeatPassword) {
                showToast("Password baru tidak cocok")
                return@setOnClickListener
            }

            // update password
            lifecycleScope.launch {
                val updatedUser = user.copy(password = newPassword)
                userDao.update(updatedUser)
                showToast("Password berhasil diperbarui")
                clearFields()
            }
        }

        // tombol LOGOUT
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
