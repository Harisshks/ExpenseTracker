package com.example.expensetracker.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.ui.components.AddAccountDialog
import com.example.expensetracker.ui.components.EditAccountDialog
import com.example.expensetracker.ui.theme.AppTeal
import com.example.expensetracker.viewmodel.DeleteAccountEvent
import com.example.expensetracker.viewmodel.HomeViewModel

@Composable
fun AccountsScreen(
    viewModel: HomeViewModel
) {

    val context = LocalContext.current
    val accounts by viewModel.accounts.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var accountToEdit by remember { mutableStateOf<Account?>(null) }
    var accountPendingDelete by remember { mutableStateOf<Account?>(null) }
    var showReassignDialog by remember { mutableStateOf(false) }

    val totalBalance = accounts.sumOf { it.balance }



    LaunchedEffect(Unit) {
        viewModel.deleteAccountEvent.collect { result ->
            when (result) {
                is DeleteAccountEvent.SystemAccount -> {
                    Toast.makeText(
                        context,
                        "System account cannot be deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is DeleteAccountEvent.InUse -> {
                    accountPendingDelete = result.account
                    showReassignDialog = true
                }

                is DeleteAccountEvent.Success -> {
                    Toast.makeText(
                        context,
                        "Account deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {


            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    )

                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Total Balance",color = Color(0xFF1B5E20))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "₹ %.2f".format(totalBalance),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }

            items(accounts) { account ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                )
                {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = account.name,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = "₹ %.2f".format(account.balance),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }


                        IconButton(
                            onClick = { accountToEdit = account }
                        ) {
                            Icon(Icons.Default.Edit, null)
                        }

                        if (account.isSystem) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            IconButton(
                                onClick = {
                                    viewModel.deleteAccount(account)
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
              //  .background(AppTeal),
            containerColor = AppTeal,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, null)
        }
    }

    if (showAddDialog) {
        AddAccountDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, balance,type ->
                viewModel.addAccount(name, balance,type)
                showAddDialog = false
            }
        )
    }

    accountToEdit?.let { account ->
        EditAccountDialog(
            account = account,
            onDismiss = { accountToEdit = null },
            onSave = {
                viewModel.updateAccount(it)
                accountToEdit = null
            }
        )
    }

    if (showReassignDialog && accountPendingDelete != null) {
        AlertDialog(
            onDismissRequest = { showReassignDialog = false },
            title = { Text("Account In Use") },
            text = {
                Text(
                    "This account is used in expenses.\n\n" +
                            "Do you want to delete it and reassign its expenses to the default account?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.forceDeleteAndReassign(
                            accountPendingDelete!!
                        )
                        showReassignDialog = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showReassignDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

