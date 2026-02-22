package com.example.expensetracker.data.repository


import androidx.room.withTransaction
import com.example.expensetracker.data.local.ExpenseDao
import com.example.expensetracker.data.local.ExpenseDatabase
import com.example.expensetracker.data.local.ExpenseTagCrossRef
import com.example.expensetracker.data.local.ExpenseTagCrossRefDao
import com.example.expensetracker.data.mapper.toEntity
import com.example.expensetracker.data.mapper.toExpense
import com.example.expensetracker.data.model.CategoryTotal
import com.example.expensetracker.data.model.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepository(
    private val database: ExpenseDatabase,
    private val expenseDao: ExpenseDao,
    private val crossRefDao: ExpenseTagCrossRefDao
) {


    suspend fun addExpense(expense: Expense) {
        database.withTransaction {

            val expenseId = expenseDao.insertExpense(
                expense.toEntity()
            ).toInt()

            val crossRefs = expense.tagIds.map { tagId ->
                ExpenseTagCrossRef(
                    expenseId = expenseId,
                    tagId = tagId
                )
            }

            crossRefDao.insertCrossRefs(crossRefs)
        }
    }

    suspend fun updateExpense(expense: Expense) {
        database.withTransaction {
            expenseDao.updateExpense(expense.toEntity())
            crossRefDao.deleteTagsForExpense(expense.id)

            val crossRefs = expense.tagIds.map { tagId ->
                ExpenseTagCrossRef(
                    expenseId = expense.id,
                    tagId = tagId
                )
            }
            crossRefDao.insertCrossRefs(crossRefs)
        }
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense.toEntity())

    }

    fun getExpenses(start: Long, end: Long): Flow<List<Expense>> {
        return expenseDao.getAllExpenses(start, end)
            .map { list ->
                list.map { it.toExpense() }
            }
    }

    fun getTotalExpensesThisMonth(
        startOfMonth: Long,
        endOfMonth: Long
    ): Flow<Double> {
        return expenseDao.getTotalExpensesThisMonth(startOfMonth, endOfMonth)
    }


    fun getCategoryWiseExpenses(
        startOfMonth: Long,
        endOfMonth: Long
    ): Flow<List<CategoryTotal>> {
        return expenseDao.getCategoryWiseExpenses(startOfMonth, endOfMonth)
    }

    suspend fun reassignAccount(oldAccountId: Int, newAccountId: Int) {
        expenseDao.reassignAccount(oldAccountId, newAccountId)
    }

    suspend fun getExpenseById(id: Int): Expense? {
        return expenseDao.getExpenseWithDetailsById(id)?.toExpense()
    }

    suspend fun getTotalExpensesForAccount(accountId: Int): Double {
        return expenseDao.getTotalExpensesForAccount(accountId) ?: 0.0
    }
}
