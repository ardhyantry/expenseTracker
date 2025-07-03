package com.bebas.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.bebas.expensetracker.model.AppDatabase
import com.bebas.expensetracker.model.Budget
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class BudgetViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    private val budgetDao = AppDatabase.getInstance(application).budgetDao()

    val allBudgets: LiveData<List<Budget>> = budgetDao.getAllBudgets()

    fun insert(budget: Budget) {
        launch {
            budgetDao.insert(budget)
        }
    }

    fun update(budget: Budget) {
        launch {
            budgetDao.update(budget)
        }
    }

    fun delete(budget: Budget) {
        launch {
            budgetDao.delete(budget)
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
    fun getBudgetById(budgetId: Int, callback: (Budget?) -> Unit) {
        launch {
            val budget = budgetDao.getBudgetById(budgetId)
            withContext(Dispatchers.Main) {
                callback(budget)
            }
        }
    }


    fun getBudgetsForUser(userId: Int): LiveData<List<Budget>> {
        return budgetDao.getBudgetsByUser(userId)
    }

}