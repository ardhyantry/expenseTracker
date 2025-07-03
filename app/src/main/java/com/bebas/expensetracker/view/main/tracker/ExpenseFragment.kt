package com.bebas.expensetracker.view.main.tracker

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bebas.expensetracker.databinding.FragmentExpenseBinding
import com.bebas.expensetracker.model.Expense
import com.bebas.expensetracker.util.DateUtils
import com.bebas.expensetracker.util.SessionManager
import com.bebas.expensetracker.view.main.adapter.ExpenseAdapter
import com.bebas.expensetracker.viewmodel.BudgetViewModel
import com.bebas.expensetracker.viewmodel.ExpenseViewModel

class ExpenseFragment : Fragment() {

    private lateinit var binding: FragmentExpenseBinding
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var budgetViewModel: BudgetViewModel
    private lateinit var session: SessionManager
    private lateinit var adapter: ExpenseAdapter
    private var userId: Int = -1
    private val budgetMap = mutableMapOf<Int, String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        session = SessionManager(requireContext())
        userId = session.getUserId()

        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
        budgetViewModel = ViewModelProvider(this)[BudgetViewModel::class.java]

        adapter = ExpenseAdapter(
            onNominalClick = { showDetailDialog(it) },
            budgetIdToNameMap = budgetMap
        )

        binding.rvExpense.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpense.adapter = adapter

        // Observasi data budget
        budgetViewModel.getBudgetsForUser(userId).observe(viewLifecycleOwner) { budgets ->
            budgetMap.clear()
            budgets.forEach { budgetMap[it.id] = it.name }
            adapter.notifyDataSetChanged()
        }

        // Observasi data expense
        expenseViewModel.getExpensesForUser(userId).observe(viewLifecycleOwner) { expenses ->
            adapter.submitList(expenses.sortedByDescending { it.timestamp })
        }

        binding.fabAddExpense.setOnClickListener {
            showAddExpenseDialog()
        }
    }

    private fun showDetailDialog(expense: Expense) {
        val budgetName = budgetMap[expense.budgetId] ?: "Tidak diketahui"
        val tanggal = DateUtils.formatDate(expense.timestamp)
        val nominal = "Rp %,d".format(expense.amount)

        val message = """
            📅 Tanggal     : $tanggal
            💾 Nominal     : $nominal
            📂 Budget      : $budgetName
            📝 Keterangan  : ${expense.description}
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("📊 Detail Pengeluaran")
            .setMessage(message)
            .setPositiveButton("Tutup", null)
            .show()
    }

    private fun showAddExpenseDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(com.bebas.expensetracker.R.layout.dialog_new_expense, null)

        val spinner = dialogView.findViewById<Spinner>(com.bebas.expensetracker.R.id.spinnerBudget)
        val etAmount = dialogView.findViewById<EditText>(com.bebas.expensetracker.R.id.etAmount)
        val etNote = dialogView.findViewById<EditText>(com.bebas.expensetracker.R.id.etNote)

        val budgetNames = mutableListOf<String>()
        val budgetIds = mutableListOf<Int>()

        budgetViewModel.getBudgetsForUser(userId).observe(viewLifecycleOwner) { budgets ->
            budgetNames.clear()
            budgetIds.clear()
            budgets.forEach {
                budgetNames.add(it.name)
                budgetIds.add(it.id)
            }

            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, budgetNames)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = spinnerAdapter
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
                    userId = userId,
                    budgetId = budgetId,
                    amount = nominal,
                    description = note,
                    timestamp = System.currentTimeMillis()
                )

                expenseViewModel.insert(expense)
                Toast.makeText(requireContext(), "Pengeluaran ditambahkan", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
