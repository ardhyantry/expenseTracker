package com.bebas.expensetracker.view.main.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bebas.expensetracker.databinding.FragmentReportBinding
import com.bebas.expensetracker.model.Budget
import com.bebas.expensetracker.view.main.adapter.ReportAdapter
import com.bebas.expensetracker.view.main.adapter.ReportItem
import com.bebas.expensetracker.viewmodel.BudgetViewModel
import com.bebas.expensetracker.viewmodel.ExpenseViewModel

class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding
    private lateinit var reportAdapter: ReportAdapter
    private lateinit var budgetViewModel: BudgetViewModel
    private lateinit var expenseViewModel: ExpenseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        budgetViewModel = ViewModelProvider(this)[BudgetViewModel::class.java]
        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
        reportAdapter = ReportAdapter()

        binding.rvReport.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReport.adapter = reportAdapter

        // Pantau semua budget
        budgetViewModel.allBudgets.observe(viewLifecycleOwner) { budgets ->
            val reportList = mutableListOf<ReportItem>()
            var totalPengeluaran = 0
            var counter = 0

            budgets.forEach { budget ->
                expenseViewModel.getTotalExpenseForBudget(budget.id) { totalUsed ->
                    val item = ReportItem(budget, totalUsed)
                    reportList.add(item)
                    totalPengeluaran += totalUsed
                    counter++

                    // Saat semua budget sudah diproses, tampilkan
                    if (counter == budgets.size) {
                        reportAdapter.submitList(reportList)
                        binding.tvTotalPengeluaran.text = "Total Pengeluaran: Rp $totalPengeluaran"
                    }
                }
            }
        }
    }
}
