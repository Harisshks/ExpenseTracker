package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountDialog(
    onDismiss: () -> Unit,
    onSave: (String, Double, AccountType) -> Unit
) {

    var name by remember { mutableStateOf("") }
    var balanceText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AccountType.CASH) }
    var expanded by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var balanceError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Account") },
        confirmButton = {
            Button(
                onClick = {
                    nameError = null
                    balanceError = null

                    val trimmedName = name.trim()
                    val balance = balanceText.toDoubleOrNull()

                    when {
                        trimmedName.isEmpty() -> {
                            nameError = "Account name cannot be empty"
                        }

                        balanceText.isEmpty() -> {
                            balanceError = "Balance is required"
                        }

                        balance == null -> {
                            balanceError = "Balance must be a valid number"
                        }

                        else -> {
                            onSave(trimmedName, balance, selectedType)
                        }
                    }
                }
            ) {
                Text("Save")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    label = { Text("Account Name") },
                    singleLine = true,
                    isError = nameError != null,
                    supportingText = {
                        nameError?.let { Text(it) }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { input ->
                        if (input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            balanceText = input
                            balanceError = null
                        }
                    },
                    label = { Text("Starting Balance") },
                    singleLine = true,
                    isError = balanceError != null,
                    supportingText = {
                        balanceError?.let { Text(it) }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {

                    OutlinedTextField(
                        value = selectedType.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Account Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        AccountType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccountDialog(
    account: Account,
    onDismiss: () -> Unit,
    onSave: (Account) -> Unit
) {

    var name by remember { mutableStateOf(account.name) }
    var balanceText by remember { mutableStateOf(account.balance.toString()) }
    var selectedType by remember { mutableStateOf(account.type) }
    var expanded by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var balanceError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Account") },

        confirmButton = {
            Button(
                onClick = {

                    nameError = null
                    balanceError = null

                    val trimmedName = name.trim()
                    val balance = balanceText.toDoubleOrNull()

                    when {
                        trimmedName.isEmpty() -> {
                            nameError = "Account name cannot be empty"
                        }

                        balanceText.isEmpty() -> {
                            balanceError = "Balance is required"
                        }

                        balance == null -> {
                            balanceError = "Balance must be a valid number"
                        }

                        else -> {
                            val updated = account.copy(
                                name = trimmedName,
                                balance = balance,
                                type = selectedType
                            )
                            onSave(updated)
                        }
                    }
                }
            ) {
                Text("Update")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },

        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    label = { Text("Account Name") },
                    singleLine = true,
                    isError = nameError != null,
                    supportingText = {
                        nameError?.let { Text(it) }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { input ->
                        if (input.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            balanceText = input
                            balanceError = null
                        }
                    },
                    label = { Text("Balance") },
                    singleLine = true,
                    isError = balanceError != null,
                    supportingText = {
                        balanceError?.let { Text(it) }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {

                    OutlinedTextField(
                        value = selectedType.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Account Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        AccountType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}
