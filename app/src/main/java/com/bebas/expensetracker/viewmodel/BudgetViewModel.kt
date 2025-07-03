package com.bebas.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.bebas.expensetracker.model.AppDatabase
import com.bebas.expensetracker.model.Budget
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val budgetDao = AppDatabase.getInstance(application).budgetDao()
    val allBudgets: LiveData<List<Budget>> = budgetDao.getAllBudgets()

    fun insert(budget: Budget) = viewModelScope.launch {
        budgetDao.insert(budget)
    }

    fun update(budget: Budget) = viewModelScope.launch {
        budgetDao.update(budget)
    }
}
