package com.bebas.expensetracker.view.main.budgeting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bebas.expensetracker.databinding.FragmentBudgetBinding
import com.bebas.expensetracker.model.Budget
import com.bebas.expensetracker.view.main.adapter.BudgetAdapter
import com.bebas.expensetracker.viewmodel.BudgetViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.widget.EditText
import android.widget.Toast
import com.bebas.expensetracker.R
import com.bebas.expensetracker.util.SessionManager

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private lateinit var budgetAdapter: BudgetAdapter
    private val budgetViewModel: BudgetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        observeData()
        binding.fabAddBudget.setOnClickListener {
            showBudgetDialog(null) // tambah
        }
    }

    private fun setupRecyclerView() {
        budgetAdapter = BudgetAdapter(
            listOf(),
            onItemClick = { budget ->
                showBudgetDialog(budget)
            }
        )
        binding.rvBudgetList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = budgetAdapter
        }
    }

    private fun observeData() {
        val userId = SessionManager(requireContext()).getUserId()
        budgetViewModel.getBudgetsForUser(userId).observe(viewLifecycleOwner) { list ->
            budgetAdapter.updateList(list)
        }
    }

    private fun showBudgetDialog(existingBudget: Budget?) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_budget, null)
        val etName = dialogView.findViewById<EditText>(R.id.etBudgetName)
        val etAmount = dialogView.findViewById<EditText>(R.id.etBudgetAmount)

        if (existingBudget != null) {
            etName.setText(existingBudget.name)
            etAmount.setText(existingBudget.amount.toString())
        }

        val dialogTitle = if (existingBudget != null) "Edit Budget" else "New Budget"

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(dialogTitle)
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val name = etName.text.toString().trim()
                val amount = etAmount.text.toString().toIntOrNull() ?: -1
                val userId = SessionManager(requireContext()).getUserId()

                if (name.isEmpty()) {
                    Toast.makeText(context, "Nama budget tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (amount < 0) {
                    Toast.makeText(context, "Nominal harus positif", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val budget = Budget(
                    id = existingBudget?.id ?: 0,
                    name = name,
                    amount = amount,
                    userId = userId
                )

                if (existingBudget == null) {
                    budgetViewModel.insert(budget)
                } else {
                    budgetViewModel.update(budget)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
