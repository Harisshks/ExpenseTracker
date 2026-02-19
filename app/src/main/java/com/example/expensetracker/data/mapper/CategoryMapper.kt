package com.example.expensetracker.data.mapper

import com.example.expensetracker.data.local.CategoryEntity
import com.example.expensetracker.data.model.Category


fun CategoryEntity.toCategory(): Category {
    return Category(
        id = id,
        name = name,
        color = color,
        icon = IconMapper.getIcon(icon)
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        color = color,
        icon = IconMapper.getIconName(icon)
    )
}
