package com.bebas.expensetracker.view.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bebas.expensetracker.R
import com.bebas.expensetracker.model.Budget

class BudgetAdapter(
    private var budgetList: List<Budget>,
    private val onItemClick: (Budget) -> Unit
) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    inner class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvBudgetName)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvBudgetAmount)

        fun bind(budget: Budget) {
            tvName.text = budget.name
            tvAmount.text = "Rp ${budget.amount}"

            itemView.setOnClickListener {
                onItemClick(budget)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.bind(budgetList[position])
    }

    override fun getItemCount(): Int = budgetList.size

    fun updateList(newList: List<Budget>) {
        budgetList = newList
        notifyDataSetChanged()
    }
}
