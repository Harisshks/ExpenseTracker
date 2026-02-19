package com.example.expensetracker.ui.navigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.ui.components.ExpenseBottomBar
import com.example.expensetracker.ui.components.TopBar
import com.example.expensetracker.viewmodel.HomeViewModel

@Composable
fun MainScreen(homeViewModel: HomeViewModel) {

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val context = LocalContext.current


    BackHandler {
        if (currentRoute != Routes.Home.route) {
            navController.navigate(Routes.Home.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        } else {
            (context as? Activity)?.finish()
        }
    }

    Scaffold(
        topBar = { TopBar(currentRoute) },
        bottomBar = {
            if (currentRoute == Routes.Home.route ||
                currentRoute == Routes.Analytics.route || currentRoute == Routes.Accounts.route
            ) {
                ExpenseBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { padding ->

        ExpenseNavGraph(
            navController = navController,
            homeViewModel = homeViewModel,
            padding = padding
        )
    }
}
