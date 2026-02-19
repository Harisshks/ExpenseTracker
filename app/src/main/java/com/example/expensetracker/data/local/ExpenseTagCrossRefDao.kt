package com.example.expensetracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExpenseTagCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRefs(crossRefs: List<ExpenseTagCrossRef>)

    @Query("DELETE FROM expense_tags WHERE expenseId = :expenseId")
    suspend fun deleteTagsForExpense(expenseId: Int)

    @Query("""
        DELETE FROM expense_tags 
        WHERE expenseId = :expenseId AND tagId = :tagId
    """)
    suspend fun deleteSingleTagFromExpense(expenseId: Int, tagId: Int)

    @Query("SELECT tagId FROM expense_tags WHERE expenseId = :expenseId")
    suspend fun getTagIdsForExpense(expenseId: Int): List<Int>
}
