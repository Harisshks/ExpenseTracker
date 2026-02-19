package com.example.expensetracker.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val id: Int,
    val name: String,
    val color: Int,
    val icon: ImageVector
)

val defaultCategories = listOf(
    Category(id = 1, name = "Food", color = 0xFFFFA726.toInt(), icon = Icons.Default.Restaurant),
    Category(id = 2, name = "Transport", color = 0xFF42A5F5.toInt(), icon = Icons.Default.DirectionsCar),
    Category(id = 3, name = "Shopping", color = 0xFF66BB6A.toInt(), icon = Icons.Default.ShoppingCart),
    Category(id = 4, name = "Entertainment", color = 0xFFAB47BC.toInt(), icon = Icons.Default.Movie),
    Category(id = 5, name = "Health", color = 0xFFEF5350.toInt(), icon = Icons.Default.Favorite) ,
    Category(id = 6, name =  "Bills", color = 0xFF8D6E63.toInt(), icon = Icons.Default.Payment),
    Category(id = 7, name = "Education", color = 0xFF5C6BC0.toInt(), icon = Icons.Default.School),
    Category(id = 8, name = "Other", color = 0xFF9E9E9E.toInt(), icon = Icons.Default.Category)
)
