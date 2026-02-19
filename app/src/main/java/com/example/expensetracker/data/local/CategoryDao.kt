package com.example.expensetracker.data.local


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: List<CategoryEntity>)

    @Query("""
        SELECT * FROM categories
        ORDER BY name ASC
    """)
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("""
        SELECT * FROM categories
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun getCategoryById(id: Int): CategoryEntity?

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    suspend fun getAllCategoriesOnce(): List<CategoryEntity>

}
