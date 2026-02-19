package com.example.expensetracker.ui.navigation


sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Accounts : Routes("accounts")
    object Analytics : Routes("analytics")
    object AddExpense : Routes("add")
    object EditExpense : Routes("edit/{expenseId}") {
        fun createRoute(id: Int) = "edit/$id"
    }
    object Search : Routes("search")
}
