package com.bebas.expensetracker.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BudgetDao {
    @Insert
    fun insert(budget: Budget)

    @Update
    fun update(budget: Budget)

    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): LiveData<List<Budget>>

    @Query("SELECT * FROM budgets WHERE id = :id")
    fun getBudgetById(id: Int): Budget

    @Delete
    fun delete(budget: Budget)

}