package com.example.expensetracker.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.expensetracker.ui.screen.AccountsScreen
import com.example.expensetracker.ui.screen.AddExpenseScreen
import com.example.expensetracker.ui.screen.AnalyticsScreen
import com.example.expensetracker.ui.screen.HomeScreen
import com.example.expensetracker.ui.screen.SearchScreen
import com.example.expensetracker.viewmodel.HomeViewModel
@Composable
fun ExpenseNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    padding: PaddingValues
) {

    NavHost(
        navController = navController,
        startDestination = Routes.Home.route,
        modifier = Modifier.padding(padding)
    ) {

        composable(Routes.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onItemClick = { expense ->

                    navController.navigate(Routes.EditExpense.createRoute(expense.id))
                },
                onAddClick = {

                    navController.navigate(Routes.AddExpense.route)
                },
                onSearchClick = { navController.navigate(Routes.Search.route) }
            )
        }

        composable(Routes.Accounts.route) {
            AccountsScreen(viewModel = homeViewModel)
        }

        composable(Routes.AddExpense.route) {
            AddExpenseScreen(
                viewModel = homeViewModel,
                onSaveDone = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
                expenseToEdit = null
            )
        }

        composable(
            route = Routes.EditExpense.route,
            arguments = listOf(
                navArgument("expenseId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val id =
                backStackEntry.arguments?.getInt("expenseId") ?: 0

            val expense = homeViewModel.getExpenseById(id)

            AddExpenseScreen(
                viewModel = homeViewModel,
                onSaveDone = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
                expenseToEdit = expense
            )
        }



        composable(Routes.Search.route) {
            SearchScreen(
                viewModel = homeViewModel,
                onBack = { navController.popBackStack() },
                onItemClick = { expense ->
                    navController.navigate(Routes.EditExpense.createRoute(expense.id))
                }
            )
        }

        composable(Routes.Analytics.route) {
            AnalyticsScreen(viewModel = homeViewModel)
        }

    }
}
