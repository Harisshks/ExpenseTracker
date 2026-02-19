package com.example.expensetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun MonthTotalCard(viewModel: HomeViewModel) {

    val monthRange by viewModel.selectedMonth.collectAsState()
    val budget by viewModel.budget.collectAsState()
    val spent by viewModel.monthTotal.collectAsState()
    val remaining by viewModel.remainingBudget.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    val start = monthRange.first

    val monthText = remember(start) {
        SimpleDateFormat("MMMM, yyyy", Locale.getDefault())
            .format(Date(start))
    }

    val calendarNow = Calendar.getInstance()
    val calendarSelected = Calendar.getInstance().apply {
        timeInMillis = start
    }

    val isCurrentMonth =
        calendarNow.get(Calendar.MONTH) == calendarSelected.get(Calendar.MONTH) &&
                calendarNow.get(Calendar.YEAR) == calendarSelected.get(Calendar.YEAR)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { changeMonth(start, -1, viewModel) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.ArrowBackIos, null, tint = Color(0xFF1B5E20))
                }

                Text(
                    text = monthText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1B5E20),
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                IconButton(
                    onClick = {
                        if (!isCurrentMonth) changeMonth(start, 1, viewModel)
                    },
                    enabled = !isCurrentMonth,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.ArrowForwardIos, null, tint = Color(0xFF1B5E20))
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Monthly Budget",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Text(
                text = "₹ ${"%,.0f".format(budget?.amount ?: 0.0)}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = if (budget != null && budget!!.amount > 0)
                    (spent / budget!!.amount).toFloat().coerceIn(0f, 1f)
                else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            )

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Spent: ₹ ${"%,.0f".format(spent)}",
                    fontSize = 13.sp
                )
                remaining?.let {
                    Text(
                        "Remaining: ₹ ${"%,.0f".format(it)}",
                        fontSize = 13.sp,
                        color = if (it < 0) Color.Red else Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Edit Budget",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { showDialog = true }
            )
        }
    }

    if (showDialog) {
        SetBudgetDialog(
            current = budget?.amount,
            onDismiss = { showDialog = false },
            onSave = {
                viewModel.setBudget(it)
                showDialog = false
            }
        )
    }
}

@Composable
fun SetBudgetDialog(
    current: Double?,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var amount by remember { mutableStateOf(current?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val value = amount.toDoubleOrNull()
                    if (value != null && value > 0) {
                        onSave(value)
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Set Monthly Budget") },
        text = {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Budget Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    )
}


fun changeMonth(
    currentStart: Long,
    offset: Int,
    viewModel: HomeViewModel
) {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = currentStart
        add(Calendar.MONTH, offset)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val start = calendar.timeInMillis

    calendar.add(Calendar.MONTH, 1)
    val end = calendar.timeInMillis

    viewModel.changeMonth(start, end)
}
