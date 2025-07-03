package com.bebas.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.bebas.expensetracker.model.AppDatabase
import com.bebas.expensetracker.model.Expense
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ExpenseViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    private val dao = AppDatabase.getInstance(application).expenseDao()
    val allExpenses: LiveData<List<Expense>> = dao.getAllExpenses()

    fun getExpensesForUser(userId: Int): LiveData<List<Expense>> {
        return dao.getExpensesByUser(userId)
    }

    fun insert(expense: Expense) {
        launch {
            dao.insert(expense)
        }
    }

    fun getTotalExpenseForBudget(budgetId: Int, callback: (Int) -> Unit) {
        launch {
            val total = dao.getTotalExpenseForBudget(budgetId) ?: 0
            withContext(Dispatchers.Main) {
                callback(total)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
