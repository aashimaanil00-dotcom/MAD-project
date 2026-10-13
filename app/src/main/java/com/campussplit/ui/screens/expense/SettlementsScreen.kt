package com.campussplit.ui.screens.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campussplit.data.model.PersonBalance
import com.campussplit.data.model.Settlement
import com.campussplit.ui.components.PersonAvatar
import com.campussplit.viewmodel.ExpenseViewModel
import com.campussplit.viewmodel.GroupViewModel
import com.campussplit.viewmodel.PersonViewModel
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettlementsScreen(
    groupId: Long,
    groupVM: GroupViewModel,
    expenseVM: ExpenseViewModel,
    personVM: PersonViewModel,
    onBack: () -> Unit
) {
    val group = groupVM.getGroupById(groupId)
    if (group == null) { onBack(); return }
    val members = personVM.getPeopleByIds(group.memberIds)

    val balances    = remember(members, expenseVM.expenses) {
        expenseVM.calculateBalances(members, groupId)
    }
    val settlements = remember(balances) {
        expenseVM.calculateSettlements(balances)
    }
    val totalToSettle = balances.filter { it.netBalance > 0 }.sumOf { it.netBalance }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Settlements", fontWeight = FontWeight.Bold)
                        Text(group.name, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF2D6A4F), Color(0xFF1B7A6E))),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp)
                ) {
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Total to Settle", style = MaterialTheme.typography.labelLarge,
                                color = Color.White.copy(alpha = 0.8f))
                            Text("₹${String.format("%.2f", totalToSettle)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                        Icon(Icons.Default.AccountBalance, null,
                            tint = Color.White, modifier = Modifier.size(48.dp))
                    }
                }
            }

            // Balances
            item {
                Text("Individual Balances", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
            }

            if (balances.isEmpty()) {
                item {
                    Card(colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Box(Modifier.fillMaxWidth().padding(24.dp),
                            contentAlignment = Alignment.Center) {
                            Text("No members or expenses found.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(balances) { b -> BalanceCard(b) }
            }

            // Settlements
            item {
                Spacer(Modifier.height(4.dp))
                Text("Suggested Payments", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
            }

            if (balances.isNotEmpty() && settlements.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(32.dp))
                            Text("Everyone is settled up! 🎉",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
            } else {
                items(settlements) { s -> SettlementCard(s) }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun BalanceCard(balance: PersonBalance) {
    val positive = balance.netBalance > 0.01
    val negative = balance.netBalance < -0.01
    val color = when {
        positive -> Color(0xFF2D6A4F)
        negative -> Color(0xFFD62828)
        else     -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PersonAvatar(person = balance.person)
            Column(Modifier.weight(1f)) {
                Text(balance.person.name, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)
                Text(
                    when {
                        positive -> "Gets back"
                        negative -> "Owes"
                        else     -> "Settled"
                    },
                    style = MaterialTheme.typography.bodySmall, color = color
                )
            }
            Text("₹${String.format("%.2f", abs(balance.netBalance))}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun SettlementCard(s: Settlement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PersonAvatar(person = s.from, size = 40)
            Column(Modifier.weight(1f)) {
                Text(s.from.name, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)
                Text("pays", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("₹${String.format("%.2f", s.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary)
            Icon(Icons.Default.ArrowForward, null,
                tint = MaterialTheme.colorScheme.outline)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PersonAvatar(person = s.to, size = 40)
                Spacer(Modifier.height(2.dp))
                Text(s.to.name.split(" ").first(), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
