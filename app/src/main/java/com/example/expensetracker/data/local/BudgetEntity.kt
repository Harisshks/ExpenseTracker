package com.example.expensetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget")
data class BudgetEntity(
    @PrimaryKey
    val monthStart: Long,
    val amount: Double
)
