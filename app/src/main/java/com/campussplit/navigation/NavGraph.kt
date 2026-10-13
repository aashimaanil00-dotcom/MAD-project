package com.campussplit.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.campussplit.ui.screens.expense.AddExpenseScreen
import com.campussplit.ui.screens.expense.EditExpenseScreen
import com.campussplit.ui.screens.expense.SettlementsScreen
import com.campussplit.ui.screens.group.AddGroupScreen
import com.campussplit.ui.screens.group.EditGroupScreen
import com.campussplit.ui.screens.group.GroupDetailScreen
import com.campussplit.ui.screens.group.GroupsScreen
import com.campussplit.ui.screens.home.HomeScreen
import com.campussplit.ui.screens.people.AddPersonScreen
import com.campussplit.ui.screens.people.EditPersonScreen
import com.campussplit.ui.screens.people.PeopleScreen
import com.campussplit.viewmodel.ExpenseViewModel
import com.campussplit.viewmodel.GroupViewModel
import com.campussplit.viewmodel.PersonViewModel

@Composable
fun CampusSplitNavGraph() {
    val navController = rememberNavController()
    // ViewModels are scoped to the NavGraph and shared across all composables
    val personVM: PersonViewModel = viewModel()
    val groupVM: GroupViewModel   = viewModel()
    val expenseVM: ExpenseViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(
                personVM = personVM,
                groupVM = groupVM,
                expenseVM = expenseVM,
                onNavigateToPeople = { navController.navigate(Screen.People.route) },
                onNavigateToGroups = { navController.navigate(Screen.Groups.route) },
                onGroupClick = { navController.navigate(Screen.GroupDetail.route(it)) }
            )
        }

        // ── People ────────────────────────────────────────────────────────────
        composable(Screen.People.route) {
            PeopleScreen(
                personVM = personVM,
                onBack = { navController.popBackStack() },
                onAddPerson = { navController.navigate(Screen.AddPerson.route) },
                onEditPerson = { navController.navigate(Screen.EditPerson.route(it)) }
            )
        }

        composable(Screen.AddPerson.route) {
            AddPersonScreen(
                personVM = personVM,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Screen.EditPerson.route,
            arguments = listOf(navArgument("personId") { type = NavType.LongType })
        ) {
            val id = it.arguments!!.getLong("personId")
            EditPersonScreen(
                personId = id,
                personVM = personVM,
                onBack = { navController.popBackStack() }
            )
        }

        // ── Groups ────────────────────────────────────────────────────────────
        composable(Screen.Groups.route) {
            GroupsScreen(
                groupVM = groupVM,
                expenseVM = expenseVM,
                onBack = { navController.popBackStack() },
                onAddGroup = { navController.navigate(Screen.AddGroup.route) },
                onGroupClick = { navController.navigate(Screen.GroupDetail.route(it)) },
                onEditGroup = { navController.navigate(Screen.EditGroup.route(it)) }
            )
        }

        composable(Screen.AddGroup.route) {
            AddGroupScreen(
                groupVM = groupVM,
                personVM = personVM,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Screen.EditGroup.route,
            arguments = listOf(navArgument("groupId") { type = NavType.LongType })
        ) {
            val id = it.arguments!!.getLong("groupId")
            EditGroupScreen(
                groupId = id,
                groupVM = groupVM,
                personVM = personVM,
                onBack = { navController.popBackStack() }
            )
        }

        // ── Group Detail ──────────────────────────────────────────────────────
        composable(
            Screen.GroupDetail.route,
            arguments = listOf(navArgument("groupId") { type = NavType.LongType })
        ) {
            val id = it.arguments!!.getLong("groupId")
            GroupDetailScreen(
                groupId = id,
                groupVM = groupVM,
                expenseVM = expenseVM,
                personVM = personVM,
                onBack = { navController.popBackStack() },
                onAddExpense = { navController.navigate(Screen.AddExpense.route(id)) },
                onEditExpense = { expId -> navController.navigate(Screen.EditExpense.route(expId, id)) },
                onEditGroup = { navController.navigate(Screen.EditGroup.route(id)) },
                onSettlements = { navController.navigate(Screen.Settlements.route(id)) }
            )
        }

        // ── Expenses ──────────────────────────────────────────────────────────
        composable(
            Screen.AddExpense.route,
            arguments = listOf(navArgument("groupId") { type = NavType.LongType })
        ) {
            val groupId = it.arguments!!.getLong("groupId")
            AddExpenseScreen(
                groupId = groupId,
                groupVM = groupVM,
                expenseVM = expenseVM,
                personVM = personVM,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Screen.EditExpense.route,
            arguments = listOf(
                navArgument("expenseId") { type = NavType.LongType },
                navArgument("groupId")   { type = NavType.LongType }
            )
        ) {
            val expenseId = it.arguments!!.getLong("expenseId")
            val groupId   = it.arguments!!.getLong("groupId")
            EditExpenseScreen(
                expenseId = expenseId,
                groupId   = groupId,
                groupVM   = groupVM,
                expenseVM = expenseVM,
                personVM  = personVM,
                onBack = { navController.popBackStack() }
            )
        }

        // ── Settlements ───────────────────────────────────────────────────────
        composable(
            Screen.Settlements.route,
            arguments = listOf(navArgument("groupId") { type = NavType.LongType })
        ) {
            val groupId = it.arguments!!.getLong("groupId")
            SettlementsScreen(
                groupId   = groupId,
                groupVM   = groupVM,
                expenseVM = expenseVM,
                personVM  = personVM,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
