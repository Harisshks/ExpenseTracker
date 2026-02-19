package com.example.expensetracker.ui.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.ui.components.AccountDropdown
import com.example.expensetracker.ui.components.CategoryDropdown
import com.example.expensetracker.ui.components.TagSelector
import com.example.expensetracker.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    viewModel: HomeViewModel,
    expenseToEdit: Expense?,
    onSaveDone: () -> Unit,
    onCancel: () -> Unit
) {

    val context = LocalContext.current
    val isEdit = expenseToEdit != null

    val categories by viewModel.categories.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val tags by viewModel.tags.collectAsState()

    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedAccount by remember { mutableStateOf<Account?>(null) }
    var selectedTagIds by remember {
        mutableStateOf(expenseToEdit?.tagIds ?: emptyList())
    }

    var title by remember { mutableStateOf(expenseToEdit?.title ?: "") }
    var amount by remember { mutableStateOf(expenseToEdit?.amount?.toString() ?: "") }
    var date by remember { mutableStateOf(expenseToEdit?.date ?: System.currentTimeMillis()) }

    var amountError by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }
    val dateFormatter = remember {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    }



    LaunchedEffect(categories, accounts, expenseToEdit) {

        if (categories.isNotEmpty() && selectedCategory == null) {
            selectedCategory = expenseToEdit?.let { exp ->
                categories.find { it.id == exp.categoryId }
            } ?: categories.firstOrNull{ it.name != "All" }
        }

        if (accounts.isNotEmpty() && selectedAccount == null) {
            selectedAccount = expenseToEdit?.let { exp ->
                accounts.find { it.id == exp.accountId }
            } ?: accounts.firstOrNull()
        }
    }

    // Date Picker

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                date = calendar.timeInMillis
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Expense Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { input ->
                    if (input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                        amount = input
                        amountError = null
                    }
                },
                label = { Text("Amount") },
                leadingIcon = { Text("₹") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = amountError != null,
                supportingText = {
                    amountError?.let { Text(it) }
                },
                modifier = Modifier.fillMaxWidth()
            )

            CategoryDropdown(
                categories = categories,
                selected = selectedCategory?.name ?: "",
                onSelected = { name ->
                    selectedCategory = categories.find { it.name == name }
                }
            )

            AccountDropdown(
                accounts = accounts,
                selected = selectedAccount?.name ?: "",
                onSelected = { name ->
                    selectedAccount = accounts.find { it.name == name }
                }
            )

            TagSelector(
                allTags = tags,
                selectedTagIds = selectedTagIds,
                onSelectionChange = { selectedTagIds = it },
                onAddTag = { viewModel.addTag(it) },
                onDeleteTag = { viewModel.deleteTag(it) }
            )

            OutlinedTextField(
                value = dateFormatter.format(Date(date)),
                onValueChange = {},
                readOnly = true,
                label = { Text("Date") },
                trailingIcon = {
                    IconButton(onClick = {
                        calendar.timeInMillis = date
                        datePickerDialog.show()
                    }) {
                        Icon(Icons.Default.DateRange, null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(24.dp))

        Row {

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Spacer(Modifier.width(16.dp))

            Button(
                onClick = {

                    val amt = amount.toDoubleOrNull()
                    val category = selectedCategory
                    val account = selectedAccount

                    when {
                        title.isBlank() -> return@Button
                        amt == null -> {
                            amountError = "Enter a valid amount"
                            return@Button
                        }
                        amt <= 0 -> {
                            amountError = "Amount must be greater than 0"
                            return@Button
                        }
                        category == null || account == null -> return@Button
                    }

                    if (isEdit) {
                        viewModel.updateExpense(
                            expenseToEdit!!.copy(
                                title = title,
                                amount = amt,
                                date = date,
                                categoryId = category.id,
                                categoryName = category.name,
                                categoryColor = category.color,
                                categoryIcon = category.icon,
                                accountId = account.id,
                                tagIds = selectedTagIds
                            )
                        )
                    } else {
                        viewModel.addExpense(
                            title = title,
                            amount = amt,
                            category = category,
                            accountId = account.id,
                            tagIds = selectedTagIds,
                            date = date
                        )
                    }

                    onSaveDone()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (isEdit) "Update" else "Save")
            }
        }

        // Delete Button

        if (isEdit) {

            Spacer(Modifier.height(20.dp))

            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete Expense")
            }
        }
    }

    // Delete Confirmation Dialog

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Expense") },
            text = { Text("Are you sure you want to delete this expense?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteExpense(expenseToEdit!!)
                        showDeleteDialog = false
                        onSaveDone()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
