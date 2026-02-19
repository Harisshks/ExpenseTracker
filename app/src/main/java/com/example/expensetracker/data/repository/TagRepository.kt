package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.TagDao
import com.example.expensetracker.data.mapper.toEntity
import com.example.expensetracker.data.mapper.toTag
import com.example.expensetracker.data.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TagRepository(
    private val tagDao: TagDao
) {

    fun getAllTags(): Flow<List<Tag>> {
        return tagDao.getAllTags()
            .map { list ->
                list.map { it.toTag() }
            }
    }

    suspend fun addTag(tag: Tag) {
        tagDao.insertTag(tag.toEntity())
    }

    suspend fun deleteTag(tag: Tag) {
        tagDao.deleteTag(tag.toEntity())
    }
}
