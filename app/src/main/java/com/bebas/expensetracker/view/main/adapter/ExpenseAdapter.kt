package com.bebas.expensetracker.view.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bebas.expensetracker.databinding.ItemExpenseBinding
import com.bebas.expensetracker.model.Expense
import com.bebas.expensetracker.util.DateUtils

class ExpenseAdapter(
    private val onItemClick: (Expense) -> Unit,
    private val budgetIdToNameMap: Map<Int, String> = emptyMap()  // untuk tampilkan nama budget
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private val expenses = mutableListOf<Expense>()

    fun submitList(list: List<Expense>) {
        expenses.clear()
        expenses.addAll(list)
        notifyDataSetChanged()
    }

    fun updateBudgetMap(map: Map<Int, String>) {
        (budgetIdToNameMap as MutableMap).clear()
        budgetIdToNameMap.putAll(map)
        notifyDataSetChanged()
    }

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Expense) {
            binding.tvDate.text = DateUtils.formatDate(expense.timestamp)
            binding.tvAmount.text = "Rp ${expense.amount}"
            binding.chipBudget.text = budgetIdToNameMap[expense.budgetId] ?: "Unknown"

            binding.tvAmount.setOnClickListener {
                onItemClick(expense)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    override fun getItemCount(): Int = expenses.size
}
