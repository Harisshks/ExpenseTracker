package com.example.expensetracker.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.mapper.toCategory
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.CategoryTotal
import com.example.expensetracker.data.model.Expense
import com.example.expensetracker.data.model.Tag
import com.example.expensetracker.data.repository.AccountRepository
import com.example.expensetracker.data.repository.BudgetRepository
import com.example.expensetracker.data.repository.CategoryRepository
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.data.repository.TagRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar


sealed class DeleteAccountEvent {
    object SystemAccount : DeleteAccountEvent()
    data class InUse(val account: Account) : DeleteAccountEvent()
    object Success : DeleteAccountEvent()
}

class HomeViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val tagRepository: TagRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {


    private val _selectedCategory = MutableStateFlow("All")
    private val _selectedMonth = MutableStateFlow(getCurrentMonthRange())

    val selectedCategory: StateFlow<String> = _selectedCategory
    val selectedMonth: StateFlow<Pair<Long, Long>> = _selectedMonth

    private val _searchQuery = MutableStateFlow("")
    private val _selectedTags = MutableStateFlow<Set<Int>>(emptySet())

    val searchQuery: StateFlow<String> = _searchQuery
    val selectedTags: StateFlow<Set<Int>> = _selectedTags


    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleTag(tagId: Int) {
        _selectedTags.value =
            if (_selectedTags.value.contains(tagId))
                _selectedTags.value - tagId
            else
                _selectedTags.value + tagId
    }



    val homeExpenses: StateFlow<List<Expense>> =
        combine(
            _selectedMonth.flatMapLatest { (start, end) ->
                expenseRepository.getExpenses(start, end)
            },
            _selectedCategory
        ) { list, selectedCategory ->

            list.filter {
                selectedCategory == "All" ||
                        it.categoryName == selectedCategory
            }
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    val searchExpenses: StateFlow<List<Expense>> =
        combine(
            homeExpenses,
            _searchQuery,
            _selectedTags
        ) { list, query, selectedTags ->

            list
                .filter {
                    query.isBlank() ||
                            it.title.contains(query, ignoreCase = true)
                }
                .filter { expense ->
                    selectedTags.isEmpty() ||
                            selectedTags.all { tagId ->
                                expense.tagIds.contains(tagId)
                            }
                }
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )


    private val _deleteAccountEvent = MutableSharedFlow<DeleteAccountEvent>()
    val deleteAccountEvent = _deleteAccountEvent.asSharedFlow()

// Account

    val accounts: StateFlow<List<Account>> =
        accountRepository.getAllAccounts()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addAccount(
        name: String,
        balance: Double = 0.0,
        type: AccountType
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            accountRepository.addAccount(
                Account(
                    name = name,
                    type = type,
                    balance = balance,
                    isSystem = false
                )
            )
        }
    }

    fun updateAccount(account: Account) {
        viewModelScope.launch(Dispatchers.IO) {
            accountRepository.updateAccount(account)
        }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch(Dispatchers.IO) {

            if (account.isSystem) {
                _deleteAccountEvent.emit(DeleteAccountEvent.SystemAccount)
                return@launch
            }

            val usageCount =
                accountRepository.getExpenseCount(account.id)

            if (usageCount > 0) {
                _deleteAccountEvent.emit(
                    DeleteAccountEvent.InUse(account)
                )
                return@launch
            }

            accountRepository.deleteAccount(account)
            _deleteAccountEvent.emit(DeleteAccountEvent.Success)
        }
    }


    fun forceDeleteAndReassign(account: Account) {
        viewModelScope.launch(Dispatchers.IO) {

            val defaultAccount =
                accountRepository.getDefaultAccount()
                    ?: return@launch

            val totalExpenses =
                expenseRepository.getTotalExpensesForAccount(account.id)

            val updatedDefault = defaultAccount.copy(
                balance = defaultAccount.balance - totalExpenses
            )

            accountRepository.updateAccount(updatedDefault)

            expenseRepository.reassignAccount(
                oldAccountId = account.id,
                newAccountId = defaultAccount.id
            )
            accountRepository.deleteAccount(account)

            _deleteAccountEvent.emit(DeleteAccountEvent.Success)
        }
    }

