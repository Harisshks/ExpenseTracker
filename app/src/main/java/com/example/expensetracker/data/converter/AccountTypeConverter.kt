package com.example.expensetracker.data.converter


import androidx.room.TypeConverter
import com.example.expensetracker.data.model.AccountType

class AccountTypeConverter {

    @TypeConverter
    fun fromAccountType(type: AccountType): String {
        return type.name
    }

    @TypeConverter
    fun toAccountType(name: String): AccountType {
        return AccountType.fromName(name)
    }
}
