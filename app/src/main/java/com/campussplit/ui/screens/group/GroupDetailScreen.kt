package com.campussplit.ui.screens.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campussplit.data.model.Group
import com.campussplit.ui.components.EmptyState
import com.campussplit.ui.components.ExpenseCard
import com.campussplit.ui.components.GroupIcon
import com.campussplit.ui.components.PersonAvatar
import com.campussplit.viewmodel.ExpenseViewModel
import com.campussplit.viewmodel.GroupViewModel
import com.campussplit.viewmodel.PersonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: Long,
    groupVM: GroupViewModel,
    expenseVM: ExpenseViewModel,
    personVM: PersonViewModel,
    onBack: () -> Unit,
    onAddExpense: () -> Unit,
    onEditExpense: (Long) -> Unit,
    onEditGroup: () -> Unit,
    onSettlements: () -> Unit
) {
    val group = groupVM.getGroupById(groupId)
    if (group == null) { onBack(); return }

    val members = personVM.getPeopleByIds(group.memberIds)
    val expenses = expenseVM.getExpensesForGroup(groupId)
    val total    = expenseVM.getTotalForGroup(groupId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(group.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                actions = {
                    IconButton(onClick = onEditGroup) { Icon(Icons.Default.Edit, "Edit") }
                    IconButton(onClick = onSettlements) { Icon(Icons.Default.AccountBalance, "Settle") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpense) {
                Icon(Icons.Default.Add, "Add Expense")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stats header
            item {
                GroupStatsHeader(group = group, total = total,
                    memberCount = members.size, expenseCount = expenses.size,
                    onSettlements = onSettlements)
            }

            // Members strip
            item {
                Text("Members", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                if (members.isEmpty()) {
                    Text("No members. Edit the group to add people.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        members.forEach { person ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                PersonAvatar(person = person, size = 42)
                                Spacer(Modifier.height(4.dp))
                                Text(person.name.split(" ").first(),
                                    style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            // Expense section header
            item {
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("Expenses", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    Text("${expenses.size} item${if (expenses.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (expenses.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.ReceiptLong,
                        title = "No expenses yet",
                        subtitle = "Tap + to add the first expense"
                    )
                }
            } else {
                items(expenses, key = { it.id }) { expense ->
                    val paidBy = personVM.getPersonById(expense.paidByPersonId ?: -1)
                    ExpenseCard(
                        expense = expense,
                        paidByPerson = paidBy,
                        onEdit   = { onEditExpense(expense.id) },
                        onDelete = { expenseVM.deleteExpense(expense.id) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun GroupStatsHeader(
    group: Group, total: Double, memberCount: Int, expenseCount: Int,
    onSettlements: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(listOf(Color(0xFF6750A4), Color(0xFF0077B6))),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GroupIcon(iconIndex = group.iconIndex, size = 48)
                Column {
                    Text(group.name, style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold, color = Color.White)
                    if (group.description.isNotBlank())
                        Text(group.description, style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f))
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                StatBlock("Total", "₹${String.format("%.2f", total)}")
                StatBlock("Expenses", "$expenseCount")
                StatBlock("Members", "$memberCount")
            }
            Spacer(Modifier.height(14.dp))
            OutlinedButton(
                onClick = onSettlements,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(listOf(Color.White, Color.White))
                )
            ) {
                Icon(Icons.Default.AccountBalance, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("View Settlements", color = Color.White)
            }
        }
    }
}

@Composable
private fun StatBlock(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold, color = Color.White)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f))
    }
}
