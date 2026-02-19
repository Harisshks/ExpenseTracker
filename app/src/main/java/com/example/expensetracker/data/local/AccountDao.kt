package com.example.expensetracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertAccount(account: AccountEntity): Long

        @Update
        suspend fun updateAccount(account: AccountEntity)

        @Delete
        suspend fun deleteAccount(account: AccountEntity)

        @Query("SELECT * FROM accounts ORDER BY id DESC")
        fun getAllAccounts(): Flow<List<AccountEntity>>

        @Query("SELECT * FROM accounts WHERE id = :id")
        suspend fun getAccountById(id: Int): AccountEntity?

         @Query("SELECT * FROM accounts WHERE isSystem = 1 LIMIT 1")
        suspend fun getDefaultAccount(): AccountEntity?

         @Query("SELECT COUNT(*) FROM expenses WHERE accountId = :accountId")
        suspend fun getExpenseCountForAccount(accountId: Int): Int
    }

