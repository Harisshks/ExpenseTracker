package com.example.expensetracker.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.expensetracker.data.model.AccountType


@Entity(
    tableName = "accounts",
    indices = [Index(value = ["name"], unique = true)]
)
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: AccountType,
    val balance: Double,
    val isSystem: Boolean = false
)
