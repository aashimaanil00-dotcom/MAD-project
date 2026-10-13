package com.campussplit.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.campussplit.data.model.Expense
import com.campussplit.data.model.Person
import com.campussplit.data.model.PersonBalance
import com.campussplit.data.model.Settlement

/**
 * Holds all expenses in-memory using Compose snapshot state.
 * Expenses are keyed by groupId.
 */
class ExpenseViewModel : ViewModel() {

    private var nextId = 1L

    private val _expenses = mutableStateListOf<Expense>()
    val expenses: List<Expense> get() = _expenses

    // ── CRUD ──────────────────────────────────────────────────────────────────

    fun addExpense(
        groupId: Long,
        title: String,
        amount: Double,
        paidByPersonId: Long?,
        category: String = "General",
        note: String = ""
    ) {
        _expenses.add(
            Expense(
                id = nextId++,
                groupId = groupId,
                title = title.trim(),
                amount = amount,
                paidByPersonId = paidByPersonId,
                category = category,
                note = note.trim()
            )
        )
    }

    fun updateExpense(updated: Expense) {
        val idx = _expenses.indexOfFirst { it.id == updated.id }
        if (idx != -1) _expenses[idx] = updated
    }

    fun deleteExpense(expenseId: Long) {
        _expenses.removeAll { it.id == expenseId }
    }

    /** Delete all expenses that belong to [groupId] — called when a group is deleted. */
    fun deleteExpensesForGroup(groupId: Long) {
        _expenses.removeAll { it.groupId == groupId }
    }

    fun getExpenseById(id: Long): Expense? = _expenses.find { it.id == id }

    // ── Query helpers ─────────────────────────────────────────────────────────

    fun getExpensesForGroup(groupId: Long): List<Expense> =
        _expenses.filter { it.groupId == groupId }.sortedByDescending { it.createdAt }

    fun getTotalForGroup(groupId: Long): Double =
        _expenses.filter { it.groupId == groupId }.sumOf { it.amount }

    // ── Balance & Settlement calculation ──────────────────────────────────────

    /**
     * Calculates each member's net balance for all expenses in a group.
     *  Positive = person is owed money.
     *  Negative = person owes money.
     */
    fun calculateBalances(members: List<Person>, groupId: Long): List<PersonBalance> {
        if (members.isEmpty()) return emptyList()
        val groupExpenses = getExpensesForGroup(groupId)
        val balanceMap = mutableMapOf<Long, Double>()
        members.forEach { balanceMap[it.id] = 0.0 }

        groupExpenses.forEach { expense ->
            val sharePerPerson = expense.amount / members.size
            // Payer gets credited the full amount
            expense.paidByPersonId?.let { payerId ->
                if (payerId in balanceMap) {
                    balanceMap[payerId] = (balanceMap[payerId] ?: 0.0) + expense.amount
                }
            }
            // Everyone gets debited their share
            members.forEach { member ->
                balanceMap[member.id] = (balanceMap[member.id] ?: 0.0) - sharePerPerson
            }
        }

        return members.map { person ->
            PersonBalance(person = person, netBalance = balanceMap[person.id] ?: 0.0)
        }.sortedByDescending { it.netBalance }
    }

    /**
     * Calculates minimum number of transactions to settle all debts.
     * Uses a greedy two-pointer approach on sorted creditors / debtors.
     */
    fun calculateSettlements(balances: List<PersonBalance>): List<Settlement> {
        val settlements = mutableListOf<Settlement>()
        val creditors = balances.filter { it.netBalance > 0.01 }
            .map { it.person to it.netBalance }.toMutableList()
        val debtors = balances.filter { it.netBalance < -0.01 }
            .map { it.person to -it.netBalance }.toMutableList()

        var ci = 0
        var di = 0
        while (ci < creditors.size && di < debtors.size) {
            val (creditor, creditAmt) = creditors[ci]
            val (debtor, debtAmt) = debtors[di]
            val amount = minOf(creditAmt, debtAmt)
            settlements.add(Settlement(from = debtor, to = creditor, amount = amount))
            creditors[ci] = creditor to (creditAmt - amount)
            debtors[di] = debtor to (debtAmt - amount)
            if (creditors[ci].second < 0.01) ci++
            if (debtors[di].second < 0.01) di++
        }
        return settlements
    }
}
