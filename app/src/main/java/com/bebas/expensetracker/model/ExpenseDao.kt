package com.bebas.expensetracker.model

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bebas.expensetracker.model.Expense

@Dao
interface ExpenseDao {
    @Insert
    fun insert(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND budgetId = :budgetId")
    fun getTotalExpenseForBudget(userId: Int, budgetId: Int): Int?

    @Query("SELECT * FROM expenses WHERE id = :id")
    fun getExpenseById(id: Int): Expense

    @Query("SELECT * FROM expenses WHERE userId = :userId")
    fun getExpensesByUser(userId: Int): LiveData<List<Expense>>
}