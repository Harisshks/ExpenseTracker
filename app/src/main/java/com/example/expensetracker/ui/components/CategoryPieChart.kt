package com.example.expensetracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.CategoryTotal


private fun safeColorFromInt(colorInt: Int): Color {
    return try {
        val argbColor = if (colorInt == 0 || (colorInt and 0xFF000000.toInt()) == 0) {
            (0xFF000000.toInt() or (colorInt and 0x00FFFFFF))
        } else {
            colorInt
        }
        Color(argbColor)
    } catch (e: Throwable) {
        Color.Gray
    }
}

@Composable
fun CategoryPieChart(
    data: List<CategoryTotal>,
    categories: List<Category>,
    modifier: Modifier = Modifier
) {
    val total = data.sumOf { it.total }


    val categoryColors = categories.associate { category ->
        category.name to safeColorFromInt(category.color)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.size(220.dp)) {
            var startAngle = -90f

            data.forEach { item ->
                val sweepAngle = (item.total / total * 360f).toFloat()
                val color = categoryColors[item.category] ?: Color.Gray

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )
                startAngle += sweepAngle
            }
        }

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            data.forEach { item ->
                val color = categoryColors[item.category] ?: Color.Gray

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(color, CircleShape)
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = item.category,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "₹ %.2f".format(item.total),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

