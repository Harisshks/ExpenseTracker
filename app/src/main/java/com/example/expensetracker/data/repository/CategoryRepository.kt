package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.CategoryDao
import com.example.expensetracker.data.local.CategoryEntity
import com.example.expensetracker.data.mapper.toCategory
import com.example.expensetracker.data.model.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val categoryDao: CategoryDao
) {


    fun getAllCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    suspend fun getAllCategoriesOnce(): List<Category> {
        return categoryDao.getAllCategoriesOnce().map { it.toCategory() }
    }


    suspend fun insertCategory(category: List<CategoryEntity>) {
        categoryDao.insertCategory(category)
    }


}
