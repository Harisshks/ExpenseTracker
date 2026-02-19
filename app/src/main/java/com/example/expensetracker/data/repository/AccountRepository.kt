package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.AccountDao
import com.example.expensetracker.data.mapper.toAccount
import com.example.expensetracker.data.mapper.toEntity
import com.example.expensetracker.data.model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountRepository(
    private val accountDao: AccountDao
) {

    fun getAllAccounts(): Flow<List<Account>> {
        return accountDao.getAllAccounts()
            .map { list ->
                list.map { it.toAccount() }
            }
    }

    suspend fun addAccount(account: Account) {
        accountDao.insertAccount(account.toEntity())
    }

    suspend fun updateAccount(account: Account) {
        accountDao.updateAccount(account.toEntity())
    }

    suspend fun deleteAccount(account: Account) {
        accountDao.deleteAccount(account.toEntity())
    }

    suspend fun getDefaultAccount(): Account? {
        return accountDao.getDefaultAccount()?.toAccount()
    }

    suspend fun getExpenseCount(accountId: Int): Int {
        return accountDao.getExpenseCountForAccount(accountId)
    }

    suspend fun getAccountById(id: Int): Account? {
        return accountDao.getAccountById(id)?.toAccount()
    }

}

