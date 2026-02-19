package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.data.local.ExpenseDatabase
import com.example.expensetracker.data.mapper.toEntity
import com.example.expensetracker.data.model.defaultCategories
import com.example.expensetracker.data.repository.*
import com.example.expensetracker.ui.navigation.MainScreen
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.viewmodel.HomeViewModel
import com.example.expensetracker.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ExpenseTrackerTheme {
                val db = ExpenseDatabase.getDatabase(applicationContext)
                val expenseRepository = ExpenseRepository(
                    database = db,
                    expenseDao = db.expenseDao(),
                    crossRefDao = db.expenseTagCrossRefDao()
                )
                val categoryRepository = CategoryRepository(db.categoryDao())
                val budgetRepository = BudgetRepository(db.budgetDao())
                val tagRepository = TagRepository(db.tagDao())
                val accountRepository = AccountRepository(db.accountDao())

                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        val existingCategories =
                            categoryRepository.getAllCategoriesOnce()

                        if (existingCategories.isEmpty()) {
                            categoryRepository.insertCategory(
                                defaultCategories.map { it.toEntity() }
                            )
                        }
                    }
                }

                val factory = HomeViewModelFactory(
                    expenseRepository = expenseRepository,
                    categoryRepository = categoryRepository,
                    budgetRepository = budgetRepository,
                    tagRepository = tagRepository,
                    accountRepository = accountRepository
                )

                val homeViewModel: HomeViewModel =
                    viewModel(factory = factory)

                MainScreen(homeViewModel)
            }
        }
    }
}
