package com.example.expensetracker.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.ui.components.CategoryChips
import com.example.expensetracker.ui.components.ExpenseItem
import com.example.expensetracker.ui.components.MonthTotalCard
import com.example.expensetracker.ui.components.groupExpenses
import com.example.expensetracker.viewmodel.HomeViewModel

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onItemClick: (Expense) -> Unit,
    onAddClick: () -> Unit,
    onSearchClick: () -> Unit,
) {
    val expenses by viewModel.homeExpenses.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categories by viewModel.categories.collectAsState()


    val groupedExpenses = remember(expenses) {
        groupExpenses(expenses)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(12.dp))

            MonthTotalCard(viewModel)

            Spacer(Modifier.height(16.dp))


            CategoryChips(
                categories = categories,
                selected = selectedCategory
            ) { categoryName ->
                viewModel.setCategory(categoryName)
            }

            Spacer(Modifier.height(12.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Expenses",
                    style = MaterialTheme.typography.titleMedium
                )

                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            }

            if (expenses.isEmpty()) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "No expenses found",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Try adjusting filters or add a new expense",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {

                    groupedExpenses.forEach { (header, expenseList) ->

                        stickyHeader {
                            Text(
                                text = header,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(vertical = 12.dp)
                            )
                        }

                        items(expenseList) { expense ->
                            ExpenseItem(expense) {
                                onItemClick(expense)
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Expense"
            )
        }
    }
}


