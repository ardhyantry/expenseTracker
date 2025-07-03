package com.bebas.expensetracker.view.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bebas.expensetracker.databinding.ItemExpenseBinding
import com.bebas.expensetracker.model.Expense
import com.bebas.expensetracker.util.DateUtils

class ExpenseAdapter(
    private val onNominalClick: (Expense) -> Unit,
    private val budgetIdToNameMap: Map<Int, String>
) : ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(DiffCallback()) {

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(expense: Expense) {
            binding.tvTanggal.text = DateUtils.formatDate(expense.timestamp)
            binding.tvNominal.text = "Rp %,d".format(expense.amount)
            binding.chipBudget.text = budgetIdToNameMap[expense.budgetId] ?: "Unknown"

            // Klik nominal ➜ buka detail
            binding.tvNominal.setOnClickListener {
                onNominalClick(expense)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Expense>() {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Expense, newItem: Expense) = oldItem == newItem
    }
}
