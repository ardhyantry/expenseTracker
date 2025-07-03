package com.bebas.expensetracker.view.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bebas.expensetracker.databinding.ItemReportBinding
import com.bebas.expensetracker.model.Budget

data class ReportItem(
    val budget: Budget,
    val totalUsed: Int
)

class ReportAdapter : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    private val reportItems = mutableListOf<ReportItem>()

    fun submitList(data: List<ReportItem>) {
        reportItems.clear()
        reportItems.addAll(data)
        notifyDataSetChanged()
    }

    inner class ReportViewHolder(private val binding: ItemReportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReportItem) {
            val max = item.budget.amount
            val used = item.totalUsed
            val remaining = (max - used).coerceAtLeast(0)  // mencegah nilai negatif

            val progress = if (max > 0) (used * 100 / max) else 0

            binding.tvBudgetName.text = item.budget.name
            binding.tvUsage.text = "Terpakai: Rp $used dari Rp $max"
            binding.tvRemaining.text = "Sisa: Rp $remaining"
            binding.progressBar.max = 100
            binding.progressBar.progress = progress
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(reportItems[position])
    }

    override fun getItemCount(): Int = reportItems.size
}
