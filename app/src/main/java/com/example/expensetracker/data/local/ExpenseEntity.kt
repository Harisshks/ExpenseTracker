package com.example.expensetracker.data.local


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation


@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("categoryId"),
        Index("accountId")
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val date: Long,
    val categoryId: Int,
    val accountId: Int
)


data class ExpenseWithDetails(

    @Embedded val expense: ExpenseEntity,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity,

    @Relation(
        parentColumn = "accountId",
        entityColumn = "id"
    )
    val account: AccountEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ExpenseTagCrossRef::class,
            parentColumn = "expenseId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
)
