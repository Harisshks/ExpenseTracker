package com.example.expensetracker.data.local


import androidx.room.*
import com.example.expensetracker.data.model.CategoryTotal
import kotlinx.coroutines.flow.Flow


@Dao
interface ExpenseDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity):Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Transaction
    @Query("""
        SELECT * FROM expenses
        WHERE date BETWEEN :start AND :end
        ORDER BY date DESC
    """)
    fun getAllExpenses(
        start: Long,
        end: Long
    ): Flow<List<ExpenseWithDetails>>



    @Query("""
        SELECT IFNULL(SUM(amount), 0) FROM expenses
        WHERE date BETWEEN :startOfMonth AND :endOfMonth
    """)
    fun getTotalExpensesThisMonth(
        startOfMonth: Long,
        endOfMonth: Long
    ): Flow<Double>


    @Query("""
        SELECT c.name as category, IFNULL(SUM(e.amount),0) as total
        FROM expenses e
        INNER JOIN categories c ON e.categoryId = c.id
        WHERE e.date BETWEEN :startOfMonth AND :endOfMonth
        GROUP BY e.categoryId
    """)
    fun getCategoryWiseExpenses(
        startOfMonth: Long,
        endOfMonth: Long
    ): Flow<List<CategoryTotal>>

    @Query("""
    UPDATE expenses 
    SET accountId = :newAccountId 
    WHERE accountId = :oldAccountId
""")
    suspend fun reassignAccount(
        oldAccountId: Int,
        newAccountId: Int
    )

    @Transaction
    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseWithDetailsById(id: Int): ExpenseWithDetails?


    @Query("SELECT SUM(amount) FROM expenses WHERE accountId = :accountId")
    suspend fun getTotalExpensesForAccount(accountId: Int): Double?


}
