package com.example.expensetracker.data.mapper

import com.example.expensetracker.data.local.ExpenseEntity
import com.example.expensetracker.data.local.ExpenseWithDetails
import com.example.expensetracker.data.model.Expense


fun ExpenseWithDetails.toExpense(): Expense {
    return Expense(
        id = expense.id,
        title = expense.title,
        amount = expense.amount,
        date = expense.date,
        categoryId = category.id,
        categoryName = category.name,
        categoryColor = category.color,
        categoryIcon = IconMapper.getIcon(category.icon),
        tagIds = tags.map { it.id },
        accountId = expense.accountId
    )
}


fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        title = title,
        amount = amount,
        date = date,
        categoryId = categoryId,
        accountId = accountId
    )
}