    // Category

    val categories: StateFlow<List<Category>> =
        categoryRepository.getAllCategories()
            .map { entities ->
                val mapped = entities.map { it.toCategory() }
                listOf(Category(-1, "All", 0, Icons.Default.List)) + mapped
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

//Tag

    val tags: StateFlow<List<Tag>> =
        tagRepository.getAllTags()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun addTag(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            tagRepository.addTag(
                Tag(
                    id = 0,
                    name = name
                )
            )
        }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            tagRepository.deleteTag(tag)
        }
    }

    // Expense

    fun addExpense(
        title: String,
        amount: Double,
        category: Category,
        accountId: Int,
        tagIds: List<Int>,
        date: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val expense = Expense(
                title = title,
                amount = amount,
                date = date,
                categoryId = category.id,
                categoryName = category.name,
                categoryColor = category.color,
                categoryIcon = category.icon,
                tagIds = tagIds,
                accountId = accountId
            )
            expenseRepository.addExpense(expense)
            val account = accountRepository.getAccountById(accountId)
            account?.let {
                accountRepository.updateAccount(
                    it.copy(balance = it.balance - amount)
                )
            }
        }
    }
    fun updateExpense(updatedExpense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {

            val oldExpense = expenseRepository.getExpenseById(updatedExpense.id)
                ?: return@launch
            val oldAccount = accountRepository.getAccountById(oldExpense.accountId)
            oldAccount?.let {
                accountRepository.updateAccount(
                    it.copy(balance = it.balance + oldExpense.amount)
                )
            }
            val newAccount = accountRepository.getAccountById(updatedExpense.accountId)
            newAccount?.let {
                accountRepository.updateAccount(
                    it.copy(balance = it.balance - updatedExpense.amount)
                )
            }
            expenseRepository.updateExpense(updatedExpense)
        }
    }
    fun deleteExpense(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            val account = accountRepository.getAccountById(expense.accountId)
            account?.let {
                accountRepository.updateAccount(
                    it.copy(balance = it.balance + expense.amount)
                )
            }
            expenseRepository.deleteExpense(expense)
        }
    }


    fun getExpenseById(id: Int): Expense? =
        homeExpenses.value.find { it.id == id }


    val monthTotal: StateFlow<Double> =
        _selectedMonth.flatMapLatest { (start, end) ->
            expenseRepository.getTotalExpensesThisMonth(start, end)
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0.0
            )



    val categoryTotals: StateFlow<List<CategoryTotal>> =
        _selectedMonth.flatMapLatest { (start, end) ->
            expenseRepository.getCategoryWiseExpenses(start, end)
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    //Budget

    val budget: StateFlow<Budget?> =
        _selectedMonth.flatMapLatest { (start, _) ->
            budgetRepository.getBudget(normalizeMonthStart(start))
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val remainingBudget: StateFlow<Double> =
        combine(budget, monthTotal) { budget, spent ->
            if (budget == null) 0.0
            else budget.amount - spent
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    fun setBudget(amount: Double) {
        val normalizedStart =
            normalizeMonthStart(_selectedMonth.value.first)

        viewModelScope.launch(Dispatchers.IO) {
            budgetRepository.setBudget(
                Budget(
                    monthStart = normalizedStart,
                    amount = amount
                )
            )
        }
    }



    fun setCategory(categoryName: String) {
        _selectedCategory.value = categoryName
    }

    fun changeMonth(start: Long, end: Long) {
        _selectedMonth.value = start to end
    }



    private fun getCurrentMonthRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val start = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val end = calendar.timeInMillis

        return start to end
    }

    private fun normalizeMonthStart(time: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = time
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}
