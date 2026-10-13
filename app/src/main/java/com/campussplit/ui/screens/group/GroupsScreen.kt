package com.campussplit.ui.screens.group

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campussplit.ui.components.EmptyState
import com.campussplit.ui.components.GroupCard
import com.campussplit.viewmodel.ExpenseViewModel
import com.campussplit.viewmodel.GroupViewModel
import com.campussplit.viewmodel.PersonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    groupVM: GroupViewModel,
    expenseVM: ExpenseViewModel,
    onBack: () -> Unit,
    onAddGroup: () -> Unit,
    onGroupClick: (Long) -> Unit,
    onEditGroup: (Long) -> Unit
) {
    val groups = groupVM.groups

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Groups", style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddGroup,
                icon = { Icon(Icons.Default.GroupAdd, null) },
                text = { Text("New Group") }
            )
        }
    ) { padding ->
        if (groups.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = Icons.Default.Groups,
                    title = "No groups yet",
                    subtitle = "Tap the button below to create your first group"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(groups, key = { it.id }) { group ->
                    GroupCard(
                        group = group,
                        totalAmount = expenseVM.getTotalForGroup(group.id),
                        memberCount = group.memberIds.size,
                        onClick = { onGroupClick(group.id) },
                        onEdit = { onEditGroup(group.id) },
                        onDelete = {
                            expenseVM.deleteExpensesForGroup(group.id)
                            groupVM.deleteGroup(group.id)
                        }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}
