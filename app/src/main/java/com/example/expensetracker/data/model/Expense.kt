package com.example.expensetracker.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Expense(
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val date: Long,
    val categoryId: Int,
    val categoryName: String,
    val categoryColor: Int,
    val categoryIcon: ImageVector,
    val tagIds: List<Int> = emptyList(),
    val accountId: Int
)
