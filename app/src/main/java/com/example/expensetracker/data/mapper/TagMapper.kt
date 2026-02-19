package com.example.expensetracker.data.mapper

import com.example.expensetracker.data.local.TagEntity
import com.example.expensetracker.data.model.Tag

fun TagEntity.toTag(): Tag {
    return Tag(
        id = id,
        name = name
    )
}

fun Tag.toEntity(): TagEntity {
    return TagEntity(
        id = id,
        name = name
    )
}
