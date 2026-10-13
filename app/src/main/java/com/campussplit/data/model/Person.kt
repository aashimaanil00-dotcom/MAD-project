package com.campussplit.data.model

/** Represents a person who can be added to groups and participate in expense splits. */
data class Person(
    val id: Long,
    val name: String,
    val phone: String = "",
    val email: String = "",
    val avatarColor: Int = 0   // index into AvatarColors palette
)
