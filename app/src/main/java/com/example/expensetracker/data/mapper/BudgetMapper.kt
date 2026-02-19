package com.example.expensetracker.data.mapper

import com.example.expensetracker.data.local.BudgetEntity
import com.example.expensetracker.data.model.Budget


fun BudgetEntity.toBudget(): Budget {
    return Budget(
        monthStart = monthStart,
        amount = amount
    )
}

fun Budget.toEntity(): BudgetEntity {
    return BudgetEntity(
        monthStart = monthStart,
        amount = amount
    )
}
