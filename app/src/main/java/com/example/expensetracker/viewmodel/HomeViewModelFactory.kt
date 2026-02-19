package com.example.expensetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.expensetracker.data.repository.AccountRepository
import com.example.expensetracker.data.repository.BudgetRepository
import com.example.expensetracker.data.repository.CategoryRepository
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.data.repository.TagRepository

class HomeViewModelFactory(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val accountRepository: AccountRepository,
    private val tagRepository: TagRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                expenseRepository = expenseRepository,
                categoryRepository = categoryRepository,
                budgetRepository = budgetRepository,
                accountRepository = accountRepository,
                tagRepository = tagRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

