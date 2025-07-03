package com.bebas.expensetracker.view.main.tracker

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bebas.expensetracker.R
import com.bebas.expensetracker.databinding.FragmentExpenseBinding
import com.bebas.expensetracker.model.Expense
import com.bebas.expensetracker.util.DateUtils
import com.bebas.expensetracker.view.main.adapter.ExpenseAdapter
import com.bebas.expensetracker.viewmodel.BudgetViewModel
import com.bebas.expensetracker.viewmodel.ExpenseViewModel

class ExpenseFragment : Fragment() {

    private lateinit var binding: FragmentExpenseBinding
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var budgetViewModel: BudgetViewModel
    private lateinit var adapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
        budgetViewModel = ViewModelProvider(this)[BudgetViewModel::class.java]

        val budgetMap = mutableMapOf<Int, String>()

        adapter = ExpenseAdapter(onItemClick = { showDetailDialog(it) }, budgetIdToNameMap = budgetMap)
        binding.rvExpense.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpense.adapter = adapter

        budgetViewModel.allBudgets.observe(viewLifecycleOwner) { budgets ->
            budgetMap.clear()
            budgets.forEach { budgetMap[it.id] = it.name }
            adapter.notifyDataSetChanged()
        }

        expenseViewModel.allExpenses.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.fabAddExpense.setOnClickListener {
            showAddExpenseDialog()
        }
    }

    private fun showDetailDialog(expense: Expense) {
        val message = """
            Tanggal: ${DateUtils.formatDate(expense.timestamp)}
            Nominal: Rp ${expense.amount}
            Budget ID: ${expense.budgetId}
            Keterangan: ${expense.description}
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Detail Pengeluaran")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showAddExpenseDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_new_expense, null)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinnerBudget)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        val etNote = dialogView.findViewById<EditText>(R.id.etNote)

        val budgetNames = mutableListOf<String>()
        val budgetIds = mutableListOf<Int>()

        budgetViewModel.allBudgets.observe(viewLifecycleOwner) { budgets ->
            budgetNames.clear()
            budgetIds.clear()
            budgets.forEach {
                budgetNames.add(it.name)
                budgetIds.add(it.id)
            }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, budgetNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Pengeluaran")
            .setView(dialogView)
            .setPositiveButton("Tambah") { _, _ ->
                val amountStr = etAmount.text.toString().trim()
                val note = etNote.text.toString().trim()
                val selectedIdx = spinner.selectedItemPosition
                val budgetId = budgetIds.getOrNull(selectedIdx) ?: -1

                if (amountStr.isEmpty() || budgetId == -1) {
                    Toast.makeText(requireContext(), "Data tidak lengkap", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val nominal = amountStr.toIntOrNull() ?: 0
                if (nominal <= 0) {
                    Toast.makeText(requireContext(), "Nominal tidak valid", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val expense = Expense(
                    budgetId = budgetId,
                    amount = nominal,
                    description = note,
                    timestamp = System.currentTimeMillis()
                )
                expenseViewModel.getTotalExpenseForBudget(budgetId) { currentTotal ->
                    budgetViewModel.getBudgetById(budgetId) { budget ->
                        if (budget == null) {
                            Toast.makeText(requireContext(), "Budget tidak ditemukan", Toast.LENGTH_SHORT).show()
                            return@getBudgetById
                        }

                        val totalSetelahTambah = currentTotal + nominal
                        val sisa = budget.amount - currentTotal
                        if (totalSetelahTambah > budget.amount) {
                            Toast.makeText(
                                requireContext(),
                                "Total melebihi budget \"${budget.name}\" (maks. Rp ${sisa})",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            expenseViewModel.insert(expense)
                            Toast.makeText(requireContext(), "Pengeluaran ditambahkan", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
