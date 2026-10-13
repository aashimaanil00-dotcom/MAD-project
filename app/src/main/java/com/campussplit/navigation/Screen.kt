package com.campussplit.navigation

sealed class Screen(val route: String) {
    object Home       : Screen("home")
    object People     : Screen("people")
    object AddPerson  : Screen("add_person")
    object Groups     : Screen("groups")
    object AddGroup   : Screen("add_group")

    object EditPerson : Screen("edit_person/{personId}") {
        fun route(personId: Long) = "edit_person/$personId"
    }
    object EditGroup : Screen("edit_group/{groupId}") {
        fun route(groupId: Long) = "edit_group/$groupId"
    }
    object GroupDetail : Screen("group_detail/{groupId}") {
        fun route(groupId: Long) = "group_detail/$groupId"
    }
    object AddExpense : Screen("add_expense/{groupId}") {
        fun route(groupId: Long) = "add_expense/$groupId"
    }
    object EditExpense : Screen("edit_expense/{expenseId}/{groupId}") {
        fun route(expenseId: Long, groupId: Long) = "edit_expense/$expenseId/$groupId"
    }
    object Settlements : Screen("settlements/{groupId}") {
        fun route(groupId: Long) = "settlements/$groupId"
    }
}
