package com.campussplit.data.model

/** Represents a group of people who share expenses together. */
data class Group(
    val id: Long,
    val name: String,
    val description: String = "",
    val iconIndex: Int = 0,        // index into GroupIcons list
    val memberIds: List<Long> = emptyList()
)
