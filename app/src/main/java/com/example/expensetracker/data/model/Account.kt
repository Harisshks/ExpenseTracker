package com.example.expensetracker.data.model

data class Account(
    val id: Int = 0,
    val name: String,
    val type: AccountType,
    val balance: Double,
    val isSystem: Boolean = false
)
