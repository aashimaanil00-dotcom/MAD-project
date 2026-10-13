package com.campussplit.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campussplit.data.model.Group
import com.campussplit.ui.components.GroupIcon
import com.campussplit.viewmodel.ExpenseViewModel
import com.campussplit.viewmodel.GroupViewModel
import com.campussplit.viewmodel.PersonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    personVM: PersonViewModel,
    groupVM: GroupViewModel,
    expenseVM: ExpenseViewModel,
    onNavigateToPeople: () -> Unit,
    onNavigateToGroups: () -> Unit,
    onGroupClick: (Long) -> Unit
) {
    val people = personVM.people
    val groups = groupVM.groups

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "CampusSplit",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Split campus expenses 🎓",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero banner
            item { HeroBanner(peopleCount = people.size, groupCount = groups.size) }

            // Quick actions
            item {
                Text("Quick Actions", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        icon   = Icons.Default.People,
                        title  = "People",
                        count  = "${people.size} contacts",
                        color  = MaterialTheme.colorScheme.primaryContainer,
                        iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        onClick = onNavigateToPeople,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        icon   = Icons.Default.Group,
                        title  = "Groups",
                        count  = "${groups.size} groups",
                        color  = MaterialTheme.colorScheme.secondaryContainer,
                        iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        onClick = onNavigateToGroups,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Recent groups
            if (groups.isNotEmpty()) {
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Recent Groups", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold)
                        TextButton(onClick = onNavigateToGroups) { Text("See all") }
                    }
                }
                items(groups.takeLast(4).reversed()) { group ->
                    RecentGroupItem(
                        group = group,
                        total = expenseVM.getTotalForGroup(group.id),
                        memberCount = group.memberIds.size,
                        onClick = { onGroupClick(group.id) }
                    )
                }
            } else {
                item { EmptyHomeState(onNavigateToPeople, onNavigateToGroups) }
            }
        }
    }
}

@Composable
private fun HeroBanner(peopleCount: Int, groupCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(listOf(Color(0xFF6750A4), Color(0xFF0077B6)))
            )
            .padding(20.dp)
    ) {
        Column {
            Text("Track & Split", style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("Campus expenses made simple", style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f))
        }
        Row(
            modifier = Modifier.align(Alignment.BottomEnd),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$peopleCount", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text("People", style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$groupCount", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold, color = Color.White)
                Text("Groups", style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector, title: String, count: String,
    color: Color, iconColor: Color, onClick: () -> Unit, modifier: Modifier
) {
    Card(
        modifier = modifier.height(110.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(32.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = iconColor)
                Text(count, style = MaterialTheme.typography.bodySmall,
                    color = iconColor.copy(alpha = 0.75f))
            }
        }
    }
}

@Composable
private fun RecentGroupItem(
    group: Group, total: Double, memberCount: Int, onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GroupIcon(iconIndex = group.iconIndex, size = 44)
            Column(modifier = Modifier.weight(1f)) {
                Text(group.name, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)
                Text("$memberCount member${if (memberCount != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (total > 0) {
                Text("₹${String.format("%.0f", total)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary)
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun EmptyHomeState(onAddPeople: () -> Unit, onAddGroup: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Groups, null, modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Text("Welcome to CampusSplit!", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Add people first, then create groups and track shared expenses.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(onClick = onAddPeople) {
                    Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add People")
                }
                FilledTonalButton(onClick = onAddGroup) {
                    Icon(Icons.Default.GroupAdd, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Group")
                }
            }
        }
    }
}
