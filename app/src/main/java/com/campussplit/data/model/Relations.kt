package com.campussplit.data.model

/** Net balance for one person in a group. Positive = owed money. Negative = owes money. */
data class PersonBalance(
    val person: Person,
    val netBalance: Double
)

/** A suggested payment: [from] pays [amount] to [to]. */
data class Settlement(
    val from: Person,
    val to: Person,
    val amount: Double
)
