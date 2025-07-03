package com.bebas.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.bebas.expensetracker.model.Expense
import com.bebas.expensetracker.model.AppDatabase
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).expenseDao()
    val allExpenses: LiveData<List<Expense>> = dao.getAllExpenses()

    fun insertExpense(expense: Expense) {
        viewModelScope.launch {
            dao.insert(expense)
        }
    }

    fun getTotalExpenseForBudget(budgetId: Int, callback: (Int) -> Unit) {
        viewModelScope.launch {
            val total = dao.getTotalExpenseForBudget(budgetId) ?: 0
            callback(total)
        }
    }
}
