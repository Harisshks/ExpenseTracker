package com.example.expensetracker.ui.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {

    Box {

        BottomAppBar(
            containerColor = Color(0xFFE8F5E9),
            modifier = Modifier.height(70.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                BottomBarItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    selected = currentRoute == Routes.Home.route,
                    onClick = { onNavigate(Routes.Home.route) }
                )

                BottomBarItem(
                    icon = Icons.Default.AccountBalance,
                    label = "Accounts",
                    selected = currentRoute == Routes.Accounts.route,
                    onClick = { onNavigate(Routes.Accounts.route) }
                )

                BottomBarItem(
                    icon = Icons.Default.BarChart,
                    label = "Analytics",
                    selected = currentRoute == Routes.Analytics.route,
                    onClick = { onNavigate(Routes.Analytics.route) }
                )
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color =
        if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(6.dp)
    ) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(26.dp))
        Text(
            label,
            color = color,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
