package com.example.expensetracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setBudget(budget: BudgetEntity)

    @Query("""
        SELECT * FROM budget
        WHERE monthStart = :start
        LIMIT 1
    """)
    fun getBudget(start: Long): Flow<BudgetEntity?>
}
