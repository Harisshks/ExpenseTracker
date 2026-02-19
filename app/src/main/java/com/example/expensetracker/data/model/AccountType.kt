package com.example.expensetracker.data.model


enum class AccountType(val displayName: String) {

    CASH("Cash"),
    BANK("Bank"),
    CREDIT_CARD("Credit Card"),
    UPI("UPI"),
    SAVINGS("Savings");

    companion object {
        fun fromName(name: String): AccountType {
            return try {
                valueOf(name)
            } catch (e: Exception) {
                CASH
            }
        }
    }
}
