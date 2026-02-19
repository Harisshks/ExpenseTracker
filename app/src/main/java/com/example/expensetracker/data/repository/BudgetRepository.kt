package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.BudgetDao
import com.example.expensetracker.data.mapper.toBudget
import com.example.expensetracker.data.mapper.toEntity
import com.example.expensetracker.data.model.Budget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class BudgetRepository(
    private val budgetDao: BudgetDao
) {

   fun getBudget(start: Long): Flow<Budget?> {
        return budgetDao.getBudget(start)
            .map { it?.toBudget() }
    }

    suspend fun setBudget(budget: Budget) {
        budgetDao.setBudget(budget.toEntity())
    }
}
