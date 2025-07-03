package com.bebas.expensetracker.view.main.tracker

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bebas.expensetracker.databinding.FragmentExpenseBinding
import com.bebas.expensetracker.model.Expense
import com.bebas.expensetracker.util.DateUtils
import com.bebas.expensetracker.util.SessionManager
import com.bebas.expensetracker.view.main.adapter.ExpenseAdapter
import com.bebas.expensetracker.viewmodel.BudgetViewModel
import com.bebas.expensetracker.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch

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

        budgetViewModel.getBudgetsForUser(userId).observe(viewLifecycleOwner) { budgets ->
            budgetMap.clear()
            budgets.forEach { budgetMap[it.id] = it.name }
            adapter.notifyDataSetChanged()
        }

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
        val tvBudgetInfo =
            dialogView.findViewById<TextView>(com.bebas.expensetracker.R.id.tvBudgetInfo)
        val progressBar =
            dialogView.findViewById<ProgressBar>(com.bebas.expensetracker.R.id.budgetProgressBar)

        val budgetNames = mutableListOf<String>()
        val budgetIds = mutableListOf<Int>()
        val budgetLimits = mutableMapOf<Int, Int>()
        val budgetUsed = mutableMapOf<Int, Int>()
        var selectedBudgetId: Int = -1

        budgetViewModel.getBudgetsForUser(userId).observe(viewLifecycleOwner) { budgets ->
            if (budgets.isEmpty()) {
                Toast.makeText(requireContext(), "Tidak ada budget tersedia", Toast.LENGTH_SHORT)
                    .show()
                return@observe
            }

            budgetNames.clear()
            budgetIds.clear()
            budgetLimits.clear()
            budgets.forEach {
                budgetNames.add(it.name)
                budgetIds.add(it.id)
                budgetLimits[it.id] = it.amount
            }

            val spinnerAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, budgetNames)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = spinnerAdapter

            // Set spinner selection + listener
            spinner.setSelection(0)
            spinner.onItemSelectedListener =
                object : android.widget.AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: android.widget.AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        selectedBudgetId = budgetIds[position]
                        val limit = budgetLimits[selectedBudgetId] ?: 0

                        expenseViewModel.getTotalUsedByBudget(selectedBudgetId) { used ->
                            budgetUsed[selectedBudgetId] = used
                            tvBudgetInfo.text = "Budget: ${budgetNames[position]} - Rp %,d / Rp %,d".format(used, limit)
                            progressBar.max = limit
                            progressBar.progress = used
                        }
                    }

                    override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
                }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Pengeluaran")
            .setView(dialogView)
            .setPositiveButton("Tambah") { _, _ ->
                val amountStr = etAmount.text.toString().trim()
                val note = etNote.text.toString().trim()

                if (amountStr.isEmpty() || selectedBudgetId == -1) {
                    Toast.makeText(requireContext(), "Data tidak lengkap", Toast.LENGTH_SHORT)
                        .show()
                    return@setPositiveButton
                }

                val nominal = amountStr.toIntOrNull() ?: 0
                if (nominal <= 0) {
                    Toast.makeText(requireContext(), "Nominal tidak valid", Toast.LENGTH_SHORT)
                        .show()
                    return@setPositiveButton
                }

                val limit = budgetLimits[selectedBudgetId] ?: 0
                val used = budgetUsed[selectedBudgetId] ?: 0

                if (nominal + used > limit) {
                    Toast.makeText(
                        requireContext(),
                        "Nominal melebihi batas budget",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val expense = Expense(
                    userId = userId,
                    budgetId = selectedBudgetId,
                    amount = nominal,
                    description = note,
                    timestamp = System.currentTimeMillis()
                )

                expenseViewModel.insert(expense)
                Toast.makeText(requireContext(), "Pengeluaran ditambahkan", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
