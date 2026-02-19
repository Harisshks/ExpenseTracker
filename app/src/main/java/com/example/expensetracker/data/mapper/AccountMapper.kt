package com.example.expensetracker.data.mapper

import com.example.expensetracker.data.local.AccountEntity
import com.example.expensetracker.data.model.Account

fun AccountEntity.toAccount(): Account {
    return Account(
        id = id,
        name = name,
        type = type,
        balance = balance,
        isSystem = isSystem
    )
}

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        name = name,
        type = type,
        balance = balance,
        isSystem = isSystem
    )
}
