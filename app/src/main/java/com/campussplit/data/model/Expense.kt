package com.campussplit.data.model

/** Represents a single expense within a group. */
data class Expense(
    val id: Long,
    val groupId: Long,
    val title: String,
    val amount: Double,
    val paidByPersonId: Long?,     // null = unknown/unassigned
    val category: String = "General",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
