
package com.example.expensetracker.data.local

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.expensetracker.data.converter.AccountTypeConverter
import com.example.expensetracker.data.mapper.IconMapper
import com.example.expensetracker.data.model.defaultCategories
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
@Database(
    entities = [
        ExpenseEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        AccountEntity::class,
        TagEntity::class,
        ExpenseTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(AccountTypeConverter::class)
abstract class ExpenseDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun accountDao(): AccountDao
    abstract fun tagDao(): TagDao
    abstract fun expenseTagCrossRefDao(): ExpenseTagCrossRefDao

    companion object {

        @Volatile
        private var INSTANCE: ExpenseDatabase? = null

        fun getDatabase(context: Context): ExpenseDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseDatabase::class.java,
                    "expense_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL("""
            INSERT OR IGNORE INTO accounts (name, type, balance, isSystem)
            VALUES ('Cash', 'CASH', 0, 1)
        """)
                            defaultCategories.forEach { category ->
                                val iconName = IconMapper.getIconName(category.icon)
                                db.execSQL("""
                INSERT OR IGNORE INTO categories (id, name, color, icon)
                VALUES (${category.id}, '${category.name}', ${category.color}, '$iconName')
            """)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
