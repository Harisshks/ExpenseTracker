package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Tag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagSelector(
    allTags: List<Tag>,
    selectedTagIds: List<Int>,
    onSelectionChange: (List<Int>) -> Unit,
    onAddTag: (String) -> Unit,
    onDeleteTag: (Tag) -> Unit
) {

    var showSheet by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var newTagName by remember { mutableStateOf("") }
    var tagToDelete by remember { mutableStateOf<Tag?>(null) }


    val selectedTagNames = allTags
        .filter { selectedTagIds.contains(it.id) }
        .joinToString(", ") { it.name }

    Text("Tags", style = MaterialTheme.typography.titleMedium)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showSheet = true }
            .background(Color.White),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = selectedTagNames.ifEmpty { "Select tags" },
                modifier = Modifier.weight(1f)
            )

            Icon(Icons.Default.ArrowDropDown, null)
        }
    }


    if (showSheet) {

        ModalBottomSheet(
            onDismissRequest = { showSheet = false }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text("Select Tags", style = MaterialTheme.typography.titleMedium)

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Tags") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                val filteredTags = allTags.filter {
                    it.name.contains(searchQuery, true)
                }

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredTags) { tag ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Checkbox(
                                checked = selectedTagIds.contains(tag.id),
                                onCheckedChange = { checked ->
                                    val updated =
                                        if (checked)
                                            selectedTagIds + tag.id
                                        else
                                            selectedTagIds - tag.id

                                    onSelectionChange(updated)
                                }
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                tag.name,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = { tagToDelete = tag }
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

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add Tag")
                    }

                    Button(
                        onClick = { showSheet = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }

    if (showAddDialog) {

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Tag") },
            text = {
                OutlinedTextField(
                    value = newTagName,
                    onValueChange = { newTagName = it },
                    placeholder = { Text("Enter tag name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val name = newTagName.trim()
                        if (name.isNotEmpty()) {
                            onAddTag(name)
                            newTagName = ""
                            showAddDialog = false
                        }
                    }
                ) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }


    if (tagToDelete != null) {

        AlertDialog(
            onDismissRequest = { tagToDelete = null },
            title = { Text("Delete Tag") },
            text = {
                Text("Delete '${tagToDelete!!.name}'?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTag(tagToDelete!!)
                        onSelectionChange(
                            selectedTagIds - tagToDelete!!.id
                        )
                        tagToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { tagToDelete = null }
                ) { Text("Cancel") }
            }
        )
    }
}
