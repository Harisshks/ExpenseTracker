package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.ui.theme.AppTeal

@Composable
fun CategoryChips(
    categories: List<Category>,
    selected: String,
    onSelected: (String) -> Unit
) {
    LazyRow {
        items(categories) { category ->
            val isSelected = selected == category.name

            FilterChip(
                selected = isSelected,
                onClick = { onSelected(category.name) },
                label = { Text(category.name) },
                leadingIcon = {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = category.name,
                        modifier = Modifier.size(18.dp),
                        tint = if (isSelected) Color.White
                        else MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppTeal,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}
