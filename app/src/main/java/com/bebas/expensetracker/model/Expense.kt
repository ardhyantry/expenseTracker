package com.bebas.expensetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val budgetId: Int,
    val amount: Int,
    val description: String,
    val timestamp: Long
)