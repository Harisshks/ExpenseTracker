package com.example.expensetracker.data.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

object IconMapper {
    private val iconMap: Map<String, ImageVector> = mapOf(
        "ic_food" to Icons.Default.Restaurant,
        "ic_transport" to Icons.Default.DirectionsCar,
        "ic_shopping" to Icons.Default.ShoppingCart,
        "ic_entertainment" to Icons.Default.Movie,
        "ic_health" to Icons.Default.Favorite,
        "ic_bills" to Icons.Default.Payment,
        "ic_education" to Icons.Default.School,
        "ic_other" to Icons.Default.Category
    )

    private val reverseMap: Map<ImageVector, String> = iconMap.entries.associate { (k, v) -> v to k }

    fun getIcon(iconName: String): ImageVector {
        return iconMap[iconName] ?: Icons.Default.Category
    }
    fun getIconName(icon: ImageVector): String {
        return reverseMap[icon] ?: "ic_food"
    }
}